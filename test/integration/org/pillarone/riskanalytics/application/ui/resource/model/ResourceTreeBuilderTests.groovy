package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.IntegerTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.SimpleValueParameterizationTableTreeNode
import org.pillarone.riskanalytics.core.example.component.ExampleResource
import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory

class ResourceTreeBuilderTests extends GroovyTestCase {

    @Override
    protected void setUp() {
        Resource resource = new Resource("testTree", ExampleResource)
        resource.addParameter(ParameterHolderFactory.getHolder("parmInteger", 0, 99))
        resource.addParameter(ParameterHolderFactory.getHolder("parmString", 0, "String"))
        resource.save()
    }

    void testTree() {

        Resource resource = new Resource("testTree", ExampleResource)
        ResourceTreeBuilder builder = new ResourceTreeBuilder(resource)


        assertEquals(2, builder.root.childCount)
        IntegerTableTreeNode intNode = builder.root.getChildAt(0)
        assertEquals("parmInteger", intNode.name)
        SimpleValueParameterizationTableTreeNode stringNode = builder.root.getChildAt(1)
        assertEquals("parmString", stringNode.name)
    }

}
