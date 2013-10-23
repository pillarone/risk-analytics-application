package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

class ExcelExportHandler extends AbstractExcelHandler {
    Model model
    static List<java.awt.Color> PARAM_COLORS = [java.awt.Color.LIGHT_GRAY, new java.awt.Color(230,230,230)]
    Map<IMultiDimensionalConstraints, List<MDPTitleContraints>> mdpConstraintsWithTitles = [:]

    ExcelExportHandler(Model model) {
        this.model = model
    }

    byte[] exportModel() {
        model.init()
        model.injectComponentNames()
        OutputStream outputStream = new ByteArrayOutputStream()
        model.allComponents.each { Component component ->
            String name = getSheetName(component)
            Sheet sheet = workbook.createSheet(name)
            Row headerRow = sheet.createRow(0)
            Row technicalHeaderRow = sheet.createRow(1)

            handleComponent(component, headerRow, technicalHeaderRow, 0)
        }
        mdpConstraintsWithTitles.each { Map.Entry<IMultiDimensionalConstraints, List<MDPTitleContraints>> entry ->
            entry.value.eachWithIndex { MDPTitleContraints constraints, int index ->
                String mdpSheetName = getSheetNameForMDP(constraints)
                Sheet sheet = workbook.createSheet(mdpSheetName)
                Row headerRow = sheet.createRow(0)
                headerRow.createCell(0).setCellValue('Reference anchor.')
                Row columnNameRow = sheet.createRow(1)
                constraints.titles.eachWithIndex {val, i ->
                    columnNameRow.createCell(i).setCellValue(val.toString())
                }
            }
        }
        workbook.numberOfSheets.times {
            Sheet sheet = workbook.getSheetAt(it)
            if (sheet.getRow(0).lastCellNum > 0) {
                (0..sheet.getRow(0).getLastCellNum()).each {
                    sheet.autoSizeColumn(it)
                }
            }
        }
        addMetaInfo(workbook, model)
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
            cell.setCellValue(getDisplayName(component, parm))
            technicalCell.setCellValue(parm)
            XSSFCellStyle style = workbook.createCellStyle()
            java.awt.Color color = PARAM_COLORS[columnIndex % PARAM_COLORS.size()]
            style.setFillForegroundColor(new XSSFColor(color))
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND)
            XSSFCellStyle paramStyle = style.clone()
            style.setBorderLeft(BorderStyle.THIN)
            technicalHeaderRow.sheet.setDefaultColumnStyle(columnIndex, style)
            columnIndex = addParameterCells(component[parm], headerRow, technicalHeaderRow, ++columnIndex, cell, paramStyle)
        }

        return columnIndex
    }

    private int handleComponent(ComposedComponent component, Row headerRow, Row technicalHeaderRow, int columnIndex) {
        columnIndex = handleComponent(component as Component, headerRow, technicalHeaderRow, columnIndex)
        for (Component subComponent in component.allSubComponents()) {
            String propertyName = component.properties.entrySet().find { it.value == subComponent }.key
            Cell technicalCell = technicalHeaderRow.createCell(columnIndex)
            Cell cell = headerRow.createCell(columnIndex)
            cell.setCellValue(getDisplayName(component, propertyName))
            technicalCell.setCellValue(propertyName)
            columnIndex = handleComponent(subComponent, headerRow, technicalHeaderRow, ++columnIndex)
        }

        return columnIndex

    }

    private int handleComponent(DynamicComposedComponent component, Row headerRow, Row technicalHeaderRow, int columnIndex) {
        Cell technicalCell = technicalHeaderRow.createCell(columnIndex, Cell.CELL_TYPE_STRING)
        Cell cell = headerRow.createCell(columnIndex, Cell.CELL_TYPE_STRING)
        setCellComment(technicalCell, "To disable import, add '#' to this row")
        technicalCell.setCellValue(DISABLE_IMPORT)
        cell.setCellValue(DISABLE_IMPORT)
        columnIndex++
        headerRow.createCell(columnIndex).setCellValue(COMPONENT_HEADER_NAME)
        technicalHeaderRow.createCell(columnIndex).setCellValue(COMPONENT_HEADER_NAME)
        return handleComponent(component.createDefaultSubComponent(), headerRow, technicalHeaderRow, ++columnIndex)
    }

    private int addParameterCells(def parmObject, Row headerRow, Row technicalHeaderRow, int columnIndex, Cell cell, XSSFCellStyle columnStyle) {
        columnIndex
    }

    private int addParameterCells(ConstrainedMultiDimensionalParameter multiDimensionalParameter, Row headerRow, Row technicalHeaderRow, int columnIndex, Cell cell, XSSFCellStyle columnStyle) {
        addParameter(multiDimensionalParameter)
        String sheetName = getSheetNameForMDP(new MDPTitleContraints(multiDimensionalParameter.titles, multiDimensionalParameter.constraints))
        setCellComment(cell, sheetName)
        setHyperlink(cell, sheetName)
        return columnIndex
    }

    private int addParameterCells(IParameterObject parmObject, Row headerRow, Row technicalHeaderRow, int columnIndex, Cell c, XSSFCellStyle columnStyle) {
        List<IParameterObjectClassifier> classifiers = parmObject.type.getClassifiers()
        Set writtenParameters = []
        classifiers.each { IParameterObjectClassifier classifier ->
            for (String parmName : classifier.parameterNames) {
                if (!writtenParameters.contains(parmName)) {
                    writtenParameters << parmName
                    Cell technicalCell = technicalHeaderRow.createCell(columnIndex, Cell.CELL_TYPE_STRING)
                    Cell cell = headerRow.createCell(columnIndex, Cell.CELL_TYPE_STRING)
                    technicalHeaderRow.sheet.setDefaultColumnStyle(columnIndex, columnStyle)
                    setFont(cell, 8 as short, true)
                    setFont(technicalCell, 8 as short, true)
                    technicalCell.setCellValue(parmName)
                    cell.setCellValue(getParameterDisplayName(classifier, parmName))
                    Object classifierParameter = classifier.getType(parmName)
                    if (classifierParameter instanceof ConstrainedMultiDimensionalParameter) {
                        addParameter(classifierParameter)
                        String sheetName = getSheetNameForMDP(new MDPTitleContraints(classifierParameter.titles, classifierParameter.constraints))
                        setHyperlink(cell, sheetName)
                    }
                    if (parmObject.class == classifierParameter.class) {
                        // TODO (recursive call with same classifier not supported.)
                    } else {
                        columnIndex = addParameterCells(classifierParameter, headerRow, technicalHeaderRow, ++columnIndex, cell, columnStyle)

                    }
                }
            }
        }
        return columnIndex
    }

    private String getParameterDisplayName(IParameterObjectClassifier classifier, String parmName) {
        String parameterDisplayName = I18NUtils.findParameterDisplayName(classifier.class, parmName)
        return parameterDisplayName ?: parmName
    }

    private String getSheetNameForMDP(MDPTitleContraints mdpTitleContraints, boolean truncate = true) {
        List<MDPTitleContraints> mdpsForConstraints = mdpConstraintsWithTitles.get(mdpTitleContraints.constraints)
        String counter = mdpsForConstraints.indexOf(mdpTitleContraints)
        String name = "MDP${counter}-${mdpTitleContraints.constraints.class.simpleName}"
        return truncate ? name.substring(0, Math.min(name.length(), 31)) : name
    }

    private addParameter(ConstrainedMultiDimensionalParameter multiDimensionalParameter) {
        MDPTitleContraints mdpConstraintWithTitle = new MDPTitleContraints(multiDimensionalParameter.titles, multiDimensionalParameter.constraints)
        if (!mdpConstraintsWithTitles.values().contains(mdpConstraintWithTitle)) {
            if (!mdpConstraintsWithTitles.containsKey(multiDimensionalParameter.constraints)) {
                mdpConstraintsWithTitles.put(multiDimensionalParameter.constraints, [])
            }
            if (!mdpConstraintsWithTitles.get(multiDimensionalParameter.constraints).contains(mdpConstraintWithTitle)) {
                mdpConstraintsWithTitles.get(multiDimensionalParameter.constraints).add(mdpConstraintWithTitle)
            }
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
        anchor.setRow2(cell.row.rowNum + 3);
        Comment comment = cell.row.getSheet().createDrawingPatriarch().createCellComment(anchor)
        comment.setString(new XSSFRichTextString(commentString))
        cell.setCellComment(comment)
    }

    private static setHyperlink(Cell cell, String address) {
        Hyperlink hyperlink = cell.sheet.workbook.creationHelper.createHyperlink(Hyperlink.LINK_DOCUMENT)
        hyperlink.setAddress("'$address'!A1")
        cell.setHyperlink(hyperlink)
    }


    class MDPTitleContraints {
        List<String> titles
        IMultiDimensionalConstraints constraints

        MDPTitleContraints(List<String> titles, IMultiDimensionalConstraints constraints) {
            this.titles = titles
            this.constraints = constraints
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            MDPTitleContraints that = (MDPTitleContraints) o

            if (constraints != that.constraints) return false
            if (titles != that.titles) return false

            return true
        }

        int hashCode() {
            int result
            result = (titles != null ? titles.hashCode() : 0)
            result = 31 * result + (constraints != null ? constraints.hashCode() : 0)
            return result
        }
    }

}