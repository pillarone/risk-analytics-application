package org.pillarone.riskanalytics.application.search

import grails.util.Holders
import models.core.CoreModel
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

import static org.junit.Assert.*

class ModellingItemSearchServiceTests {

    ModellingItemSearchService modellingItemSearchService

    @Before
    void setUp() {
        modellingItemSearchService = Holders.grailsApplication.mainContext.getBean(ModellingItemSearchService)
        FileImportService.importModelsIfNeeded(['Core', 'Application'])
        modellingItemSearchService.refresh()
    }

    @Test
    void testService() {
        final List<ModellingItem> results = modellingItemSearchService.search([new AllFieldsFilter(query: "Parameters")])

        assertEquals(4, results.size())

        Parameterization parameterization = new Parameterization("MyParameters", CoreModel)
        ParameterizationDAO.withNewSession {
            parameterization.save()
        }

        results = modellingItemSearchService.search([new AllFieldsFilter(query: "Parameters")])

        assertEquals(5, results.size())

        assertNotNull(results.find { it.name == parameterization.name })

        ParameterizationDAO.withNewSession {
            parameterization.delete()
        }

        results = modellingItemSearchService.search([new AllFieldsFilter(query: "Parameters")])

        assertEquals(4, results.size())

        assertNull(results.find { it.name == parameterization.name })

    }

    @Test
    void testRenameParametrization() {

        Parameterization parameterization = new Parameterization("MyParameters", CoreModel)
        ParameterizationDAO.withNewSession {
            parameterization.save()
        }
        List<ModellingItem> results = modellingItemSearchService.search([new AllFieldsFilter(query: "MyParameters")])
        assertEquals(1, results.size())

        ParameterizationDAO.withNewSession {
            parameterization.rename("RenamedParameters")
        }
        results = modellingItemSearchService.search([new AllFieldsFilter(query: "MyParameters")])
        assertEquals(0, results.size())
        results = modellingItemSearchService.search([new AllFieldsFilter(query: "RenamedParameters")])
        assertEquals(1, results.size())
    }
}
