package org.pillarone.riskanalytics.application.ui.simulation.model

import groovy.mock.interceptor.StubFor
import models.core.CoreModel
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class ResultConfigurationVersionsListModelTests extends GroovyTestCase {

    void testLoad() {
        LocaleResources.setTestMode()
        ResultConfigurationVersionsListModel listModel = new ResultConfigurationVersionsListModel()
        assertEquals 0, listModel.size

        StubFor factoryStub = new StubFor(ModellingItemFactory)
        factoryStub.demand.getResultConfigurationsForModel {Class model -> [new ResultConfiguration('CoreResultConfiguration'), new ResultConfiguration('name')]}

        factoryStub.use {
            listModel.load(CoreModel, 'CoreResultConfiguration')
            assertEquals 1, listModel.size
        }
        LocaleResources.clearTestMode()
    }

    void testGetElementAt() {
        LocaleResources.setTestMode()
        ResultConfigurationVersionsListModel listModel = new ResultConfigurationVersionsListModel()
        assertEquals 0, listModel.size

        ResultConfiguration template = new ResultConfiguration('CoreResultConfiguration')
        template.versionNumber = new VersionNumber('2')
        ResultConfiguration template2 = new ResultConfiguration('CoreResultConfiguration')
        template2.versionNumber = new VersionNumber('1')

        StubFor factoryStub = new StubFor(ModellingItemFactory)
        factoryStub.demand.getResultConfigurationsForModel {Class model -> [template, template2]}

        factoryStub.use {
            listModel.load(CoreModel, 'CoreResultConfiguration')
            assertEquals 'v2', listModel.getElementAt(0)
            assertEquals 'v1', listModel.getElementAt(1)
        }
        LocaleResources.clearTestMode()
    }

    void testSelection() {
        LocaleResources.setTestMode()
        ResultConfigurationVersionsListModel listModel = new ResultConfigurationVersionsListModel()
        assertEquals 0, listModel.size

        ResultConfiguration template = new ResultConfiguration('CoreResultConfiguration')

        StubFor factoryStub = new StubFor(ModellingItemFactory)
        factoryStub.demand.getResultConfigurationsForModel {Class model -> [template]}

        factoryStub.use {
            listModel.load(CoreModel, 'CoreResultConfiguration')
            listModel.setSelectedItem("v1")
            assertEquals "v1", listModel.getSelectedItem()
            assertEquals template, listModel.getSelectedObject()
        }
        LocaleResources.clearTestMode()
    }
}
