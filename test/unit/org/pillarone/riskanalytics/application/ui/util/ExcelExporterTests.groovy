package org.pillarone.riskanalytics.application.ui.util

import org.apache.poi.ss.usermodel.Comment
import org.apache.poi.ss.usermodel.Sheet

class ExcelExporterTests extends GroovyTestCase {

    void testExportWithComment() {
        ExcelExporter exporter = new ExcelExporter()
        exporter.headers = ['iteration','property1','property2']
        exporter.exportResults([new TestStructure(property1: 'a', property2: 'a1'), new TestStructure(property1: 'b', property2: 'b1')],['comment1','comment2'])
        Sheet sheet = exporter.workbook.getSheet(exporter.tabName)
        Comment comment = sheet.getRow(0).getCell(1).cellComment
        assert 'comment1' == comment.getString().toString()
        comment = sheet.getRow(0).getCell(2).cellComment
        assert 'comment2' == comment.getString().toString()
    }
}

class TestStructure {
    String iteration
    String property1
    String property2
}
