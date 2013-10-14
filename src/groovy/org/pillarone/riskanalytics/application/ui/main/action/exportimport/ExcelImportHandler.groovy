package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import com.ulcjava.base.application.util.IFileLoadHandler
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.ParameterizationDAO
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
        updateVersionNumber(parameterization)
        Comment comment = new Comment(modelInstance.class.simpleName - 'Model', 0)
        comment.text = "Excel Import"
        comment.addFile(new CommentFile(filename, excelFile))
        parameterization.addComment(comment)
        parameterHolders.each {
            parameterization.addParameter(it)
        }
        parameterization.save()
        // handle errors
        return importResults

    }

    private static void updateVersionNumber(Parameterization parameterization) {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            ParameterizationDAO dao = ParameterizationDAO.find(parameterization.name, parameterization.modelClass.name, i as String)
            if (!dao) {
                parameterization.versionNumber = new VersionNumber(i as String)
                break
            }
        }
    }

    private List<ImportResult> process() {
        Model model = getModel()
        model.init()
        model.injectComponentNames()
        model.allComponents.each { Component component ->
            Sheet sheet = findSheetForComponent(component)
            if (!sheet) {
                importResults << new ImportResult("Sheet with name ${getComponentDisplayName(component)} not found in workbook.", ImportResult.Type.WARNING)
            } else {
                handleComponent(component, sheet, DATA_ROW_START_INDEX, 0)
            }
        }
        modelInstance = model
        return importResults
    }

    private static def newInstance(Class clazz) {
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
            try {
                return objectClass.class.valueOf(value)
            } catch (IllegalArgumentException ignored) {
                importResults << new ImportResult(cell, "Unknown value $value. Allowed: ${objectClass.values().collect { "'${it.toString()}'" }.join(',')}", ImportResult.Type.ERROR)
                return objectClass
            }
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
        Class typeClass = objectClass.type.class
        String propertyName = stringValue(cell)
        AbstractParameterObjectClassifier classifier
        try {
            classifier = typeClass."$propertyName"
        } catch (MissingPropertyException ignored) {
            List<AbstractParameterObjectClassifier> classifiers = objectClass.type.getClassifiers()
            importResults << new ImportResult(cell, "Unknown value $propertyName. Allowed: ${classifiers.collect { "'${it.typeName}'" }.join(',')}", ImportResult.Type.ERROR)
        }
        if (classifier) {
            Map parameters = [:]
            classifier.getParameterNames().each { String parameterName ->
                int parameterColumnIndex = findColumnIndex(cell.sheet, parameterName, cell.columnIndex)
                Cell parameterCell = cell.row.getCell(parameterColumnIndex)
                if (parameterCell) {
                    def parameterValue = toType(classifier.parameters[parameterName], cell.row.getCell(parameterColumnIndex))
                    parameters.put(parameterName, parameterValue)
                } else {
                    parameters.put(parameterName, classifier.parameters[parameterName])
                }
            }
            return classifier.getParameterObject(parameters)
        } else {
            return objectClass
        }
    }

    private def toType(ConstrainedMultiDimensionalParameter mdp, Cell cell) {
        def mdpSheet = findMdpSheet(cell)
        if (!mdpSheet) {
            importResults << new ImportResult(cell, "Sheet for MDP with name ${getMDPSheetName(cell)} not found", ImportResult.Type.ERROR)
            return mdp
        }
        def tableName = cell.stringCellValue
        if (!tableName) {
            importResults << new ImportResult(cell, "Cell with table name reference must not be empty.", ImportResult.Type.ERROR)
            return mdp
        }
        Integer tableColumnIndex = findColumnIndex(mdpSheet, tableName, 0)
        if (tableColumnIndex == null) {
            importResults << new ImportResult(cell, "Table with name '${tableName}' in MDP Sheet ${mdpSheet.sheetName} not found", ImportResult.Type.ERROR)
            return mdp
        }
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
        } catch (IllegalStateException ignored) {
            importResults << new ImportResult(cell, "Cell type String expected", ImportResult.Type.ERROR)
            return null
        }
    }

    private Date dateValue(Cell cell) {
        try {
            return cell.dateCellValue
        } catch (IllegalStateException ignored) {
            importResults << new ImportResult(cell, "Cell type Date expected", ImportResult.Type.ERROR)
            return null
        }
    }

    private Number numericValue(Cell cell) {
        try {
            return cell.numericCellValue
        } catch (IllegalStateException ignored) {
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
