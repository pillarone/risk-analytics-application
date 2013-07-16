package org.pillarone.riskanalytics.application.search

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.Field.Index
import org.apache.lucene.document.Field.Store
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.core.workflow.Status

abstract class DocumentFactory {

    public static final String ID_FIELD = "id"
    public static final String NAME_FIELD = "name"
    public static final String VERSION_FIELD = "version"
    public static final String STATE_FIELD = "state"
    public static final String DEALID_FIELD = "dealId"
    public static final String VALID_FIELD = "valid"
    public static final String OWNER_FIELD = "owner"
    public static final String LASTUPDATED_BY = "lastupdatedBy"
    public static final String CREATION_TIME = "created"
    public static final String LASTUPDATED_TIME = "updated"
    public static final String MODEL_CLASS_FIELD = "modelClass"
    public static final String ITEM_TYPE_FIELD = "itemType"
    public static final String TAGS_FIELD = "tags"

    public static String createDeleteQueryString(ModellingItem modellingItem) {
        StringBuilder builder = new StringBuilder()
        builder.append(NAME_FIELD).append(":\"").append(modellingItem.name).append("\" ").append(MODEL_CLASS_FIELD).append(":\"").append(modellingItem.modelClass.name).append("\" ")

        if (GroovyUtils.getProperties(modellingItem).containsKey("versionNumber")) {
            builder.append(VERSION_FIELD).append(":\"").append(modellingItem.versionNumber.toString()).append("\"")
        }

        return builder.toString()
    }

    public static Document createDocument(ModellingItem item) {
        return doCreateDocument(item)
    }

    private static Document doCreateDocument(ModellingItem item) {
        throw new IllegalArgumentException("Modelling item type ${item.class} not supported in search index.")
    }

