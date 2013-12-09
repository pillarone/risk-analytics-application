package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.poi.ss.formula.eval.NotImplementedException
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.components.*
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.CommentFile

/**
 * Deals with excel files that are imported into the application.
 * An export of such an excel file can be done using {@link ExcelExportHandler} class.
 * The {@link ExcelImportHandler#validate(Model)} method tries to map the data from the excel file onto the passed model.
 * There are many errors that can occur when the excel is mapped. e.g. the excel file hasn't got the required sheets or contains the wrong model.
 */
class ExcelImportHandler extends AbstractExcelHandler {
    private static final Log LOG = LogFactory.getLog(ExcelImportHandler)
    private static final String MISSING_MODEL_CLASS_INFO = 'MissingModelClassInfo'
    private static final String MISSING_META_INFO_SHEET = 'MissingMetaSheet'
    private static final String INCORRECT_MODEL_CLASS_INFO = 'IncorrectModelClassInfo'
    private static final String NO_ADDITIONAL_COMPONENTS_IMPORTED = 'NoAdditionalComponentsImported'
    private static final String MISSING_SHEET = 'MissingSheet'
    private static final String UNKNOWN_VALUE = 'UnknownValue'
    private static final String MDPSHEET_NO_FOUND = 'MDPSheetNoFound'
    private static final String TABLE_NOT_FOUND = 'TableNotFound'
    private static final String COMPONENT_ALREADY_PRESENT = 'ComponentAlreadyPresent'
    private static final String COMPONENT_PROCESSED = 'ComponentProcessed'
    private static final String WRONG_CELL_TYPE = 'WrongCellType'
    private static final String FORMULA_RESULT_WRONG_CELL_TYPE = 'FormulaResultWrongCellType'
    private static final String NULL_VALUE_NOT_ALLOWED = 'NullValueNotAllowed'
    private static final String UNKNOWN_REFERENCE_VALUE = 'UnknownReference'

    /**
     * Holds the validation results after calling {@link ExcelImportHandler#validate(Model)}
     */
    private List<ImportResult> importResults = []

    private Model parameterizedModel
    private Parameterization parameterization
    private Set allSubComponents = []
    private Set referencesToSubComponents = []

    /**
     * Validates the data within the workbook and tries to map all data onto the {@link ExcelImportHandler#modelInstance model}
     * @param expectedModel
     * @return A list of validation results.
     */
    List<ImportResult> validate(Model expectedModel) {
        clearImportResults()
        XSSFSheet sheet = workbook.getSheet(META_INFO_SHEET)
        if (!sheet) {
            importResults << new ImportResult(getMessage(MISSING_META_INFO_SHEET, [META_INFO_SHEET]), ImportResult.Type.ERROR)
        } else {
            if (!findModelName()) {
                importResults << new ImportResult(getMessage(MISSING_MODEL_CLASS_INFO, [META_INFO_SHEET]), ImportResult.Type.ERROR)
            } else {
                Model modelFromSheet = getModel()
                if (modelFromSheet?.class != expectedModel.class) {
                    importResults << new ImportResult(getMessage(INCORRECT_MODEL_CLASS_INFO, [expectedModel.class.simpleName, modelFromSheet?.class?.simpleName]), ImportResult.Type.ERROR)
                } else {
                    process()
                }
            }
        }
        referencesToSubComponents.each { Cell c ->
            String componentName = stringValue(c)
            if (!allSubComponents.contains(componentName)) {
                importResults << new ImportResult(c, getMessage(UNKNOWN_REFERENCE_VALUE, [componentName]), ImportResult.Type.ERROR)
            }
        }
        return importResults
    }

    private static String getMessage(String messageKey, List args = []) {
        return UIUtils.getText(ExcelImportHandler.class, messageKey, args.collect { it ?: 'null' })
    }

    void clearImportResults() {
        allSubComponents.clear()
        referencesToSubComponents.clear()
        importResults.clear()
    }

