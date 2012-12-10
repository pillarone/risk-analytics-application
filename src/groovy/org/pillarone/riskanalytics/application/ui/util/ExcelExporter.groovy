package org.pillarone.riskanalytics.application.ui.util

import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.pillarone.riskanalytics.core.output.PathMapping

class ExcelExporter {

    public String tabName = 'Data'
    public List headers = 'period iteration path value'.tokenize()
    XSSFWorkbook workbook = new XSSFWorkbook()

    def exportResults(List results) {
        XSSFSheet sheet = workbook.createSheet(tabName)

        XSSFRow header = sheet.createRow(0)
        headers.eachWithIndex {String text, int i ->
            addCell(header, i as short, text)
        }

        results.eachWithIndex {def result, int i ->
            XSSFRow row = sheet.createRow(i + 1)
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
        XSSFSheet sheet = workbook.createSheet(tabName)

        XSSFRow header = sheet.createRow(0)
        List headers = table.keySet() as List
        headers.eachWithIndex {String text, int i ->
            addCell(header, i as short, text)
        }

        int maxRowCount = table.values().collect {it.size()}.max()

        maxRowCount.times {int rowIndex ->
            XSSFRow row = sheet.createRow(rowIndex + 1)
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
        XSSFSheet sheet = workbook.createSheet(tabName)

        content.eachWithIndex {List line, int i ->
            XSSFRow row = sheet.createRow(i + 1)
            line.eachWithIndex {def value, int colNum ->
                if (value != null) addCell(row, colNum as short, value)
            }
        }
    }

    public void writeWorkBook(OutputStream out) {
        workbook.write out
    }

    void addCell(XSSFRow row, short col, value) {
        XSSFCell cell = row.createCell(col)
        if (value.getClass() in [double, Double, int, Integer, float, Float]) {
            cell.setCellType XSSFCell.CELL_TYPE_NUMERIC
        } else {
            cell.setCellType XSSFCell.CELL_TYPE_STRING
        }
        cell.setCellValue(value)
    }

    void addCell(XSSFRow row, short col, PathMapping value) {
        XSSFCell cell = row.createCell(col, XSSFCell.CELL_TYPE_STRING)
        cell.setCellValue(value.pathName)
    }
}
