package org.pillarone.riskanalytics.application.output.structure

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure

class DefaultResultStructureBuilderTests extends GroovyTestCase {

    void testCreate() {
        ResultStructure structure = DefaultResultStructureBuilder.create("test", ApplicationModel)
        assertEquals 6, structure.mappings.size()
        assertTrue structure.mappings.keySet().every { it.startsWith("Application:")}
    }
}