    /**
     * Imports the parameterized Model into the database.
     * @param parmeterizationName The name for the new parameterization.
     * @return
     */
    List<ImportResult> doImport(String parmeterizationName) {
        List<ParameterHolder> parameterHolders = ParameterizationHelper.extractParameterHoldersFromModel(modelInstance, 0)
        boolean mustSaveNewParameterization = !parameterization || parameterHolders.any {
            !(parameterization.hasParameterAtPath(it.path))
        }
        if (!mustSaveNewParameterization) {
            importResults.clear()
            importResults << new ImportResult(getMessage(NO_ADDITIONAL_COMPONENTS_IMPORTED), ImportResult.Type.SUCCESS)
        } else {
            Parameterization newParameterization
            if (parameterization) {
                newParameterization = ModellingItemFactory.incrementVersion(parameterization) as Parameterization
            } else {
                newParameterization = new Parameterization(parmeterizationName, modelInstance.class)
                String highestVersion = VersionNumber.getHighestNonWorkflowVersion(newParameterization)?.toString()
                if (highestVersion) {
                    newParameterization.setVersionNumber(new VersionNumber(highestVersion))
                    newParameterization.versionNumber = VersionNumber.incrementVersion(newParameterization)
                }
            }
            Comment comment = new Comment(modelInstance.class.simpleName - 'Model', 0)
            comment.text = "Excel Import"
            comment.addFile(new CommentFile(filename, excelFile))
            newParameterization.addComment(comment)
            parameterHolders.each {
                if (!parameterization?.hasParameterAtPath(it.path)) {
                    newParameterization.addParameter(it)
                }
            }
            newParameterization.save()
        }
        return importResults

    }

    /**
     * Initiates a model with the type that is given inside the excel file on the Meta-info sheet.
     * This method expects that the model can be instantiated.
     * After instantiation the model structure is taken to find the relevant data on the workbook.
     * If a sheet is missing in the workbook, the processing will not map anything to that component but continues processing.
     */
    private List<ImportResult> process() {
        Model processedModel = getModel()
        processedModel.init()
        processedModel.injectComponentNames()
        processedModel.allComponents.each { Component component ->
            Sheet sheet = findSheetForComponent(component)
            if (!sheet) {
                importResults << new ImportResult(getMessage(MISSING_SHEET, [getSheetName(component)]), ImportResult.Type.WARNING)
            } else {
                handleComponent(component, sheet, DATA_ROW_START_INDEX, 0)
            }
        }
        modelInstance = processedModel
        return importResults
    }

    private static def newInstance(Class clazz) {
        switch (clazz) {
            case Integer:
                return Integer.MIN_VALUE
            case Double:
                return Double.MIN_VALUE
            case DateTime:
                return new DateTime()
            default:
                return ''
        }

    }

    private def toType(Enum objectClass, Cell cell, Sheet sheet, int rowIndex, int columnIndex) {
        String value = stringValue(cell)
        List<Enum> possibleEnumValues = objectClass.values()
        Enum enumObject = possibleEnumValues.find {
            getDisplayName(it.declaringClass, it.toString()) == value || it.toString() == value
        }
        if (!enumObject) {
            List<String> displayNames = possibleEnumValues.collect { "'${getDisplayName(it.declaringClass, it.toString())}'" }
            importResults << new ImportResult(sheet.sheetName, rowIndex, columnIndex, getMessage(UNKNOWN_VALUE, [value, displayNames.join(',')]), ImportResult.Type.ERROR)
            return objectClass
        }
        return enumObject
    }

    private def toType(ComboBoxTableMultiDimensionalParameter objectClass, Cell cell, Sheet sheet, int rowIndex, int columnIndex) {
        String value = stringValue(cell)
        if (value) {
            objectClass.setValueAt(toSubComponentName(value), 1, 0)
            referencesToSubComponents << cell
        }
        return objectClass
    }

    private String toSubComponentName(String name) {
        if (name && name.size() > 1) {
            String firstLetterUpperCase = name[0].toUpperCase()
            String subComponentName = "${ComponentUtils.SUB}$firstLetterUpperCase${name.substring(1).replaceAll(' ', '')}"
            return subComponentName

        }
        return name
    }

    private def toType(ConstrainedString objectClass, Cell cell, Sheet sheet, int rowIndex, int columnIndex) {
        String value = stringValue(cell)
        if (value) {
            objectClass.setStringValue(toSubComponentName(value))
            referencesToSubComponents << cell
        }
        return objectClass
    }

