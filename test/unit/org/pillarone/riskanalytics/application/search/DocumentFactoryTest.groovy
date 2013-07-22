package org.pillarone.riskanalytics.application.search

import org.apache.lucene.document.Document
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.workflow.Status

class DocumentFactoryTest extends GroovyTestCase {

    void testDocumentCreation_Parameterization() {
        Parameterization parameterization = createParameterization()
        Document document = DocumentFactory.createDocument(parameterization)
        assertEquals(12, document.fields.size())
        assertCommonFields(document, parameterization, DocumentFactory.ItemType.PARAMETERIZATION)
        assertNotNull(document.getField(DocumentFactory.VERSION_FIELD))
        assertEquals(parameterization.versionNumber.toString(), document.getField(DocumentFactory.VERSION_FIELD).stringValue())
        assertNotNull(document.getField(DocumentFactory.VALID_FIELD))
        assertEquals(parameterization.valid.toString(), document.getField(DocumentFactory.VALID_FIELD).stringValue())
        assertNotNull(document.getField(DocumentFactory.TAGS_FIELD))
        assertEquals(parameterization.tags.join(' '), document.getField(DocumentFactory.TAGS_FIELD).stringValue())
        assertNotNull(document.getField(DocumentFactory.STATE_FIELD))
        assertEquals(parameterization.status.toString(), document.getField(DocumentFactory.STATE_FIELD).stringValue())
        assertNotNull(document.getField(DocumentFactory.OWNER_FIELD))
        assertEquals(parameterization.creator.username, document.getField(DocumentFactory.OWNER_FIELD).stringValue())
        assertNotNull(document.getField(DocumentFactory.LASTUPDATED_BY))
        assertEquals(parameterization.lastUpdater.username, document.getField(DocumentFactory.LASTUPDATED_BY).stringValue())
        assertNotNull(document.getField(DocumentFactory.CREATION_TIME))
        assertEquals(parameterization.creationDate.millis.toString(), document.getField(DocumentFactory.CREATION_TIME).stringValue())
    }

    void testBothWays_Parameterization() {
        Parameterization parameterization = createParameterization()
        Document document = DocumentFactory.createDocument(parameterization)
        Parameterization fromDocumentFactory = DocumentFactory.toModellingItem(document)
        assertEquals(parameterization.name, fromDocumentFactory.name)
        assertEquals(parameterization.modelClass, fromDocumentFactory.modelClass)
        assertEquals(parameterization.versionNumber, fromDocumentFactory.versionNumber)
        assertEquals(parameterization.tags, fromDocumentFactory.tags)
        assertEquals(parameterization.creator.username, fromDocumentFactory.creator.username)
        assertEquals(parameterization.creationDate, fromDocumentFactory.creationDate)
        assertEquals(parameterization.lastUpdater.username, fromDocumentFactory.lastUpdater.username)
        assertEquals(parameterization.modificationDate, fromDocumentFactory.modificationDate)
        assertEquals(parameterization.valid, fromDocumentFactory.valid)
        assertEquals(parameterization.status, fromDocumentFactory.status)
    }
    void testBothWays_Resource() {
        Resource resource = createResource()
        Document document = DocumentFactory.createDocument(resource)
        Resource fromDocumentFactory = DocumentFactory.toModellingItem(document)
        assertEquals(resource.name, fromDocumentFactory.name)
        assertEquals(resource.modelClass, fromDocumentFactory.modelClass)
        assertEquals(resource.versionNumber, fromDocumentFactory.versionNumber)
        assertEquals(resource.tags, fromDocumentFactory.tags)
        assertEquals(resource.creator.username, fromDocumentFactory.creator.username)
        assertEquals(resource.creationDate, fromDocumentFactory.creationDate)
        assertEquals(resource.lastUpdater.username, fromDocumentFactory.lastUpdater.username)
        assertEquals(resource.modificationDate, fromDocumentFactory.modificationDate)
        assertEquals(resource.valid, fromDocumentFactory.valid)
        assertEquals(resource.status, fromDocumentFactory.status)
    }

    void testBothWays_Simulation() {
        Simulation simulation = createSimulation()
        Document document = DocumentFactory.createDocument(simulation)
        Simulation fromDocumentFactory = DocumentFactory.toModellingItem(document)
        assertEquals(simulation.name, fromDocumentFactory.name)
        assertEquals(simulation.modelClass, fromDocumentFactory.modelClass)
        assertEquals(simulation.tags, fromDocumentFactory.tags)
        assertEquals(simulation.creator.username, fromDocumentFactory.creator.username)
        assertEquals(simulation.creationDate, fromDocumentFactory.creationDate)
        assertEquals(simulation.lastUpdater.username, fromDocumentFactory.lastUpdater.username)
        assertEquals(simulation.modificationDate, fromDocumentFactory.modificationDate)
    }

