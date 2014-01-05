package org.pillarone.riskanalytics.application.search

import org.apache.commons.lang.StringUtils
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.user.Person

class AllFieldsFilterTests extends GroovyTestCase {

    void testSearchByOwner() {
        Resource resource = new Resource('testName', null)
        resource.creator  = new Person(username: 'user')
        assert ! new AllFieldsFilter(query: 'use').accept(resource)
        assert   new AllFieldsFilter(query: 'user').accept(resource)
        // Must not find 'user' in name of resource, nor 'testName' in username
        assert ! new AllFieldsFilter(query: 'name:user').accept(resource)
        assert ! new AllFieldsFilter(query: 'owner:testName').accept(resource)
    }


    void testSearchParameterizations() {
        Parameterization parameterization = new Parameterization('testName')
        verifyOnlyNameSpecificOrGenericFiltersMatch('test',parameterization)

        assert ! new AllFieldsFilter(query: 'not found').accept(parameterization)
        parameterization.name = 'some other name'
        parameterization.tags = [new Tag(name: 'testName'), new Tag(name: 'secondTag')]
        assert   new AllFieldsFilter(query: 'testName').accept(parameterization)
        assert   new AllFieldsFilter(query: 'tag:testName').accept(parameterization)
        assert   new AllFieldsFilter(query: 'tag:unknown OR name:other').accept(parameterization)
        assert ! new AllFieldsFilter(query: 'tag:unknown AND name:other').accept(parameterization)
        assert ! new AllFieldsFilter(query: 'unknown AND other').accept(parameterization)
    }


    void testSearchSimulations() {
        Simulation simulation = new Simulation('testName')
        verifyOnlyNameSpecificOrGenericFiltersMatch('test', simulation)

        assert ! new AllFieldsFilter(query: 'not found').accept(simulation)
        assert ! new AllFieldsFilter(query: 'name:not found').accept(simulation)

        simulation.name = 'some other name'
        simulation.tags = [new Tag(name: 'testName'), new Tag(name: 'secondTag')]
        assert   new AllFieldsFilter(query: 'testName').accept(simulation)        //should match tag
        assert ! new AllFieldsFilter(query: 'name:testName').accept(simulation)   //name-specific; should not match tag
        assert   new AllFieldsFilter(query: 'tag:testName').accept(simulation)    //should match tag
        assert ! new AllFieldsFilter(query: 'tag:other').accept(simulation)       //tag-specific; should not match name

        Parameterization parameterization = new Parameterization('PARAM_NAME')
        simulation.parameterization = parameterization
        assert   new AllFieldsFilter(query: 'PARAM_NAME').accept(simulation)     //should match on pn
        assert   new AllFieldsFilter(query: 'name:param_').accept(simulation)    //should match on pn

        simulation.template = new ResultConfiguration('TEMPLATE_NAME')
        assert   new AllFieldsFilter(query: 'TEMPLATE_NAME').accept(simulation)  //should match tmpl name
        assert   new AllFieldsFilter(query: 'name:template').accept(simulation)  //should match on tmpl

        parameterization.tags = [new Tag(name: 'paramTestName')]
        assert ! new AllFieldsFilter(query: 'paramTestName').accept(simulation)  //should not match pn's tags
        assert ! new AllFieldsFilter(query: 'tag:paramtest').accept(simulation)  //should not match pn's tags
    }

    void testSearchResources() {
        Resource resource = new Resource('testName', null)
        verifyOnlyNameSpecificOrGenericFiltersMatch('TEST',resource)

        assert ! new AllFieldsFilter(query: 'not found').accept(resource)
        assert ! new AllFieldsFilter(query: 'tag:found').accept(resource)
        assert ! new AllFieldsFilter(query: 'state:found').accept(resource)
        assert ! new AllFieldsFilter(query: 'dealid:found').accept(resource)
        assert ! new AllFieldsFilter(query: 'owner:found').accept(resource)

        resource.name = 'some other name'
        resource.tags = [new Tag(name: 'testName'), new Tag(name: 'secondTag')]
        assert   new AllFieldsFilter(query: 'testName').accept(resource)
        assert   new AllFieldsFilter(query: 'tag:testName').accept(resource)
        assert ! new AllFieldsFilter(query: 'name:testName').accept(resource)
        assert   new AllFieldsFilter(query: 'name:testName OR TAG:TESTNAME').accept(resource)
        assert   new AllFieldsFilter(query: 'name:other AND tag:second').accept(resource)
    }

    void testSearchWithMultipleValues(){
        Resource resource1 = new Resource('firstName', null)
        resource1.versionNumber = new VersionNumber('1')
        Resource resource2 = new Resource('secondName', null)
        resource2.versionNumber = new VersionNumber('2')
        Resource resource3 = new Resource('firstName', null)
        resource3.versionNumber = new VersionNumber('2')
        AllFieldsFilter filter = new AllFieldsFilter(query: 'firstName v1 OR secondName OR thirdName')
        assert filter.accept(resource1)
        assert filter.accept(resource2)
        assert !filter.accept(resource3)

        assert   new AllFieldsFilter(query: 'name:first AND name:v1').accept(resource1)
        assert ! new AllFieldsFilter(query: 'name:first AND name:v2').accept(resource1)
        assert   new AllFieldsFilter(query: 'name:first OR name:v2').accept(resource1)

    }
    // Supply sim, pn or resource with given name-fragment
    // Checks that only a generic filters or a name-specific one will match fragment via the name
    private static void verifyOnlyNameSpecificOrGenericFiltersMatch( String nameFragment, def modellingItem){

        //Checks are pointless unless item name contains supplied name fragment
        assert StringUtils.containsIgnoreCase(modellingItem.name, nameFragment)

        //Generic filter should match by name
        assert  (new AllFieldsFilter(query: nameFragment)).accept(modellingItem)
        //Name-specific filter should match by name
        assert  (new AllFieldsFilter(query: 'name:'  +nameFragment)).accept(modellingItem)
        assert  (new AllFieldsFilter(query: 'n:'     +nameFragment)).accept(modellingItem)
        //Other-column-specific filters should not match by name
        assert !(new AllFieldsFilter(query: 'dealid:'+nameFragment)).accept(modellingItem)
        assert !(new AllFieldsFilter(query: 'd:'     +nameFragment)).accept(modellingItem)
        assert !(new AllFieldsFilter(query: 'owner:' +nameFragment)).accept(modellingItem)
        assert !(new AllFieldsFilter(query: 'o:'     +nameFragment)).accept(modellingItem)
        assert !(new AllFieldsFilter(query: 'state:' +nameFragment)).accept(modellingItem)
        assert !(new AllFieldsFilter(query: 's:'     +nameFragment)).accept(modellingItem)
        assert !(new AllFieldsFilter(query: 'tag:'   +nameFragment)).accept(modellingItem)
        assert !(new AllFieldsFilter(query: 't:'     +nameFragment)).accept(modellingItem)
    }
}
