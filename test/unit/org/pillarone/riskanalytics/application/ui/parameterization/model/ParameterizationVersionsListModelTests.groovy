package org.pillarone.riskanalytics.application.ui.parameterization.model

import groovy.mock.interceptor.StubFor
import models.core.CoreModel
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class ParameterizationVersionsListModelTests extends GroovyTestCase {

    void testLoad() {
        LocaleResources.setTestMode()
        ParameterizationVersionsListModel listModel = new ParameterizationVersionsListModel()
        assertEquals 0, listModel.size

        Parameterization parameterization = new Parameterization('CoreParameters')
        parameterization.valid = true
        Parameterization parameterization2 = new Parameterization('name')
        parameterization2.valid = true


        StubFor factoryStub = new StubFor(ModellingItemFactory)
        factoryStub.demand.getParameterizationsForModel {Class model -> [parameterization, parameterization2]}

        factoryStub.use {
            listModel.load(CoreModel, 'CoreParameters')
            assertEquals 1, listModel.size
        }
        LocaleResources.clearTestMode()
    }

    void testGetElementAt() {
        LocaleResources.setTestMode()
        ParameterizationVersionsListModel listModel = new ParameterizationVersionsListModel()
        assertEquals 0, listModel.size

        Parameterization parameterization = new Parameterization('CoreParameters')
        parameterization.valid = true
        Parameterization parameterization2 = new Parameterization('CoreParameters')
        parameterization2.valid = true
        parameterization2.versionNumber = new VersionNumber('2')

        StubFor factoryStub = new StubFor(ModellingItemFactory)
        factoryStub.demand.getParameterizationsForModel {Class model -> [parameterization, parameterization2]}

        factoryStub.use {
            listModel.load(CoreModel, 'CoreParameters')
            assertEquals 2, listModel.size
            assertEquals 'v2', listModel.getElementAt(0)
            assertEquals 'v1', listModel.getElementAt(1)
        }
        LocaleResources.clearTestMode()
    }

    void testSelection() {
        LocaleResources.setTestMode()
        ParameterizationVersionsListModel listModel = new ParameterizationVersionsListModel()
        assertEquals 0, listModel.size

        Parameterization parameterization = new Parameterization('CoreParameters')
        parameterization.valid = true

        StubFor factoryStub = new StubFor(ModellingItemFactory)
        factoryStub.demand.getParameterizationsForModel {Class model -> [parameterization]}

        factoryStub.use {
            listModel.load(CoreModel, 'CoreParameters')
            listModel.setSelectedItem("v1")
            assertEquals 'v1', listModel.getSelectedItem()
            assertEquals(parameterization, listModel.getSelectedObject())
        }
        LocaleResources.clearTestMode()
    }
}