package org.pillarone.riskanalytics.application.ui.util

import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.pillarone.riskanalytics.core.output.PathMapping

class ExcelExporter {

    public String tabName = 'Data'
    public List headers = 'period iteration path value'.tokenize()
    HSSFWorkbook workbook = new HSSFWorkbook()

    def exportResults(List results) {
        HSSFSheet sheet = workbook.createSheet(tabName)

        HSSFRow header = sheet.createRow(0)
        headers.eachWithIndex {String text, int i ->
            addCell(header, i as short, text)
        }

        results.eachWithIndex {def result, int i ->
            HSSFRow row = sheet.createRow(i + 1)
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
        HSSFSheet sheet = workbook.createSheet(tabName)

        HSSFRow header = sheet.createRow(0)
        List headers = table.keySet() as List
        headers.eachWithIndex {String text, int i ->
            addCell(header, i as short, text)
        }

        int maxRowCount = table.values().collect {it.size()}.max()

        maxRowCount.times {int rowIndex ->
            HSSFRow row = sheet.createRow(rowIndex + 1)
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
        HSSFSheet sheet = workbook.createSheet(tabName)

        content.eachWithIndex {List line, int i ->
            HSSFRow row = sheet.createRow(i + 1)
            line.eachWithIndex {def value, int colNum ->
                if (value != null) addCell(row, colNum as short, value)
            }
        }
    }

    public void writeWorkBook(OutputStream out) {
        workbook.write out
    }

    void addCell(HSSFRow row, short col, value) {
        HSSFCell cell = row.createCell(col)
        if (value.getClass() in [double, Double, int, Integer, float, Float]) {
            cell.setCellType HSSFCell.CELL_TYPE_NUMERIC
        } else {
            cell.setCellType HSSFCell.CELL_TYPE_STRING
        }
        cell.setCellValue(value)
    }

    void addCell(HSSFRow row, short col, PathMapping value) {
        HSSFCell cell = row.createCell(col, HSSFCell.CELL_TYPE_STRING)
        cell.setCellValue(value.pathName)
    }
}
