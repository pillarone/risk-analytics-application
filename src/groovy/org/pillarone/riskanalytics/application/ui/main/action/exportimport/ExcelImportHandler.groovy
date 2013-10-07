package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import com.ulcjava.base.application.util.IFileLoadHandler
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.FileConstants
import org.pillarone.riskanalytics.core.components.*
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.CommentFile

class ExcelImportHandler extends AbstractExcelHandler implements IFileLoadHandler {
    private List<ImportResult> importResults = []

    List<ImportResult> validate(Model expectedModel) {
        clearImportResults()
        XSSFSheet sheet = workbook.getSheet(META_INFO_SHEET)
        if (!sheet) {
            importResults << new ImportResult("Excel File does not contain mandatory sheet '$META_INFO_SHEET'", ImportResult.Type.ERROR)
        } else {
            if (!findModelName()) {
                importResults << new ImportResult("Excel File does not contain model class info at sheet '$META_INFO_SHEET'", ImportResult.Type.ERROR)
            } else {
                Model modelFromSheet = getModel()
                if (modelFromSheet.class != expectedModel.class) {
                    importResults << new ImportResult("Excel File does not contain model class name ${expectedModel.class.simpleName}. Found: ${modelFromSheet.class.simpleName}", ImportResult.Type.ERROR)
                } else {
                    process()
                }
            }
        }
        return importResults
    }

    void clearImportResults() {
        importResults.clear()
    }


    List<ImportResult> doImport(String parmeterizationName) {
        List<ParameterHolder> parameterHolders = ParameterizationHelper.extractParameterHoldersFromModel(modelInstance, 0)
        Parameterization parameterization = new Parameterization(parmeterizationName, modelInstance.class)
//        Comment comment = new Comment(modelInstance.class.simpleName - 'Model', 0)
//        File file = File.createTempFile(parmeterizationName, '.xlsx', new File(FileConstants.TEMP_FILE_DIRECTORY))
//        file.bytes = data
//        comment.addFile(new CommentFile(filename, file))
//        parameterization.addComment(comment)
        parameterHolders.each {
            parameterization.addParameter(it)
        }
        parameterization.save()
        // handle errors
        return importResults

    }

    private List<ImportResult> process() {
        Model model = getModel()
        model.init()
        model.injectComponentNames()
        model.allComponents.each { Component component ->
            Sheet sheet = findSheetForComponent(component)
            if (!sheet) {
                importResults << new ImportResult("Sheet with name $component.name not found in workbook.", ImportResult.Type.WARNING)
            } else {
                handleComponent(component, sheet, DATA_ROW_START_INDEX, 0)
            }
        }
        modelInstance = model
        return importResults
    }

    private def newInstance(Class clazz) {
        switch (clazz) {
            case Integer:
                return new Integer(0)
            case Double:
                return new Double(0)
            default:
                return ''
        }

    }

    private def toType(Enum objectClass, Cell cell) {
        String value = stringValue(cell)
        if (value) {
            return objectClass.class.valueOf(value)
        } else {
            return objectClass
        }
    }

    private def toType(ComboBoxTableMultiDimensionalParameter objectClass, Cell cell) {
        String value = stringValue(cell)
        if (value) {
            objectClass.setValueAt(toSubComponentName(cell.stringCellValue), 1, 0)
        }
        return objectClass
    }

    private def toType(ConstrainedString objectClass, Cell cell) {
        String value = stringValue(cell)
        if (value) {
            objectClass.setStringValue(toSubComponentName(value))
        }
        return objectClass
    }

    private def toType(IParameterObject objectClass, Cell cell) {
        AbstractParameterObjectClassifier classifier = objectClass.type.class."${cell.stringCellValue}"
        Map parameters = [:]
        classifier.getParameterNames().each { String parameterName ->
            int parameterColumnIndex = findColumnIndex(cell.sheet, parameterName, cell.columnIndex)
            Cell parameterCell = cell.row.getCell(parameterColumnIndex)
            if (parameterCell) {
                def parameterValue = toType(classifier.parameters[parameterName], cell.row.getCell(parameterColumnIndex))
                parameters.put(parameterName, parameterValue)
            } else {
                importResults << new ImportResult(cell, 'Cell is empty. Using default.', ImportResult.Type.WARNING)
                parameters.put(parameterName, classifier.parameters[parameterName])
            }
        }
        return classifier.getParameterObject(parameters)
    }

