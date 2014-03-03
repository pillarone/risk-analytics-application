package org.pillarone.riskanalytics.application.dataaccess.item

import models.application.ApplicationModel
import models.core.CoreModel
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.core.ModelDAO
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ModelFileImportService
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.output.CollectorInformation
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.simulation.item.*
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment

import static junit.framework.Assert.*

class ModellingItemFactoryTests {

    @Before
    void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['Core'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['Core'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['Core'])
        new ModelFileImportService().compareFilesAndWriteToDB(['Core'])
        ModellingItemFactory.clear()
    }

    @Test
    void testCreateItem() {
        ConfigObject data = new ConfigObject()
        data.model = CoreModel

        ModellingItem item = ModellingItemFactory.createItem('CoreNotYetExistingParameters', data, Parameterization, false)
        item.load()
        assertTrue item instanceof Parameterization
        assertEquals '1', item.versionNumber.toString()

        item = ModellingItemFactory.createItem('CoreParameters', data, Parameterization, false)
        assertEquals '2', item.versionNumber.toString()
    }

    @Test
    void testGetParameterization() {
        ParameterizationDAO dao = ParameterizationDAO.list()[0]
        Parameterization parameterization = ModellingItemFactory.getParameterization(dao)
        assertNotNull parameterization
        assertEquals dao.name, parameterization.name
        assertSame parameterization, ModellingItemFactory.getParameterization(dao)

        parameterization.load()
        dao = ModellingItemFactory.incrementVersion(parameterization).dao
        assertNotSame parameterization, ModellingItemFactory.getParameterization(dao)
    }

    @Test
    void testGetNewestParameterization() {
        ParameterizationDAO dao = ParameterizationDAO.findByName('CoreParameters')
        Parameterization parameterization = ModellingItemFactory.getParameterization(dao)
        assertNotNull parameterization
        parameterization.load()

        int currentCount = ModellingItemFactory.getParameterizationsForModel(CoreModel).size()

        Parameterization newVersion = ModellingItemFactory.incrementVersion(parameterization)
        newVersion.load()

        assertEquals currentCount + 1, ModellingItemFactory.getParameterizationsForModel(CoreModel).size()
        List newParams = ModellingItemFactory.getNewestParameterizationsForModel(CoreModel)
        assertEquals currentCount, newParams.size()
        newParams.each { it.load() }
        assertTrue newParams.contains(newVersion)
        assertFalse newParams.contains(parameterization)
    }

    @Test
    void testGetModelItem() {
        ModelDAO dao = ModelDAO.list()[0]
        ModelItem modelItem = ModellingItemFactory.getModelItem(dao)
        assertNotNull modelItem
        assertEquals dao.name, modelItem.name
        assertSame modelItem, ModellingItemFactory.getModelItem(dao)

        modelItem.load()
        dao = ModellingItemFactory.incrementVersion(modelItem).dao
        assertNotSame modelItem, ModellingItemFactory.getModelItem(dao)
    }

    @Test
    void testGetModelStructure() {

        ModelStructureDAO dao = ModelStructureDAO.list()[0]
        ModelStructure template = ModellingItemFactory.getModelStructure(dao)
        assertNotNull template
        assertEquals dao.name, template.name
        assertSame template, ModellingItemFactory.getModelStructure(dao)

        template.load()
        dao = ModellingItemFactory.incrementVersion(template).dao
        assertNotSame template, ModellingItemFactory.getModelStructure(dao)
    }

