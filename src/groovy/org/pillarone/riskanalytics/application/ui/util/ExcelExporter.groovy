package org.pillarone.riskanalytics.application.ui.util

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.pillarone.riskanalytics.core.output.PathMapping

class ExcelExporter {

    public String tabName = 'Data'
    public List headers = 'period iteration path value'.tokenize()
    SXSSFWorkbook workbook = new SXSSFWorkbook()

    def exportResults(List results) {
        Sheet sheet = workbook.createSheet(tabName)

        Row header = sheet.createRow(0)
        headers.eachWithIndex {String text, int i ->
            addCell(header, i as short, text)
        }

        results.eachWithIndex {def result, int i ->
            Row row = sheet.createRow(i + 1)
            headers.eachWithIndex {String propName, int colNum ->
                def value = result[propName]
                if (value != null) addCell(row, colNum as short, value)
            }
        }
    }

    /**
     * exports a map with lists ["column":[1,2,3],"column2":[4,3,2]]. Each list represents a column in the sheet
     */
    def export(Map table) {
        Sheet sheet = workbook.createSheet(tabName)

        Row header = sheet.createRow(0)
        List headers = table.keySet() as List
        headers.eachWithIndex {String text, int i ->
            addCell(header, i as short, text)
        }

        int maxRowCount = table.values().collect {it.size()}.max()

        maxRowCount.times {int rowIndex ->
            Row row = sheet.createRow(rowIndex + 1)
            headers.eachWithIndex {String propName, int colNum ->
                def value = table[propName][rowIndex]
                if (value != null) addCell(row, colNum as short, value)
            }
        }

    }

    /**
     * adds an extra tab with the information in the content array
     */
    public void addTab(String tabName, List<List<String>> content) {
        Sheet sheet = workbook.createSheet(tabName)

        content.eachWithIndex {List line, int i ->
            Row row = sheet.createRow(i + 1)
            line.eachWithIndex {def value, int colNum ->
                if (value != null) addCell(row, colNum as short, value)
            }
        }
    }

    public void writeWorkBook(OutputStream out) {
        workbook.write out
    }

    void addCell(Row row, short col, value) {
        Cell cell = row.createCell(col)
        if (value.getClass() in [double, Double, int, Integer, float, Float]) {
            cell.setCellType Cell.CELL_TYPE_NUMERIC
        } else {
            cell.setCellType Cell.CELL_TYPE_STRING
        }
        cell.setCellValue(value)
    }

    void addCell(Row row, short col, PathMapping value) {
        Cell cell = row.createCell(col, Cell.CELL_TYPE_STRING)
        cell.setCellValue(value.pathName)
    }
}