    private def toType(IParameterObject objectClass, Cell cell, Sheet sheet, int rowIndex, int columnIndex) {
        String propertyName = stringValue(cell)
        AbstractParameterObjectClassifier classifier
        List<AbstractParameterObjectClassifier> classifiers = objectClass.type.getClassifiers() as List<AbstractParameterObjectClassifier>
        classifier = classifiers.find { AbstractParameterObjectClassifier c -> c.displayName?.equalsIgnoreCase(propertyName) || c.typeName?.equalsIgnoreCase(propertyName) }
        if (classifier) {
            Map parameters = [:]
            classifier.getParameterNames().each { String parameterName ->
                int parameterColumnIndex = findColumnIndex(cell.sheet, parameterName, cell.columnIndex + 1)
                Cell parameterCell = cell.row.getCell(parameterColumnIndex)
                if (parameterCell) {
                    def parameterValue = toType(classifier.parameters[parameterName], cell.row.getCell(parameterColumnIndex), sheet, rowIndex, parameterColumnIndex)
                    parameters.put(parameterName, parameterValue)
                } else {
                    parameters.put(parameterName, classifier.parameters[parameterName])
                }
            }
            try {
                return classifier.getParameterObject(parameters)
            } catch (Exception parameterObjectInstantiationError) {
                importResults << new ImportResult(sheet.sheetName, rowIndex, "Error instantiating object : ${classifier.toString()} of type : ${classifier.class.simpleName},  ${parameterObjectInstantiationError.message}", ImportResult.Type.ERROR)
                return objectClass
            }
        } else {
            importResults << new ImportResult(sheet.sheetName, rowIndex, columnIndex, getMessage(UNKNOWN_VALUE, [propertyName, classifiers.collect { "'${it.displayName}'" }.join(',')]), ImportResult.Type.ERROR)
            return objectClass
        }
    }

