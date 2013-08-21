package org.pillarone.riskanalytics.application.search

import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.user.Person

class AllFieldsFilterTests extends GroovyTestCase {

    void testSearchParameterizations() {
        AllFieldsFilter filter = new AllFieldsFilter(query: 'test')
        Parameterization parameterization = new Parameterization('testName')
        assert filter.accept(parameterization)
        filter.query = 'not found'
        assert !filter.accept(parameterization)
        parameterization.name = 'some other name'
        filter.query = 'testName'
        parameterization.tags = [new Tag(name: 'testName'), new Tag(name: 'secondTag')]
        assert filter.accept(parameterization)
    }

    void testSearchSimulations() {
        AllFieldsFilter filter = new AllFieldsFilter(query: 'test')
        Simulation simulation = new Simulation('testName')
        assert filter.accept(simulation)
        filter.query = 'not found'
        assert !filter.accept(simulation)
        simulation.name = 'some other name'
        filter.query = 'testName'
        simulation.tags = [new Tag(name: 'testName'), new Tag(name: 'secondTag')]
        assert filter.accept(simulation)
        Parameterization parameterization = new Parameterization('PARAM_NAME')
        simulation.parameterization = parameterization
        filter.query = 'PARAM_NAME'
        assert filter.accept(simulation)
        simulation.template = new ResultConfiguration('TEMPLATE_NAME')
        filter.query = 'TEMPLATE_NAME'
        assert filter.accept(simulation)
        parameterization.tags = [new Tag(name: 'paramTestName')]
        filter.query = 'paramTestName'
        assert !filter.accept(simulation)
    }

    void testSearchResources() {
        AllFieldsFilter filter = new AllFieldsFilter(query: 'test')
        Resource resource = new Resource('testName', null)
        assert filter.accept(resource)
        filter.query = 'not found'
        assert !filter.accept(resource)
        resource.name = 'some other name'
        filter.query = 'testName'
        resource.tags = [new Tag(name: 'testName'), new Tag(name: 'secondTag')]
        assert filter.accept(resource)
    }

    void testSearchByOwner() {
        AllFieldsFilter filter = new AllFieldsFilter(query: 'user')
        Resource resource = new Resource('testName', null)
        resource.creator = new Person(username: 'user')
        assert filter.accept(resource)
    }
}
