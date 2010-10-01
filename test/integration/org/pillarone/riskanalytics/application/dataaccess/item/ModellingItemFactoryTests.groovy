package org.pillarone.riskanalytics.application.dataaccess.item

import models.application.ApplicationModel
import models.core.CoreModel
import org.pillarone.riskanalytics.core.ModelDAO
import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.example.model.EmptyModel
import org.pillarone.riskanalytics.core.fileimport.ModelFileImportService
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.output.CollectorInformation
import org.pillarone.riskanalytics.core.output.ResultConfigurationDAO
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.simulation.item.*

class ModellingItemFactoryTests extends GroovyTestCase {

    void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreParameters'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['CoreResultConfiguration'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['CoreStructure'])
        new ModelFileImportService().compareFilesAndWriteToDB(['CoreModel'])
    }

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

    void testGetNewestParameterization() {
        ParameterizationDAO dao = ParameterizationDAO.findByName('CoreParameters')
        Parameterization parameterization = ModellingItemFactory.getParameterization(dao)
        assertNotNull parameterization
        parameterization.load()

        assertEquals 1, ModellingItemFactory.getParameterizationsForModel(CoreModel).size()

        Parameterization newVersion = ModellingItemFactory.incrementVersion(parameterization)
        newVersion.load()

        assertEquals 2, ModellingItemFactory.getParameterizationsForModel(CoreModel).size()
        List newParams = ModellingItemFactory.getNewestParameterizationsForModel(CoreModel)
        assertEquals 1, newParams.size()
        newParams.each {it.load()}
        assertTrue newParams.contains(newVersion)
        assertFalse newParams.contains(parameterization)
    }

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

    void testGetNewestModelItem() {
        ModelDAO dao = ModelDAO.findByName('CoreModel')
        ModelItem modelItem = ModellingItemFactory.getModelItem(dao)
        assertNotNull modelItem
        modelItem.load()

        ModelItem newVersion = ModellingItemFactory.incrementVersion(modelItem)
        newVersion.load()

        assertEquals newVersion, ModellingItemFactory.getNewestModelItem('CoreModel')
    }

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

    void testGetSimulation() {

        Simulation simulation = ModellingItemFactory.getSimulation("Foo", EmptyModel)
        assertNotNull simulation
        assertEquals "Foo", simulation.name
        assertSame simulation, ModellingItemFactory.getSimulation("Foo", EmptyModel)
        assertNotSame simulation, ModellingItemFactory.getSimulation("Bar", EmptyModel)
        assertNotSame simulation, ModellingItemFactory.getSimulation("Foo", CoreModel)
    }

    void testCopyParameterization() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['ApplicationParameters'])
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

    void testIncrementParameterizationVersion() {
//        ParameterizationDAO dao = ParameterizationDAO.findByItemVersion('1')
        new ParameterizationImportService().compareFilesAndWriteToDB(['ApplicationParameters'])
        ParameterizationDAO dao = ParameterizationDAO.findByModelClassName(ApplicationModel.getName())
        Parameterization parameterization = ModellingItemFactory.getParameterization(dao)
        parameterization.load()
        parameterization.periodLabels = ['p1', 'p2']
        parameterization.addComment(new Comment("path", 0))
        Parameterization copy = ModellingItemFactory.incrementVersion(parameterization)

        assertNotSame parameterization, copy
        assertEquals copy.name, "$parameterization.name"
        assertEquals parameterization.modelClass, copy.modelClass
        assertEquals '2', copy.versionNumber.toString()

        assertEquals 2, copy.periodLabels.size()
        assertTrue copy.periodLabels.contains('p1')
        assertTrue copy.periodLabels.contains('p2')

        assertEquals parameterization.periodCount, copy.periodCount
        assertEquals parameterization.parameters.size(), copy.parameters.size()
        assertEquals 1, copy.comments.size()
        assertEquals "path", copy.comments.get(0).path
        assertEquals 0, copy.comments.get(0).period
    }

    void testGetNewestModelStructure() {
        ModelStructureDAO dao = ModelStructureDAO.list()[0]
        ModelStructure modelStructure = ModellingItemFactory.getModelStructure(dao)
        assertNotNull modelStructure
        modelStructure.load()

        ModelStructure newVersion = ModellingItemFactory.incrementVersion(modelStructure)
        newVersion.load()

        List newParams = ModellingItemFactory.getNewestModulStructureForModel(modelStructure.modelClass)
        assertEquals 1, newParams.size()
        newParams.each {it.load()}
        assertTrue newParams.contains(newVersion)
        assertFalse newParams.contains(modelStructure)
    }
}