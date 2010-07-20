package org.pillarone.riskanalytics.application.output.structure

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure

class DefaultResultStructureBuilderTests extends GroovyTestCase {

    void testCreate() {
        ResultStructure structure = DefaultResultStructureBuilder.create("test", ApplicationModel)
        println structure.mappings.keySet()
        assertEquals 6, structure.mappings.size()
    }
}
