package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.application.ui.util.TableDataParser
import org.pillarone.riskanalytics.application.ui.base.action.DefaultColumnMapping;

class TableDataParserTests extends GroovyTestCase {

    void testParseTableData() {

        String stringData = "a\tb\tc\n1\t\t2.0"
        TableDataParser parser = new TableDataParser(locale: Locale.ENGLISH, columnMapping: new DefaultColumnMapping())

        List tableData = parser.parseTableData(stringData)

        assertNotNull "no result", tableData
        assertEquals "# rows", 2, tableData.size()
        assertEquals "# cols in row 1", 3, tableData[0].size()
        assertEquals "# cols in row 2", 3, tableData[1].size()

        assertEquals "a", tableData[0][0]
        assertEquals "b", tableData[0][1]
        assertEquals "c", tableData[0][2]

        assertEquals 1.0, tableData[1][0]
        assertEquals "", tableData[1][1]
        assertEquals 2.0, tableData[1][2]

    }

    void testBigNumbers() {
        String stringData = "8226285.41\t0.000994753919160041\n8765133.33\t0.00093910825660648\n9183573.41\t0.000886575363657438\n9766056.49\t0.000836981114706092\n10370771.7360482\t0.000790161124589739"
        TableDataParser parser = new TableDataParser(locale: Locale.ENGLISH, columnMapping: new DefaultColumnMapping())

        List tableData = parser.parseTableData(stringData)
        assertEquals 8226285.41, tableData[0][0]
        assertEquals 0.000994753919160041, tableData[0][1]
        assertEquals 0.00093910825660648, tableData[1][1]
    }

    void testNumbersWithBlanks() {
        String stringData = " 85.41 "
        TableDataParser parser = new TableDataParser(locale: Locale.ENGLISH, columnMapping: new DefaultColumnMapping())
        List tableData = parser.parseTableData(stringData)
        assertEquals 85.41, tableData[0][0]

    }
}