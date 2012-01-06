package org.pillarone.riskanalytics.application.output.result.item

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement


class CustomTableTests extends GroovyTestCase {

    void testSaveLoad() {

        CustomTable table = new CustomTable("Name", ApplicationModel)
        table.tableData = [
                ["string", new OutputElement(categoryMap: ["a":"b"])],
                ["string2", new OutputElement(categoryMap: ["c":"d"])]
        ]

        table.save()

        table = new CustomTable("Name", ApplicationModel)
        table.load()
        assertEquals(2, table.tableData.size())
        assertEquals("string", table.tableData[0][0])
        assertEquals("string2", table.tableData[1][0])

        OutputElement outputElement = table.tableData[0][1]
        assertEquals("b", outputElement.categoryMap["a"])

        outputElement = table.tableData[1][1]
        assertEquals("d", outputElement.categoryMap["c"])

    }
}