    @Test
    void testCopyParameterization() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['Application'])
        ParameterizationDAO dao = ParameterizationDAO.findByModelClassName(ApplicationModel.getName())
        Parameterization parameterization = ModellingItemFactory.getParameterization(dao)
        parameterization.load()
        parameterization.periodLabels = ['p1', 'p2']
        Parameterization copy = ModellingItemFactory.copyItem(parameterization, "$parameterization.name-copy")

        assertNotSame parameterization, copy
        assertEquals copy.name, "$parameterization.name-copy"
        assertEquals parameterization.modelClass, copy.modelClass
        assertEquals '1', copy.versionNumber.toString()

        assertEquals 2, copy.periodLabels.size()
        assertTrue copy.periodLabels.contains('p1')
        assertTrue copy.periodLabels.contains('p2')

        assertEquals parameterization.periodCount, copy.periodCount
        assertEquals parameterization.parameters.size(), copy.parameters.size()
    }

    @Test
    void testCopyResultConfiguration() {

        int collectorCount = CollectorInformation.count()

        ResultConfigurationDAO dao = ResultConfigurationDAO.list()[0]
        ResultConfiguration resultConfiguration = ModellingItemFactory.getResultConfiguration(dao)
        resultConfiguration.load()
        ResultConfiguration copy = ModellingItemFactory.copyItem(resultConfiguration, "$resultConfiguration.name-copy")

        assertNotSame resultConfiguration, copy
        assertEquals copy.name, "$resultConfiguration.name-copy"
        assertEquals resultConfiguration.modelClass, copy.modelClass
        assertEquals '1', copy.versionNumber.toString()
        assertEquals resultConfiguration.comment, copy.comment

        assertEquals collectorCount + resultConfiguration.collectors.size(), CollectorInformation.count()
        assertEquals resultConfiguration.collectors.size(), copy.collectors.size()
    }

    @Test
    void testIncrementResultConfigurationVersion() {

        int collectorCount = CollectorInformation.count()

        ResultConfigurationDAO dao = ResultConfigurationDAO.list()[0]
        ResultConfiguration resultConfiguration = ModellingItemFactory.getResultConfiguration(dao)
        resultConfiguration.load()
        ResultConfiguration newVersion = ModellingItemFactory.incrementVersion(resultConfiguration)

        assertNotSame resultConfiguration, newVersion
        assertEquals resultConfiguration.name, newVersion.name
        assertEquals resultConfiguration.modelClass, newVersion.modelClass
        assertEquals '2', newVersion.versionNumber.toString()
        assertEquals resultConfiguration.comment, newVersion.comment

        assertEquals collectorCount + resultConfiguration.collectors.size(), CollectorInformation.count()
        assertEquals resultConfiguration.collectors.size(), newVersion.collectors.size()
    }

    @Test
    void testIncrementParameterizationVersion() {
//        ParameterizationDAO dao = ParameterizationDAO.findByItemVersion('1')
        new ParameterizationImportService().compareFilesAndWriteToDB(['Application'])
        ParameterizationDAO dao = ParameterizationDAO.findByModelClassName(ApplicationModel.getName())
        Parameterization parameterization = ModellingItemFactory.getParameterization(dao)
        parameterization.load()
        parameterization.periodLabels = ['p1', 'p2']
        parameterization.addComment(new Comment("path", 0))
        parameterization.dealId = 3
        Parameterization copy = ModellingItemFactory.incrementVersion(parameterization)

        assertNotSame parameterization, copy
        assertEquals copy.name, "$parameterization.name"
        assertEquals parameterization.modelClass, copy.modelClass
        assertEquals '2', copy.versionNumber.toString()

        assertEquals 2, copy.periodLabels.size()
        assertTrue copy.periodLabels.contains('p1')
        assertTrue copy.periodLabels.contains('p2')
        assertEquals 3, copy.dealId

        assertEquals parameterization.periodCount, copy.periodCount
        assertEquals parameterization.parameters.size(), copy.parameters.size()
        assertEquals 1, copy.comments.size()
        assertEquals "path", copy.comments.get(0).path
        assertEquals 0, copy.comments.get(0).period
    }

    @Test
    void testPMO1985() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['Application'])
        ParameterizationDAO dao = ParameterizationDAO.findByModelClassName(ApplicationModel.getName())
        Parameterization parameterization = ModellingItemFactory.getParameterization(dao)
        parameterization.load()
        parameterization.periodLabels = ['p1', 'p2']
        parameterization.addComment(new Comment("path", 0))
        parameterization.dealId = 3
        parameterization.addParameter(ParameterHolderFactory.getHolder("newPath", 0, "value"))


        Parameterization copy = ModellingItemFactory.incrementVersion(parameterization)

        assertNotNull(copy.parameterHolders.find { it.path == "newPath" })
        assertNull(parameterization.parameterHolders.find { it.path == "newPath" })
    }

}