    private static Document doCreateDocument(Resource item) {
        Document document = new Document()
        document.add(new Field(ID_FIELD, id(item), Store.YES, Index.NOT_ANALYZED))
        document.add(new Field(NAME_FIELD, item.name, Store.YES, Index.ANALYZED))
        document.add(new Field(VERSION_FIELD, item.versionNumber.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(MODEL_CLASS_FIELD, item.modelClass.name, Store.YES, Index.ANALYZED))
        document.add(new Field(ITEM_TYPE_FIELD, ItemType.RESOURCE.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(STATE_FIELD, item.status.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(TAGS_FIELD, item.tags*.name.join(" "), Store.YES, Index.ANALYZED))
        document.add(new Field(VALID_FIELD, item.valid.toString(), Store.YES, Index.ANALYZED))
        addFieldIfNotNull(document, OWNER_FIELD, item.creator?.username?.toString())
        addFieldIfNotNull(document, LASTUPDATED_BY, item.lastUpdater?.username?.toString())
        addFieldIfNotNull(document, CREATION_TIME, item.creationDate?.millis?.toString())
        addFieldIfNotNull(document, LASTUPDATED_TIME, item.modificationDate?.millis?.toString())


        return document
    }

    private static Document doCreateDocument(Parameterization item) {
        Document document = new Document()
        document.add(new Field(ID_FIELD, id(item), Store.YES, Index.NOT_ANALYZED))
        document.add(new Field(NAME_FIELD, item.name, Store.YES, Index.ANALYZED))
        document.add(new Field(VERSION_FIELD, item.versionNumber.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(MODEL_CLASS_FIELD, item.modelClass.name, Store.YES, Index.ANALYZED))
        document.add(new Field(ITEM_TYPE_FIELD, ItemType.PARAMETERIZATION.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(TAGS_FIELD, item.tags*.name.join(" "), Store.YES, Index.ANALYZED))
        document.add(new Field(STATE_FIELD, item.status.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(VALID_FIELD, item.valid.toString(), Store.YES, Index.ANALYZED))
        addFieldIfNotNull(document, DEALID_FIELD, item.dealId?.toString())
        addFieldIfNotNull(document, OWNER_FIELD, item.creator?.username?.toString())
        addFieldIfNotNull(document, LASTUPDATED_BY, item.lastUpdater?.username?.toString())
        addFieldIfNotNull(document, CREATION_TIME, item.creationDate?.millis?.toString())
        addFieldIfNotNull(document, LASTUPDATED_TIME, item.modificationDate?.millis?.toString())


        return document
    }

    private static void addFieldIfNotNull(Document document, String fieldName, String fieldValue) {
        if (fieldValue) {
            document.add(new Field(fieldName, fieldValue, Store.YES, Index.ANALYZED))
        }

    }

    private static Document doCreateDocument(ResultConfiguration item) {
        Document document = new Document()
        document.add(new Field(ID_FIELD, id(item), Store.YES, Index.NOT_ANALYZED))
        document.add(new Field(NAME_FIELD, item.name, Store.YES, Index.ANALYZED))
        document.add(new Field(VERSION_FIELD, item.versionNumber.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(MODEL_CLASS_FIELD, item.modelClass.name, Store.YES, Index.ANALYZED))
        document.add(new Field(ITEM_TYPE_FIELD, ItemType.RESULT_CONFIGURATION.toString(), Store.YES, Index.ANALYZED))
        addFieldIfNotNull(document, OWNER_FIELD, item.creator?.username?.toString())
        addFieldIfNotNull(document, LASTUPDATED_BY, item.lastUpdater?.username?.toString())
        addFieldIfNotNull(document, CREATION_TIME, item.creationDate?.millis?.toString())
        addFieldIfNotNull(document, LASTUPDATED_TIME, item.modificationDate?.millis?.toString())

        return document
    }

    private static Document doCreateDocument(Simulation item) {
        Document document = new Document()
        document.add(new Field(ID_FIELD, id(item), Store.YES, Index.NOT_ANALYZED))
        document.add(new Field(NAME_FIELD, item.name, Store.YES, Index.ANALYZED))
        document.add(new Field(MODEL_CLASS_FIELD, item.modelClass.name, Store.YES, Index.ANALYZED))
        document.add(new Field(ITEM_TYPE_FIELD, ItemType.RESULT.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(TAGS_FIELD, item.tags*.name.join(" "), Store.YES, Index.ANALYZED))
        addFieldIfNotNull(document, OWNER_FIELD, item.creator?.username?.toString())
        addFieldIfNotNull(document, LASTUPDATED_BY, item.lastUpdater?.username?.toString())
        addFieldIfNotNull(document, CREATION_TIME, item.creationDate?.millis?.toString())
        addFieldIfNotNull(document, LASTUPDATED_TIME, item.modificationDate?.millis?.toString())

        return document
    }

    public static ModellingItem toModellingItem(Document document) {
        ItemType itemType = ItemType.valueOf(document.getField(ITEM_TYPE_FIELD).stringValue())
        String name = document.getField(NAME_FIELD).stringValue()
        Class modelClass = DocumentFactory.getClassLoader().loadClass(document.getField(MODEL_CLASS_FIELD).stringValue())
        switch (itemType) {
            case ItemType.PARAMETERIZATION:
                VersionNumber versionNumber = new VersionNumber(document.getField(VERSION_FIELD).stringValue())
                Parameterization parameterization = new Parameterization(name, modelClass)
                parameterization.versionNumber = versionNumber
                parameterization.valid = document.getField(VALID_FIELD).stringValue().toBoolean()
                parameterization.status = Status.valueOf(document.getField(STATE_FIELD).stringValue())
                mapOwner(parameterization, document)
                mapModificator(parameterization, document)
                mapTags(parameterization, document)
                return parameterization
            case ItemType.RESULT_CONFIGURATION:
                VersionNumber versionNumber = new VersionNumber(document.getField(VERSION_FIELD).stringValue())
                ResultConfiguration resultConfiguration = new ResultConfiguration(name)
                resultConfiguration.modelClass = modelClass
                resultConfiguration.versionNumber = versionNumber
                mapOwner(resultConfiguration, document)
                mapModificator(resultConfiguration, document)
                return resultConfiguration
            case ItemType.RESULT:
                Simulation simulation = new Simulation(name)
                simulation.modelClass = modelClass
                mapOwner(simulation, document)
                mapModificator(simulation, document)
                mapTags(simulation, document)
                return simulation
            case ItemType.RESOURCE:
                Resource resource = new Resource(name, modelClass)
                resource.versionNumber = new VersionNumber(document.getField(VERSION_FIELD).stringValue())
                resource.valid = document.getField(VALID_FIELD).stringValue().toBoolean()
                resource.status = Status.valueOf(document.getField(STATE_FIELD).stringValue())
                mapOwner(resource, document)
                mapModificator(resource, document)
                mapTags(resource, document)
                return resource

        }
    }

    private static void mapTags(ModellingItem item, Document document) {
        Field field = document.getField(TAGS_FIELD)
        if (field) {
            List<String> tags = field.stringValue().split(' ').toList()
            tags.each {
                item.tags << new Tag(name: it)
            }
        }
    }

    private static void mapOwner(ModellingItem item, Document document) {
        item.creationDate = new DateTime(document.getField(CREATION_TIME).stringValue().toLong())
        Field field = document.getField(OWNER_FIELD)
        if (field) {
            item.creator = new Person(username: field.stringValue())
        }
    }

    private static void mapModificator(ModellingItem item, Document document) {
        item.modificationDate = new DateTime(document.getField(LASTUPDATED_TIME).stringValue().toLong())
        Field field = document.getField(LASTUPDATED_BY)
        if (field) {
            item.lastUpdater = new Person(username: field.stringValue())
        }
    }

    static String id(ModellingItem item) {
        "${item.class.name}_${item.id}"
    }

    public static enum ItemType {
        PARAMETERIZATION, RESULT_CONFIGURATION, RESULT, RESOURCE
    }
}
