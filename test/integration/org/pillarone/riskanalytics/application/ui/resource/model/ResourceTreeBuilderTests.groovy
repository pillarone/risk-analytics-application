package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.IntegerTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.SimpleValueParameterizationTableTreeNode
import org.pillarone.riskanalytics.core.example.component.ExampleResource
import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.example.resource.ApplicationResource

class ResourceTreeBuilderTests extends GroovyTestCase {

    @Override
    protected void setUp() {
        Resource resource = new Resource("testTree", ExampleResource)
        resource.addParameter(ParameterHolderFactory.getHolder("parmInteger", 0, 99))
        resource.addParameter(ParameterHolderFactory.getHolder("parmString", 0, "String"))
        resource.save()

        Resource resource2 = new Resource("testTreeWithStructure", ApplicationResource)
        resource2.addParameter(ParameterHolderFactory.getHolder("parmInteger", 0, 99))
        resource2.addParameter(ParameterHolderFactory.getHolder("parmString", 0, "String"))
        resource2.save()
    }

    void testTree() {

        Resource resource = new Resource("testTree", ExampleResource)
        resource.load()
        ResourceTreeBuilder builder = new ResourceTreeBuilder(resource)


        assertEquals(2, builder.root.childCount)
        IntegerTableTreeNode intNode = builder.root.getChildAt(0)
        assertEquals("parmInteger", intNode.name)
        SimpleValueParameterizationTableTreeNode stringNode = builder.root.getChildAt(1)
        assertEquals("parmString", stringNode.name)
    }

    void testTreeWithStructure() {

        Resource resource = new Resource("testTreeWithStructure", ApplicationResource)
        resource.load()
        ResourceTreeBuilder builder = new ResourceTreeBuilder(resource)


        assertEquals(2, builder.root.childCount)
        SimpleTableTreeNode intNode = builder.root.getChildAt(0)
        assertEquals(1, intNode.childCount)
        assertTrue(intNode.getChildAt(0) instanceof IntegerTableTreeNode)
        assertEquals("integer", intNode.name)
        SimpleTableTreeNode stringNode = builder.root.getChildAt(1)
        assertEquals(1, stringNode.childCount)
        assertTrue(stringNode.getChildAt(0) instanceof SimpleValueParameterizationTableTreeNode)
        assertEquals("string", stringNode.name)
    }


}
