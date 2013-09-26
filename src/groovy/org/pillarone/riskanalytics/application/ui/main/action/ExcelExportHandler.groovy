package org.pillarone.riskanalytics.application.ui.main.action

import org.apache.poi.POIXMLProperties
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.ClientAnchor
import org.apache.poi.ss.usermodel.Comment
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFComment
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.pillarone.riskanalytics.application.ui.parameterization.model.TreeBuilderUtil
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.util.PropertiesUtils

class ExcelExportHandler {
    Model model
    List<ConstrainedMultiDimensionalParameter> multiDimensionalParameters = []

    ExcelExportHandler(Model model) {
        this.model = model
    }

    byte[] exportModel() {
        model.init()
        model.injectComponentNames()
        OutputStream outputStream = new ByteArrayOutputStream()
        Workbook workbook = new XSSFWorkbook()
        POIXMLProperties.CustomProperties properties = workbook.getProperties().customProperties
        //TODO (add other meta properties if needed)
        properties.addProperty('Model', model.class.name)
        properties.addProperty('application-version', new PropertiesUtils().getProperties("/version.properties").getProperty("version", "N/A"))
        model.allComponents.each { Component component ->
            Sheet sheet = workbook.createSheet(component.name)
            Row headerRow = sheet.createRow(0)

            handleComponent(component, headerRow, 0)
            (0..sheet.getRow(0).getLastCellNum()).each {
                sheet.autoSizeColumn(it)
            }
        }
        multiDimensionalParameters.each { ConstrainedMultiDimensionalParameter mdp ->
            String mdpSheetName = "${mdp.constraints.class.simpleName}-MDP"
            Sheet sheet = workbook.createSheet(mdpSheetName)
            properties.addProperty(mdpSheetName, sheet.sheetName)
            Row headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue('Link to component todo...')
            Row columnNameRow = sheet.createRow(1)
            mdp.getColumnNames().eachWithIndex { val, i ->
                columnNameRow.createCell(i).setCellValue(val)
            }
        }
        workbook.write(outputStream)
        return outputStream.toByteArray()

    }

    private int handleComponent(Component component, Row headerRow, int columnIndex) {
        List allParms = getAllParms(component)
        for (String parm in allParms) {
            Cell cell = headerRow.createCell(columnIndex++, Cell.CELL_TYPE_STRING)
            setFont(cell, 10 as short, false, Font.BOLDWEIGHT_BOLD)
            cell.setCellValue(parm)
            columnIndex = addParameterCells(component[parm], headerRow, columnIndex)
        }

        return columnIndex
    }

    private int handleComponent(ComposedComponent component, Row headerRow, int columnIndex) {
        columnIndex = handleComponent(component as Component, headerRow, columnIndex)
        for (Component subComponent in component.allSubComponents()) {
            String propertyName = component.properties.entrySet().find { it.value == subComponent }.key
            headerRow.createCell(columnIndex++).setCellValue(propertyName)
            columnIndex = handleComponent(subComponent, headerRow, columnIndex)
        }

        return columnIndex

    }

    private int handleComponent(DynamicComposedComponent component, Row headerRow, int columnIndex) {
        Cell cell = headerRow.createCell(columnIndex++, Cell.CELL_TYPE_STRING)
        setCellComment(cell, "To disable import add '#' to this row")
        cell.setCellValue('Disable Import')
        headerRow.createCell(columnIndex++).setCellValue('Component Name')
        return handleComponent(component.createDefaultSubComponent(), headerRow, columnIndex)
    }

    private int addParameterCells(def parmObject, Row headerRow, int columnIndex) {
        columnIndex
    }

    private int addParameterCells(ConstrainedMultiDimensionalParameter multiDimensionalParameter, Row headerRow, int columnIndex) {
        addParameter(multiDimensionalParameter)
        return columnIndex
    }

    private int addParameterCells(IParameterObject parmObject, Row headerRow, int columnIndex) {
        List<IParameterObjectClassifier> classifiers = parmObject.type.getClassifiers()
        Set writtenParameters = []
        classifiers.each { IParameterObjectClassifier classifier ->
            for (String parmName : classifier.parameterNames) {
                if (!writtenParameters.contains(parmName)) {
                    writtenParameters << parmName
                    Cell parameterCell = headerRow.createCell(columnIndex++, Cell.CELL_TYPE_STRING)
                    setFont(parameterCell, 8 as short, true)
                    parameterCell.setCellValue(parmName)

                    Object classifierParameter = classifier.getType(parmName)
                    if (classifierParameter instanceof ConstrainedMultiDimensionalParameter) {
                        addParameter(classifierParameter)

                    }
                    if (parmObject.class == classifierParameter.class) {
                        // TODO (recursive call with same classifier not supported.)
                    } else {
                        columnIndex = addParameterCells(classifierParameter, headerRow, columnIndex)

                    }
                }

            }
        }
        return columnIndex
    }


    void addParameter(ConstrainedMultiDimensionalParameter multiDimensionalParameter) {
        if (!multiDimensionalParameters*.constraints.contains(multiDimensionalParameter.constraints)) {
            multiDimensionalParameters << multiDimensionalParameter
        }

    }

    private List getAllParms(Component component) {
        TreeBuilderUtil.collectProperties(component, 'parm')
    }

    private static setFont(Cell cell, short fontSize, boolean italic, short bold = Font.BOLDWEIGHT_NORMAL) {
        Font font = cell.row.sheet.workbook.createFont()
        font.setFontHeightInPoints(fontSize)
        CellStyle style = cell.row.sheet.workbook.createCellStyle()
        font.italic = italic
        font.boldweight = bold
        style.setFont(font)
        cell.setCellStyle(style)
    }

    private static setCellComment(Cell cell, String commentString) {
        ClientAnchor anchor = cell.row.sheet.workbook.creationHelper.createClientAnchor()
        anchor.setCol1(cell.columnIndex);
        anchor.setCol2(cell.columnIndex + 1);
        anchor.setRow1(cell.row.rowNum);
        anchor.setRow2(cell.row.rowNum + 3);
        Comment comment = cell.row.getSheet().createDrawingPatriarch().createCellComment(anchor)
        comment.setString(new XSSFRichTextString(commentString))
        cell.setCellComment(comment)
    }


}