    private def toType(ConstrainedMultiDimensionalParameter mdp, Cell cell) {
        def mdpSheet = findMdpSheet(cell)
        def tableName = cell.stringCellValue
        int tableColumnIndex = findColumnIndex(mdpSheet, tableName, 0)
        List<List> values = []
        mdp.valueColumnCount.times {
            values << []
        }
        for (int rowIndex = DATA_ROW_START_INDEX; rowIndex <= mdpSheet.lastRowNum; rowIndex++) {
            Row row = mdpSheet.getRow(rowIndex)
            if (row && rowHasValuesInRange(row, tableColumnIndex, tableColumnIndex + mdp.valueColumnCount)) {
                for (int columnIndex = tableColumnIndex; columnIndex < tableColumnIndex + mdp.valueColumnCount; columnIndex++) {
                    Cell dataCell = row.getCell(columnIndex)
                    if (dataCell) {
                        Class valueType = mdp.constraints.getColumnType(columnIndex - tableColumnIndex)
                        def value = toType(newInstance(valueType), dataCell)
                        if (IComponentMarker.isAssignableFrom(valueType)) {
                            value = toSubComponentName(value as String)
                        }
                        values[columnIndex - tableColumnIndex] << value
                    } else {
                        importResults << new ImportResult(mdpSheet.sheetName, rowIndex, columnIndex, 'Cell is empty. Using default.', ImportResult.Type.WARNING)
                        values[columnIndex - tableColumnIndex] << newInstance(mdp.constraints.getColumnType(columnIndex - tableColumnIndex))
                    }
                }
            }
        }
        return new ConstrainedMultiDimensionalParameter(values, mdp.titles, mdp.constraints)
    }

    private def toType(Integer objectClass, Cell cell) {
        Number numericValue = numericValue(cell)
        return numericValue ? numericValue as Integer : new Integer(0)
    }

    private def toType(Double objectClass, Cell cell) {
        Number numericValue = numericValue(cell)
        return numericValue ? numericValue as Double : new Double(0)
    }

    private def toType(DateTime objectClass, Cell cell) {
        Date value = dateValue(cell)
        return value ? new DateTime(value.time) : new DateTime()
    }

    private def toType(Boolean objectClass, Cell cell) {
        String value = stringValue(cell)
        return value ? Boolean.parseBoolean(value) : Boolean.FALSE
    }

    private def toType(IResource resource, Cell cell) {
        String value = stringValue(cell)
        if (value) {
            String[] values = value.split(" v")
            return new ResourceHolder(resource.class, values[0], new VersionNumber(values[1]))
        }
    }

    private def toType(def objectClass, Cell cell) {
        return stringValue(cell)
    }

    private String stringValue(Cell cell) {
        try {
            return cell.stringCellValue
        } catch (IllegalStateException e) {
            importResults << new ImportResult(cell, "Cell type String expected", ImportResult.Type.ERROR)
            return null
        }
    }

    private Date dateValue(Cell cell) {
        try {
            return cell.dateCellValue
        } catch (IllegalStateException e) {
            importResults << new ImportResult(cell, "Cell type Date expected", ImportResult.Type.ERROR)
            return null
        }
    }

    private Number numericValue(Cell cell) {
        try {
            return cell.numericCellValue
        } catch (IllegalStateException e) {
            importResults << new ImportResult(cell, "Cell type Number expected", ImportResult.Type.ERROR)
            return null
        }
    }

    private void handleComponent(DynamicComposedComponent component, Sheet sheet, int rowIndex, int columnStartIndex) {
        handleComponent(component as Component, sheet, DATA_ROW_START_INDEX, columnStartIndex)
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
                    importResults << new ImportResult(sheet.sheetName, rowIdx, "$componentName processed", ImportResult.Type.SUCCESS)
                }
            }
        }
    }

    private void handleComponent(ComposedComponent component, Sheet sheet, int rowIndex, int columnStartIndex) {
        handleComponent(component as Component, sheet, rowIndex, columnStartIndex)
        for (Component subComponent in component.allSubComponents()) {
            String propertyName = component.properties.entrySet().find { it.value == subComponent }.key
            Integer columnIndex = findColumnIndex(sheet, propertyName, columnStartIndex)
            handleComponent(subComponent, sheet, DATA_ROW_START_INDEX, columnIndex ?: 0)
        }
    }

    private void handleComponent(Component component, Sheet sheet, int rowIndex, int columnStartIndex) {
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
    }

    @Override
    void onSuccess(InputStream[] ins, String[] filePaths, String[] fileNames) {
        workbook = new XSSFWorkbook(ins[0])
    }

    @Override
    void onFailure(int reason, String description) {

    }
}
