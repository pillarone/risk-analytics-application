package org.pillarone.riskanalytics.application.fileimport

import org.pillarone.riskanalytics.application.output.structure.StructureMapping
import org.pillarone.riskanalytics.application.output.structure.ResultStructureDAO


class ResultStructureImportServiceTests extends GroovyTestCase {

    void testImport() {
        int initialMappings = StructureMapping.count()

        ResultStructureImportService.importDefaults()

        assertEquals 3, ResultStructureDAO.count()
        assertTrue StructureMapping.count() > initialMappings
    }
}