    private def toType(ConstrainedMultiDimensionalParameter mdp, Cell cell, Sheet s, int r, int c) {
        if (!cell) {
            return mdp
        }
        def mdpSheet = findMdpSheet(cell)
        if (!mdpSheet) {
            importResults << new ImportResult(cell, getMessage(MDPSHEET_NO_FOUND, [getMDPSheetName(cell)]), ImportResult.Type.ERROR)
            return mdp
        }
        def tableName = stringValue(cell)
        if (!tableName) {
            importResults << new ImportResult(cell, getMessage(NULL_VALUE_NOT_ALLOWED), ImportResult.Type.ERROR)
            return mdp
        }
        Integer tableColumnIndex = findColumnIndex(mdpSheet, tableName, 0)
        if (tableColumnIndex == null) {
            importResults << new ImportResult(cell, getMessage(TABLE_NOT_FOUND, [tableName, mdpSheet.sheetName]), ImportResult.Type.ERROR)
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
                        def value = toType(newInstance(valueType), dataCell, mdpSheet, rowIndex, columnIndex)
                        if (IComponentMarker.isAssignableFrom(valueType)) {
                            referencesToSubComponents << dataCell
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

    private boolean rowHasValuesInRange(Row row, int columnStartIndex, int columnEndIndex) {
        for (int columnIndex = columnStartIndex; columnIndex < columnEndIndex; columnIndex++) {
            Cell cell = row.getCell(columnIndex)
            if (cell) {
                switch (cell.cellType) {
                    case Cell.CELL_TYPE_STRING:
                        if (cell.stringCellValue) {
                            return true
                        }
                        break
                    case Cell.CELL_TYPE_NUMERIC:
                        return true
                    case Cell.CELL_TYPE_FORMULA:
                        CellValue value = evaluate(cell)
                        if (value?.cellType == Cell.CELL_TYPE_NUMERIC || value?.cellType == Cell.CELL_TYPE_STRING && value?.stringValue) {
                            return true
                        }
                }
            }
        }
        return false
    }


    private def toType(Integer objectClass, Cell cell, Sheet sheet, int rowIndex, int columnIndex) {
        Number numericValue = numericValue(cell)
        return numericValue ? numericValue as Integer : new Integer(0)
    }

    private def toType(Double objectClass, Cell cell, Sheet sheet, int rowIndex, int columnIndex) {
        Number numericValue = numericValue(cell)
        return numericValue ? numericValue as Double : new Double(0)
    }

    private def toType(BigDecimal objectClass, Cell cell, Sheet sheet, int rowIndex, int columnIndex) {
        Number numericValue = numericValue(cell)
        return numericValue ? numericValue as BigDecimal : new BigDecimal(0)
    }

    private def toType(DateTime objectClass, Cell cell, Sheet sheet, int rowIndex, int columnIndex) {
        Date value = dateValue(cell)
        return value ? new DateTime(value.time) : new DateTime()
    }

    private def toType(Boolean objectClass, Cell cell, Sheet sheet, int rowIndex, int columnIndex) {
        return booleanValue(cell)
    }

    private def toType(IResource resource, Cell cell, Sheet sheet, int rowIndex, int columnIndex) {
        String value = stringValue(cell)
        if (value) {
            String[] values = value.split(" v")
            return new ResourceHolder(resource.class, values[0], new VersionNumber(values[1]))
        }
    }

    private def toType(def objectClass, Cell cell, Sheet sheet, int rowIndex, int columnIndex) {
        return stringValue(cell)
    }

    private boolean importDisabled(Row row, int columnStartIndex) {
        int columnIndex = findColumnIndex(row.sheet, DISABLE_IMPORT, columnStartIndex)
        Cell cell = row.getCell(columnIndex)
        if (cell) {
            switch (cell.cellType) {
                case Cell.CELL_TYPE_BOOLEAN:
                    return cell.booleanCellValue
                case Cell.CELL_TYPE_STRING:
                    return cell.stringCellValue?.contains('#')
                case Cell.CELL_TYPE_FORMULA:
                    CellValue evalutatedValue = evaluate(cell)
                    if (evalutatedValue?.cellType == Cell.CELL_TYPE_BOOLEAN) {
                        return evalutatedValue.booleanValue
                    } else {
                        return evalutatedValue?.stringValue?.contains('#')
                    }
            }
        }
        return false
    }


    private String stringValue(Cell cell) {
        try {
            CellValue cellValue = evaluate(cell)
            if (cellValue) {
                if (cellValue.cellType == Cell.CELL_TYPE_STRING) {
                    return cellValue.stringValue
                } else {
                    importResults << new ImportResult(cell, getMessage(FORMULA_RESULT_WRONG_CELL_TYPE, ['String']), ImportResult.Type.ERROR)
                    return null
                }

            } else {
                return cell?.stringCellValue
            }
        } catch (IllegalStateException ignored) {
            importResults << new ImportResult(cell, getMessage(WRONG_CELL_TYPE, ['String']), ImportResult.Type.ERROR)
            return null
        }
    }

    private boolean booleanValue(Cell cell) {
        try {
            CellValue cellValue = evaluate(cell)
            if (cellValue) {
                if (cellValue.cellType == Cell.CELL_TYPE_BOOLEAN) {
                    return cellValue.booleanValue
                } else {
                    importResults << new ImportResult(cell, getMessage(FORMULA_RESULT_WRONG_CELL_TYPE, ['Boolean']), ImportResult.Type.ERROR)
                    return null
                }

            } else {
                if (cell?.cellType == Cell.CELL_TYPE_BOOLEAN) {
                    return cell?.booleanCellValue
                } else {
                    return Boolean.parseBoolean(cell?.stringCellValue)
                }
            }
        } catch (IllegalStateException ignored) {
            importResults << new ImportResult(cell, getMessage(WRONG_CELL_TYPE, ['Boolean']), ImportResult.Type.ERROR)
            return null
        }
    }

    private Date dateValue(Cell cell) {
        try {
            CellValue cellValue = evaluate(cell)
            if (cellValue) {
                if (cellValue.cellType == Cell.CELL_TYPE_NUMERIC) {
                    return DateUtil.getJavaDate(cellValue.numberValue)
                } else {
                    importResults << new ImportResult(cell, getMessage(FORMULA_RESULT_WRONG_CELL_TYPE, ['Numeric']), ImportResult.Type.ERROR)
                    return null
                }
            } else {
                return cell?.dateCellValue
            }
        } catch (IllegalStateException ignored) {
            importResults << new ImportResult(cell, getMessage(WRONG_CELL_TYPE, ['Date']), ImportResult.Type.ERROR)
            return null
        }
    }

    private Number numericValue(Cell cell) {
        CellValue cellValue = evaluate(cell)
        try {
            if (cellValue) {
                if (cellValue.cellType == Cell.CELL_TYPE_NUMERIC) {
                    return cellValue.numberValue
                } else if (!cellValue.stringValue.isEmpty()) {
                    if (cellValue.stringValue.isNumber()) {
                        return cellValue.stringValue.toDouble()
                    } else {
                        importResults << new ImportResult(cell, getMessage(FORMULA_RESULT_WRONG_CELL_TYPE, ['Number']), ImportResult.Type.ERROR)
                        return null
                    }
                } else {
                    return null
                }
            } else {
                return cell?.numericCellValue
            }
        } catch (IllegalStateException ignored) {
            importResults << new ImportResult(cell, getMessage(WRONG_CELL_TYPE, ['Number']), ImportResult.Type.ERROR)
            return null
        }
    }

    private CellValue evaluate(Cell cell) {
        if (cell?.cellType == Cell.CELL_TYPE_FORMULA) {
            FormulaEvaluator evaluator = workbook.creationHelper.createFormulaEvaluator()
            try {
                return evaluator.evaluate(cell)
            } catch (NotImplementedException e) {
                LOG.error(e.message, e)
                importResults << new ImportResult(cell,"Formula not implemented: '${e.cause?.message}'", ImportResult.Type.ERROR)
                return null
            } catch (Exception e) {
                LOG.error(e.message, e)
                importResults << new ImportResult(cell, e.message, ImportResult.Type.ERROR)
                return null
            }
        }
    }

    /**
     * Handles dynamic components. Each row represents a sub component. If the Disable Import cell contains a '#' or a boolean value TRUE
     * then this row is ignored.
     * @param component The parent component to which the sub components are added.
     * @param sheet The sheet to process.
     * @param rowIndex The
     * @param columnStartIndex
     */
    private void handleComponent(DynamicComposedComponent component, Sheet sheet, int rowIndex, int columnStartIndex) {
        handleComponent(component as Component, sheet, rowIndex, columnStartIndex)
        for (int rowIdx = rowIndex; rowIdx <= sheet.lastRowNum; rowIdx++) {
            Row row = sheet.getRow(rowIdx)
            if (row) {
                int index = findColumnIndex(sheet, COMPONENT_HEADER_NAME, columnStartIndex)
                String componentName = stringValue(row.getCell(index))
                if (componentName && !importDisabled(row, columnStartIndex)) {
                    String subComponentName = toSubComponentName(componentName)
                    if (componentAlreadyExists(subComponentName) || component.getComponentByName(subComponentName)) {
                        importResults << new ImportResult(sheet.sheetName, rowIdx, getMessage(COMPONENT_ALREADY_PRESENT, [componentName]), ImportResult.Type.WARNING)
                    } else {
                        allSubComponents << componentName
                        Component subComponent = component.createDefaultSubComponent()
                        subComponent.setName(subComponentName)
                        component.addSubComponent(subComponent)
                        handleComponent(subComponent, sheet, rowIdx, columnStartIndex)
                        importResults << new ImportResult(sheet.sheetName, rowIdx, getMessage(COMPONENT_PROCESSED, [componentName]), ImportResult.Type.SUCCESS)
                    }
                }
            }
        }
    }

    /**
     * Handles the composed components.
     * @param component
     * @param sheet
     * @param rowIndex
     * @param columnStartIndex
     */
    private void handleComponent(ComposedComponent component, Sheet sheet, int rowIndex, int columnStartIndex) {
        handleComponent(component as Component, sheet, rowIndex, columnStartIndex)
        for (Component subComponent in component.allSubComponents()) {
            String propertyName = component.properties.entrySet().find { it.value == subComponent }.key
            Integer columnIndex = findColumnIndex(sheet, propertyName, columnStartIndex)
            handleComponent(subComponent, sheet, rowIndex, columnIndex ?: 0)
        }
    }

    /**
     * Handles the basic components.
     * @param component
     * @param sheet
     * @param rowIndex
     * @param columnStartIndex
     */
    private void handleComponent(Component component, Sheet sheet, int rowIndex, int columnStartIndex) {
        Row dataRow = sheet.getRow(rowIndex)
        getAllParms(component).each { String paramName ->
            Integer columnIndex = findParameterColumnIndex(sheet, paramName, columnStartIndex)
            if (dataRow && columnIndex != null) {
                Cell cell = dataRow.getCell(columnIndex)
                def paramType = component[paramName]
                component[paramName] = toType(paramType, cell, sheet, rowIndex, columnIndex)
            }
        }
    }

    private boolean componentAlreadyExists(String componentName) {
        if (parameterizedModel) {
            return parameterizedModel.allComponentsRecursively.find { it.name == componentName }
        }
        return false
    }

    void setParameterizationOnModel(final Model model, Parameterization parameterization) {
        Model m = model.class.newInstance()
        m.init()
        m.injectComponentNames()
        parameterization.load()
        ParameterApplicator applicator = new ParameterApplicator(parameterization: parameterization, model: m)
        applicator.init()
        applicator.applyParameterForPeriod(0)
        parameterizedModel = m
        this.parameterization = parameterization
    }
}
