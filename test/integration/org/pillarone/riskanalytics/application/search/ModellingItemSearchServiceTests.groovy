package org.pillarone.riskanalytics.application.search

import grails.util.Holders
import models.core.CoreModel
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class ModellingItemSearchServiceTests extends GroovyTestCase {

    ModellingItemSearchService modellingItemSearchService

    void setUp() {
        modellingItemSearchService = Holders.grailsApplication.mainContext.getBean(ModellingItemSearchService)
        FileImportService.importModelsIfNeeded(['Core', 'Application'])
        modellingItemSearchService.refresh()
    }

    void testService() {
        final List<ModellingItem> results = modellingItemSearchService.search([new AllFieldsFilter(query: "Parameters")])

        assertEquals(4, results.size())

        Parameterization parameterization = new Parameterization("MyParameters", CoreModel)
        parameterization.save()

        results = modellingItemSearchService.search([new AllFieldsFilter(query: "Parameters")])

        assertEquals(5, results.size())

        assertNotNull(results.find { it.name == parameterization.name})

        parameterization.delete()
        results = modellingItemSearchService.search([new AllFieldsFilter(query: "Parameters")])

        assertEquals(4, results.size())

        assertNull(results.find { it.name == parameterization.name})

    }

    void testRenameParametrization() {

        Parameterization parameterization = new Parameterization("MyParameters", CoreModel)
        parameterization.save()
        List<ModellingItem> results = modellingItemSearchService.search([new AllFieldsFilter(query: "MyParameters")])
        assertEquals(1, results.size())

        parameterization.rename("RenamedParameters")
        results = modellingItemSearchService.search([new AllFieldsFilter(query: "MyParameters")])
        assertEquals(0, results.size())
        results = modellingItemSearchService.search([new AllFieldsFilter(query: "RenamedParameters")])
        assertEquals(1, results.size())
    }
}
