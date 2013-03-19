package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.pillarone.riskanalytics.core.example.component.ExampleResource
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.application.example.resource.ApplicationResource

/**
 * Created with IntelliJ IDEA.
 * User: oandersson
 * Date: 11/16/12
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
class ResourceTableTreeModelTests extends GroovyTestCase {

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

    void testFindPath() {
        Resource resource = new Resource("testTree", ExampleResource)
        resource.load()

        ResourceTreeBuilder builder = new ResourceTreeBuilder(resource)
        ResourceTableTreeModel model = new ResourceTableTreeModel(builder)

        assertNotNull(model.findNode(['parmInteger'] as String[]))
    }

    void testFindPathWithStructure() {
        Resource resource = new Resource("testTreeWithStructure", ApplicationResource)
        resource.load()

        ResourceTreeBuilder builder = new ResourceTreeBuilder(resource)
        ResourceTableTreeModel model = new ResourceTableTreeModel(builder)

        assertNotNull(model.findNode(['parmInteger'] as String[]))
    }
}
