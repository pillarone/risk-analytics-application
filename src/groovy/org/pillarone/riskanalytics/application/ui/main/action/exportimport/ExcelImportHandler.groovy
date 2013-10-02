package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import org.apache.poi.POIXMLProperties
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.codehaus.plexus.interpolation.util.StringUtils
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.components.IResource
import org.pillarone.riskanalytics.core.components.ResourceHolder
import org.pillarone.riskanalytics.core.example.component.ExampleResource
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.IComboBoxBasedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class ExcelImportHandler extends AbstractExcelHandler {

    ExcelImportHandler(File excelParameterization) {
        super(excelParameterization)
    }

    /**
     * Returns a list of invalid lines
     * @return
     */
    List<ImportResult> validate() {
        List<ImportResult> result = []
        XSSFSheet sheet = workbook.getSheet(META_INFO_SHEET)
        if (!sheet){
            result << new ImportResult("Excel File does not contain mandatory sheet '$META_INFO_SHEET'", ImportResult.Type.ERROR)
        }else {
            if (!findModelName()){
                result << new ImportResult("Excel File does not contain mandatory sheet '$META_INFO_SHEET'", ImportResult.Type.ERROR)
            }
        }
        return result
    }

    List<ImportResult> process() {
        List<ImportResult> result = []
        Model model = getModel()
        model.init()
        model.injectComponentNames()
        model.allComponents.each { Component component ->
            Sheet sheet = findSheetForComponent(component)
            result.addAll(handleComponent(component, sheet, DATA_ROW_START_INDEX, 0))
        }
        modelInstance = model
        return result

    }

    List<ImportResult> handleComponent(Component component, Sheet sheet, int rowIndex, int columnStartIndex) {
        List<ImportResult> result = []
        Row dataRow = sheet.getRow(rowIndex)
        getAllParms(component).each { String paramName ->
            Integer columnIndex = findParameterColumnIndex(sheet, paramName, columnStartIndex)
            if (dataRow && columnIndex != null) {
                Cell cell = dataRow.getCell(columnIndex)
                if (cell) {
                    def paramType = component[paramName]
                    component[paramName] = toType(paramType, cell)
                }
            }
        }
        return result
    }

    def toType(Enum objectClass, Cell cell) {
        return objectClass.class.valueOf(cell.stringCellValue)
    }

    def toType(ComboBoxTableMultiDimensionalParameter objectClass, Cell cell) {
        objectClass.setValueAt(cell.stringCellValue, 1,0)
        return objectClass
    }

    def toType(ConstrainedString objectClass, Cell cell) {
        objectClass.setStringValue(cell.stringCellValue)
        return objectClass
    }

    def toType(IParameterObject objectClass, Cell cell) {
        AbstractParameterObjectClassifier classifier = objectClass.type.class."${cell.stringCellValue}"
        Map parameters = [:]
        classifier.getParameterNames().each { String parameterName ->
            int parameterColumnIndex = findColumnIndex(cell.sheet, parameterName, cell.columnIndex)
            Cell parameterCell = cell.row.getCell(parameterColumnIndex)
            if (parameterCell){
                parameters.put(parameterName, toType(classifier.parameters[parameterName], cell.row.getCell(parameterColumnIndex)))
            }
        }
        return classifier.getParameterObject(parameters)
    }

    def toType(ConstrainedMultiDimensionalParameter mdp, Cell cell) {
        def mdpSheet = findMdpSheet(cell)
        def tableName = cell.stringCellValue
        int tableColumnIndex = findColumnIndex(mdpSheet, tableName, 0)
        List<List> values = []
        mdp.valueColumnCount.times {
            values << []
        }
        (DATA_ROW_START_INDEX..mdpSheet.lastRowNum).each { int rowIndex ->
            Row row = mdpSheet.getRow(rowIndex)
            if (row){
                for (int columnIndex = tableColumnIndex; columnIndex < tableColumnIndex + mdp.valueColumnCount; columnIndex++) {
                    Cell dataCell = row.getCell(columnIndex)
                    if (dataCell) {
                        values[columnIndex - tableColumnIndex] << toType(newInstance(mdp.constraints.getColumnType(columnIndex - tableColumnIndex)), dataCell)
                    }
                }
            }
        }
        return new ConstrainedMultiDimensionalParameter(values, mdp.titles, mdp.constraints)
    }

    def newInstance(Class clazz){
        switch (clazz){
            case Integer:
                return new Integer(0)
            case Double:
                return new Double(0)
            default:
                return clazz.newInstance()
        }
    }

    def toType(Integer objectClass, Cell cell) {
        return cell.getNumericCellValue() as Integer
    }

    def toType(Double objectClass, Cell cell) {
        return cell.getNumericCellValue() as Double
    }

    def toType(DateTime objectClass, Cell cell) {
        return new DateTime(cell.getDateCellValue().time)
    }

    def toType(Boolean objectClass, Cell cell) {
        return Boolean.parseBoolean(cell.stringCellValue)
    }

    def toType(IResource resource, Cell cell) {
        String value = cell.stringCellValue
        String[] values = value.split(" v")
        return new ResourceHolder(resource.class, values[0], new VersionNumber(values[1]))
    }

    def toType(def objectClass, Cell cell) {
        return cell.getStringCellValue()
    }

    List<ImportResult> handleComponent(DynamicComposedComponent component, Sheet sheet, int rowIndex, int columnStartIndex) {
        List<ImportResult> result = []
        result.addAll(handleComponent(component as Component, sheet, DATA_ROW_START_INDEX, columnStartIndex))
        for (int rowIdx = rowIndex; rowIdx <= sheet.lastRowNum; rowIdx++) {
            Row row = sheet.getRow(rowIdx)
            String componentName = row.getCell(findColumnIndex(sheet, COMPONENT_HEADER_NAME, columnStartIndex))
            if (componentName && importEnabled(row,columnStartIndex)){
                Component subComponent = component.createDefaultSubComponent()
                componentName = componentName.replaceAll(' ','')
                subComponent.setName("sub${StringUtils.capitalizeFirstLetter(componentName)}")
                component.addSubComponent(subComponent)
                handleComponent(subComponent, sheet, rowIdx, columnStartIndex)
                result << new ImportResult(sheet.sheetName, rowIdx, "$componentName processed", ImportResult.Type.SUCCESS)
            }
        }
        return result
    }

    List<ImportResult> handleComponent(ComposedComponent component, Sheet sheet, int rowIndex, int columnStartIndex) {
        List<ImportResult> result = []
        result.addAll(handleComponent(component as Component, sheet, rowIndex, columnStartIndex))
        for (Component subComponent in component.allSubComponents()) {
            String propertyName = component.properties.entrySet().find { it.value == subComponent }.key
            Integer columnIndex = findColumnIndex(sheet, propertyName, columnStartIndex)
            result.addAll(handleComponent(subComponent, sheet, DATA_ROW_START_INDEX, columnIndex ?: 0))
        }
        return result
    }

    Sheet findSheetForComponent(Component component) {
        workbook.getSheet(component.name)
    }
}
