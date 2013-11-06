package org.pillarone.riskanalytics.application.ui.resource.model

import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.application.example.resource.ApplicationResource
import org.pillarone.riskanalytics.core.example.component.ExampleResource
import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory

import static org.junit.Assert.assertNotNull

class ResourceTableTreeModelTests {

    @Before
    void setUp() {
        Resource resource = new Resource("testTree", ExampleResource)
        resource.addParameter(ParameterHolderFactory.getHolder("parmInteger", 0, 99))
        resource.addParameter(ParameterHolderFactory.getHolder("parmString", 0, "String"))
        resource.save()

        Resource resource2 = new Resource("testTreeWithStructure", ApplicationResource)
        resource2.addParameter(ParameterHolderFactory.getHolder("parmInteger", 0, 99))
        resource2.addParameter(ParameterHolderFactory.getHolder("parmString", 0, "String"))
        resource2.save()
    }

    @Test
    void testFindPath() {
        Resource resource = new Resource("testTree", ExampleResource)
        resource.load()

        ResourceTreeBuilder builder = new ResourceTreeBuilder(resource)
        ResourceTableTreeModel model = new ResourceTableTreeModel(builder)

        assertNotNull(model.findNode(['parmInteger'] as String[]))
    }

    @Test
    void testFindPathWithStructure() {
        Resource resource = new Resource("testTreeWithStructure", ApplicationResource)
        resource.load()

        ResourceTreeBuilder builder = new ResourceTreeBuilder(resource)
        ResourceTableTreeModel model = new ResourceTableTreeModel(builder)

        assertNotNull(model.findNode(['parmInteger'] as String[]))
    }
}