    void testBothWays_ResultConfiguration() {
        ResultConfiguration resultConfiguration = createResultConfiguration()
        Document document = DocumentFactory.createDocument(resultConfiguration)
        ResultConfiguration fromDocumentFactory = DocumentFactory.toModellingItem(document)
        assertEquals(resultConfiguration.name, fromDocumentFactory.name)
        assertEquals(resultConfiguration.modelClass, fromDocumentFactory.modelClass)
        assertEquals(resultConfiguration.creator.username, fromDocumentFactory.creator.username)
        assertEquals(resultConfiguration.creationDate, fromDocumentFactory.creationDate)
        assertEquals(resultConfiguration.lastUpdater.username, fromDocumentFactory.lastUpdater.username)
        assertEquals(resultConfiguration.modificationDate, fromDocumentFactory.modificationDate)
    }

    private ResultConfiguration createResultConfiguration() {
        ResultConfiguration resultConfiguration = new ResultConfiguration("resultConfiguration")
        resultConfiguration.modelClass = DocumentFactory
        resultConfiguration.creator = new Person(username: 'Owner')
        resultConfiguration.lastUpdater = new Person(username: 'Updater')
        resultConfiguration.creationDate = new DateTime(System.currentTimeMillis() - 1000)
        resultConfiguration.modificationDate = new DateTime(System.currentTimeMillis())
        return resultConfiguration
    }

    private Simulation createSimulation() {
        Simulation simulation = new Simulation("simulation")
        simulation.modelClass = DocumentFactory
        simulation.creator = new Person(username: 'Owner')
        simulation.lastUpdater = new Person(username: 'Updater')
        simulation.creationDate = new DateTime(System.currentTimeMillis() - 1000)
        simulation.modificationDate = new DateTime(System.currentTimeMillis())
        simulation.tags = [new Tag(name: 'A'), new Tag(name: 'B'), new Tag(name: 'C')]
        return simulation
    }

    private Parameterization createParameterization() {
        Parameterization parameterization = new Parameterization("test", DocumentFactory)
        parameterization.valid = true
        parameterization.creator = new Person(username: 'Owner')
        parameterization.lastUpdater = new Person(username: 'Updater')
        parameterization.creationDate = new DateTime(System.currentTimeMillis() - 1000)
        parameterization.modificationDate = new DateTime(System.currentTimeMillis())
        parameterization.versionNumber = new VersionNumber('13')
        parameterization.status = Status.DATA_ENTRY
        parameterization.tags = [new Tag(name: 'A'), new Tag(name: 'B'), new Tag(name: 'C')]
        parameterization
    }

    private Resource createResource() {
        Resource resource = new Resource("test", DocumentFactory)
        resource.valid = true
        resource.creator = new Person(username: 'Owner')
        resource.lastUpdater = new Person(username: 'Updater')
        resource.creationDate = new DateTime(System.currentTimeMillis() - 1000)
        resource.modificationDate = new DateTime(System.currentTimeMillis())
        resource.versionNumber = new VersionNumber('13')
        resource.status = Status.DATA_ENTRY
        resource.tags = [new Tag(name: 'A'), new Tag(name: 'B'), new Tag(name: 'C')]
        resource
    }

    void assertCommonFields(Document document, ModellingItem item, DocumentFactory.ItemType type) {
        assertNotNull(document.getField(DocumentFactory.NAME_FIELD))
        assertNotNull(document.getField(DocumentFactory.ITEM_TYPE_FIELD))
        assertNotNull(document.getField(DocumentFactory.MODEL_CLASS_FIELD))
        assertNotNull(document.getField(DocumentFactory.CREATION_TIME))
        assertNotNull(document.getField(DocumentFactory.LASTUPDATED_TIME))
        assertEquals(item.name, document.getField(DocumentFactory.NAME_FIELD).stringValue())
        assertEquals(item.modelClass.name, document.getField(DocumentFactory.MODEL_CLASS_FIELD).stringValue())
        assertEquals(item.creationDate, new DateTime(document.getField(DocumentFactory.CREATION_TIME).stringValue().toLong()))
        assertEquals(item.modificationDate, new DateTime(document.getField(DocumentFactory.LASTUPDATED_TIME).stringValue().toLong()))
        assertEquals(type.toString(), document.getField(DocumentFactory.ITEM_TYPE_FIELD).stringValue())


    }
}
