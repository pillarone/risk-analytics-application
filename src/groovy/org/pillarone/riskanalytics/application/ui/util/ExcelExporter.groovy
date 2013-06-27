package org.pillarone.riskanalytics.application.ui.util

import org.apache.poi.ss.SpreadsheetVersion
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.ClientAnchor
import org.apache.poi.ss.usermodel.Comment
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.Drawing
import org.apache.poi.ss.usermodel.RichTextString
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFComment
import org.pillarone.riskanalytics.core.output.PathMapping

class ExcelExporter {

    public String tabName = 'Data'
    public List headers = 'period iteration path value'.tokenize()
    SXSSFWorkbook workbook = new SXSSFWorkbook()

    def exportResults(List results, List<String> displayPaths = []) {
        Sheet sheet = workbook.createSheet(tabName)

        Row header = sheet.createRow(0)
        headers.eachWithIndex { String text, int i ->
            Cell headerCell = addCell(header, i, text)
            if (i > 0 && displayPaths.size() > 0) {
                addComment(i, headerCell, displayPaths, sheet)
            }
        }

        results.eachWithIndex { def result, int i ->
            Row row = sheet.createRow(i + 1)
            headers.eachWithIndex { String propName, int colNum ->
                def value = result[propName]
                if (value != null) addCell(row, colNum, value, result)
            }
        }
    }

    private void addComment(int i, Cell headerCell, List displayPaths, Sheet sheet) {
        CreationHelper factory = workbook.getCreationHelper();
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(i);
        anchor.setCol2(i + 3);
        anchor.setRow1(0);
        anchor.setRow2(3);
        Comment comment = drawing.createCellComment(anchor);
        RichTextString str = factory.createRichTextString(displayPaths[(i - 1) % displayPaths.size()]);
        comment.setString(str);
        headerCell.setCellComment(comment);
    }

    /**
     * exports a map with lists ["column":[1,2,3],"column2":[4,3,2]]. Each list represents a column in the sheet
     */
    def export(Map table) {
        Sheet sheet = workbook.createSheet(tabName)

        Row header = sheet.createRow(0)
        List headers = table.keySet() as List
        headers.eachWithIndex { String text, int i ->
            addCell(header, i, text)
        }

        int maxRowCount = table.values().collect { it.size() }.max()

        maxRowCount.times { int rowIndex ->
            Row row = sheet.createRow(rowIndex + 1)
            headers.eachWithIndex { String propName, int colNum ->
                def value = table[propName][rowIndex]
                if (value != null) addCell(row, colNum, value)
            }
        }

    }

    /**
     * adds an extra tab with the information in the content array
     */
    public void addTab(String tabName, List<List<String>> content) {
        Sheet sheet = workbook.createSheet(tabName)

        content.eachWithIndex { List line, int i ->
            Row row = sheet.createRow(i + 1)
            line.eachWithIndex { def value, int colNum ->
                if (value != null) addCell(row, colNum, value)
            }
        }
    }

    public void writeWorkBook(OutputStream out) {
        workbook.write out
    }

    private Cell addCell(Row row, int col, value, result = [:]) {
        Cell cell = row.createCell(col)
        if (value.getClass() in [double, Double, int, Integer, float, Float]) {
            cell.setCellType Cell.CELL_TYPE_NUMERIC
        } else {
            cell.setCellType Cell.CELL_TYPE_STRING
        }
        cell.setCellValue(value)
        return cell
    }

    private Cell addCell(Row row, int col, PathMapping value, result = [:]) {
        Cell cell = row.createCell(col, Cell.CELL_TYPE_STRING)
        cell.setCellValue("${value.pathName}:${result['field']}")
        return cell
    }
}
