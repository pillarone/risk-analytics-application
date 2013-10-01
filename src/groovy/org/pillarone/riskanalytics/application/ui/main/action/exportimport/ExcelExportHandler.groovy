package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import org.apache.poi.POIXMLProperties
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.pillarone.riskanalytics.application.ui.parameterization.model.TreeBuilderUtil
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.ComponentUtils
import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.util.PropertiesUtils

class ExcelExportHandler extends AbstractExcelHandler {
    Model model
    List<ConstrainedMultiDimensionalParameter> multiDimensionalParameters = []

    ExcelExportHandler(Model model) {
        super()
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
            Row technicalHeaderRow = sheet.createRow(1)

            handleComponent(component, headerRow, technicalHeaderRow, 0)
            if (sheet.getRow(0).lastCellNum > 0) {
                (0..sheet.getRow(0).getLastCellNum()).each {
                    sheet.autoSizeColumn(it)
                }
            }
        }
        multiDimensionalParameters.each { ConstrainedMultiDimensionalParameter mdp ->
            String mdpSheetName = getSheetName(mdp)
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

    private int handleComponent(Component component, Row headerRow, Row technicalHeaderRow, int columnIndex) {
        List allParms = getAllParms(component)
        for (String parm in allParms) {
            Cell technicalCell = technicalHeaderRow.createCell(columnIndex, Cell.CELL_TYPE_STRING)
            Cell cell = headerRow.createCell(columnIndex, Cell.CELL_TYPE_STRING)
            setFont(cell, 10 as short, false, Font.BOLDWEIGHT_BOLD)
            setFont(technicalCell, 10 as short, false, Font.BOLDWEIGHT_BOLD)
            String displayName = I18NUtils.findParameterDisplayName(component, parm)
            //TODO: call normalizedName
            cell.setCellValue(displayName ?: ComponentUtils.getNormalizedName(parm))
            technicalCell.setCellValue(parm)
            columnIndex = addParameterCells(component[parm], headerRow, technicalHeaderRow, ++columnIndex)
        }

        return columnIndex
    }

    private int handleComponent(ComposedComponent component, Row headerRow, Row technicalHeaderRow, int columnIndex) {
        columnIndex = handleComponent(component as Component, headerRow, technicalHeaderRow, columnIndex)
        for (Component subComponent in component.allSubComponents()) {
            String propertyName = component.properties.entrySet().find { it.value == subComponent }.key
            Cell technicalCell = technicalHeaderRow.createCell(columnIndex)
            Cell cell = headerRow.createCell(columnIndex)
            String displayName = I18NUtils.findParameterDisplayName(component, propertyName)
            cell.setCellValue(displayName ?: propertyName)
            technicalCell.setCellValue(propertyName)
            columnIndex = handleComponent(subComponent, headerRow, technicalHeaderRow, ++columnIndex)
        }

        return columnIndex

    }

    private int handleComponent(DynamicComposedComponent component, Row headerRow, Row technicalHeaderRow, int columnIndex) {
        Cell technicalCell = technicalHeaderRow.createCell(columnIndex, Cell.CELL_TYPE_STRING)
        Cell cell = headerRow.createCell(columnIndex, Cell.CELL_TYPE_STRING)
        setCellComment(technicalCell, "To disable import add '#' to this row")
        technicalCell.setCellValue(DISABLE_IMPORT)
        cell.setCellValue(DISABLE_IMPORT)
        columnIndex++
        headerRow.createCell(columnIndex).setCellValue(COMPONENT_HEADER_NAME)
        technicalHeaderRow.createCell(columnIndex).setCellValue(COMPONENT_HEADER_NAME)
        return handleComponent(component.createDefaultSubComponent(), headerRow, technicalHeaderRow, ++columnIndex)
    }

    private int addParameterCells(def parmObject, Row headerRow, Row technicalHeaderRow, int columnIndex) {
        columnIndex
    }

    private int addParameterCells(ConstrainedMultiDimensionalParameter multiDimensionalParameter, Row headerRow, Row technicalHeaderRow, int columnIndex) {
        addParameter(multiDimensionalParameter)
        return columnIndex
    }

    private int addParameterCells(IParameterObject parmObject, Row headerRow, Row technicalHeaderRow, int columnIndex) {
        List<IParameterObjectClassifier> classifiers = parmObject.type.getClassifiers()
        Set writtenParameters = []
        classifiers.each { IParameterObjectClassifier classifier ->
            for (String parmName : classifier.parameterNames) {
                if (!writtenParameters.contains(parmName)) {
                    writtenParameters << parmName
                    Cell technicalCell = technicalHeaderRow.createCell(columnIndex, Cell.CELL_TYPE_STRING)
                    Cell cell = headerRow.createCell(columnIndex, Cell.CELL_TYPE_STRING)
                    setFont(cell, 8 as short, true)
                    setFont(technicalCell, 8 as short, true)
                    technicalCell.setCellValue(parmName)
                    cell.setCellValue(parmName)

                    Object classifierParameter = classifier.getType(parmName)
                    if (classifierParameter instanceof ConstrainedMultiDimensionalParameter) {
                        setCellComment(cell, getSheetName(classifierParameter))
                        //TODO
//                        cell.setCellValue("=HYPERLINK(\"#Exampleresourceconstraints-MDP\";\"retrospectivereinsurance\")")
                        addParameter(classifierParameter)

                    }
                    if (parmObject.class == classifierParameter.class) {
                        // TODO (recursive call with same classifier not supported.)
                    } else {
                        columnIndex = addParameterCells(classifierParameter, headerRow, technicalHeaderRow, ++columnIndex)

                    }
                }

            }
        }
        return columnIndex
    }

    private static String getSheetName(ConstrainedMultiDimensionalParameter constrainedMultiDimensionalParameter) {
        String name = "${constrainedMultiDimensionalParameter.constraints.class.simpleName}-MDP"
        return name.substring(0, Math.min(name.length(), 31));
    }

    private addParameter(ConstrainedMultiDimensionalParameter multiDimensionalParameter) {
        if (!multiDimensionalParameters*.constraints.contains(multiDimensionalParameter.constraints)) {
            multiDimensionalParameters << multiDimensionalParameter
        }

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

    private static setCellComment(Cell cell, String commentString, int width = 4) {
        ClientAnchor anchor = cell.row.sheet.workbook.creationHelper.createClientAnchor()
        anchor.setCol1(cell.columnIndex);
        anchor.setCol2(cell.columnIndex + width);
        anchor.setRow1(cell.row.rowNum);
        anchor.setRow2(cell.row.rowNum + 1);
        Comment comment = cell.row.getSheet().createDrawingPatriarch().createCellComment(anchor)
        comment.setString(new XSSFRichTextString(commentString))
        cell.setCellComment(comment)
    }


}