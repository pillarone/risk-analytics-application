package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.*
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

class ExcelImportHandler extends AbstractExcelHandler {

    ExcelImportHandler(File excelParameterization) {
        super(excelParameterization)
    }

    ExcelImportHandler() {
    }

    List<ImportResult> validate(Model expectedModel) {
        List<ImportResult> result = []
        XSSFSheet sheet = workbook.getSheet(META_INFO_SHEET)
        if (!sheet) {
            result << new ImportResult("Excel File does not contain mandatory sheet '$META_INFO_SHEET'", ImportResult.Type.ERROR)
        } else {
            if (!findModelName()) {
                result << new ImportResult("Excel File does not contain mandatory sheet '$META_INFO_SHEET'", ImportResult.Type.ERROR)
            }
        }
        Model modelFromSheet = getModel()
        if (modelFromSheet.class != expectedModel.class) {
            result << new ImportResult("Excel File does not contain model class name ${expectedModel.class.simpleName}. Found: ${modelFromSheet.class.simpleName}", ImportResult.Type.ERROR)
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
            if (!sheet){
                result << new ImportResult("Sheet with name $component.name not found in workbook.", ImportResult.Type.WARNING)
            }
            result.addAll(handleComponent(component, sheet, DATA_ROW_START_INDEX, 0))
        }
        modelInstance = model
        return result

    }

    private def newInstance(Class clazz) {
        switch (clazz) {
            case Integer:
                return new Integer(0)
            case Double:
                return new Double(0)
            case IComponentMarker:
                return ''
            default:
                return ''
//                if (clazz.hasProperty('enumConstants')) {
//                    return clazz.'enumConstants'[0]
//                }
//                throw new IllegalArgumentException('Dont know what to do.')
        }

    }

    def toType(Enum objectClass, Cell cell) {
        return objectClass.class.valueOf(cell.stringCellValue)
    }

    def toType(ComboBoxTableMultiDimensionalParameter objectClass, Cell cell) {
        objectClass.setValueAt(toSubComponentName(cell.stringCellValue), 1, 0)
        return objectClass
    }

    def toType(ConstrainedString objectClass, Cell cell) {
        objectClass.setStringValue(toSubComponentName(cell.stringCellValue))
        return objectClass
    }

    def toType(IParameterObject objectClass, Cell cell) {
        AbstractParameterObjectClassifier classifier = objectClass.type.class."${cell.stringCellValue}"
        Map parameters = [:]
        classifier.getParameterNames().each { String parameterName ->
            int parameterColumnIndex = findColumnIndex(cell.sheet, parameterName, cell.columnIndex)
            Cell parameterCell = cell.row.getCell(parameterColumnIndex)
            if (parameterCell) {
                parameters.put(parameterName, toType(classifier.parameters[parameterName], cell.row.getCell(parameterColumnIndex)))
            } else {
                // using default.
                parameters.put(parameterName, classifier.parameters[parameterName])
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
            if (row && rowHasValuesInRange(row, tableColumnIndex, tableColumnIndex + mdp.valueColumnCount)) {
                for (int columnIndex = tableColumnIndex; columnIndex < tableColumnIndex + mdp.valueColumnCount; columnIndex++) {
                    Cell dataCell = row.getCell(columnIndex)
                    if (dataCell) {
                        Class valueType = mdp.constraints.getColumnType(columnIndex - tableColumnIndex)
                        def value = toType(newInstance(valueType), dataCell)
                        if (IComponentMarker.isAssignableFrom(valueType)){
                            value = toSubComponentName(value)
                        }
                        values[columnIndex - tableColumnIndex] << value

                    } else {
                        values[columnIndex - tableColumnIndex] << newInstance(mdp.constraints.getColumnType(columnIndex - tableColumnIndex))
                    }
                }
            }
        }
        return new ConstrainedMultiDimensionalParameter(values, mdp.titles, mdp.constraints)
    }

    boolean rowHasValuesInRange(Row row, int columnStartIndex, int columnEndIndex) {
        for (int columnIndex = columnStartIndex; columnIndex <= columnEndIndex; columnIndex++) {
            if (row.getCell(columnIndex)) {
                return true
            }
        }
        return false
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

    private List<ImportResult> handleComponent(DynamicComposedComponent component, Sheet sheet, int rowIndex, int columnStartIndex) {
        List<ImportResult> result = []
        result.addAll(handleComponent(component as Component, sheet, DATA_ROW_START_INDEX, columnStartIndex))
        for (int rowIdx = rowIndex; rowIdx <= sheet.lastRowNum; rowIdx++) {
            Row row = sheet.getRow(rowIdx)
            if (row) {
                int index = findColumnIndex(sheet, COMPONENT_HEADER_NAME, columnStartIndex)
                String componentName = row.getCell(index)
                if (componentName && importEnabled(row, columnStartIndex)) {
                    Component subComponent = component.createDefaultSubComponent()
                    subComponent.setName(toSubComponentName(componentName))
                    component.addSubComponent(subComponent)
                    handleComponent(subComponent, sheet, rowIdx, columnStartIndex)
                    result << new ImportResult(sheet.sheetName, rowIdx, "$componentName processed", ImportResult.Type.SUCCESS)
                }
            }
        }
        return result
    }

    private List<ImportResult> handleComponent(ComposedComponent component, Sheet sheet, int rowIndex, int columnStartIndex) {
        List<ImportResult> result = []
        result.addAll(handleComponent(component as Component, sheet, rowIndex, columnStartIndex))
        for (Component subComponent in component.allSubComponents()) {
            String propertyName = component.properties.entrySet().find { it.value == subComponent }.key
            Integer columnIndex = findColumnIndex(sheet, propertyName, columnStartIndex)
            result.addAll(handleComponent(subComponent, sheet, DATA_ROW_START_INDEX, columnIndex ?: 0))
        }
        return result
    }

    private Sheet findSheetForComponent(Component component) {
        workbook.getSheet(component.name)
    }

    @Override
    void onSuccess(InputStream[] ins, String[] filePaths, String[] fileNames) {
        workbook = new XSSFWorkbook(ins[0])
        List<ImportResult> results = process()
        List<ParameterHolder> parameterHolders = ParameterizationHelper.extractParameterHoldersFromModel(modelInstance, 0)
        Parameterization parameterization = new Parameterization(System.currentTimeMillis().toString(), modelInstance.class)
        parameterHolders.each {
            parameterization.addParameter(it)
        }
        parameterization.save()
    }

    private List<ImportResult> handleComponent(Component component, Sheet sheet, int rowIndex, int columnStartIndex) {
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


    @Override
    void onFailure(int reason, String description) {

    }
}
