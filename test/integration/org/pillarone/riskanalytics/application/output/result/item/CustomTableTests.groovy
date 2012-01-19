package org.pillarone.riskanalytics.application.output.result.item

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement
import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy
import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMapping
import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.simulation.item.Parameterization


class CustomTableTests extends GroovyTestCase {

    @Override
    protected void setUp() {
        if(PathMapping.countByPathName("mypath") == 0) {
            new PathMapping(pathName: "mypath").save()
        }
        if(FieldMapping.countByFieldName("myfield") == 0) {
            new FieldMapping(fieldName: "myfield").save()
        }
        if(CollectorMapping.countByCollectorName(AggregatedCollectingModeStrategy.IDENTIFIER) == 0) {
            new CollectorMapping(collectorName: AggregatedCollectingModeStrategy.IDENTIFIER).save()
        }

        new ParameterizationImportService().compareFilesAndWriteToDB(['Application'])
    }

    void testSaveLoad() {

        Parameterization parameterization = new Parameterization("ApplicationParameters", ApplicationModel)

        CustomTable table = new CustomTable("Name", ApplicationModel)
        table.parameterization = parameterization
        table.tableData = [
                ["string", new DataCellElement(categoryMap: ["a":"=A1"], period: 0, path: "mypath", field: "myfield", collector: AggregatedCollectingModeStrategy.IDENTIFIER, templatePath: "")],
                ["string2", new DataCellElement(categoryMap: ["c":"=A2"], period: 0, path: "mypath", field: "myfield", collector: AggregatedCollectingModeStrategy.IDENTIFIER, templatePath: "")]
        ]

        table.save()

        table = new CustomTable("Name", ApplicationModel)
        table.load()
        assertEquals(2, table.tableData.size())
        assertEquals("string", table.tableData[0][0])
        assertEquals("string2", table.tableData[1][0])

        DataCellElement outputElement = table.tableData[0][1]
        assertEquals("=A1", outputElement.categoryMap["a"])

        outputElement = table.tableData[1][1]
        assertEquals("=A2", outputElement.categoryMap["c"])

    }
}
