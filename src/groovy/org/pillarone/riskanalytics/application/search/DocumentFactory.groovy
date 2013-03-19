package org.pillarone.riskanalytics.application.search

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.Field.Index
import org.apache.lucene.document.Field.Store
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.util.GroovyUtils

abstract class DocumentFactory {

    public static final String NAME_FIELD = "name"
    public static final String VERSION_FIELD = "version"
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

    private static Document doCreateDocument(Parameterization parameterization) {
        Document document = new Document()
        document.add(new Field(NAME_FIELD, parameterization.name, Store.YES, Index.ANALYZED))
        document.add(new Field(VERSION_FIELD, parameterization.versionNumber.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(MODEL_CLASS_FIELD, parameterization.modelClass.name, Store.YES, Index.ANALYZED))
        document.add(new Field(ITEM_TYPE_FIELD, ItemType.PARAMETERIZATION.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(TAGS_FIELD, parameterization.tags*.name.join(" "), Store.NO, Index.ANALYZED))

        return document
    }

    private static Document doCreateDocument(ResultConfiguration resultConfiguration) {
        Document document = new Document()
        document.add(new Field(NAME_FIELD, resultConfiguration.name, Store.YES, Index.ANALYZED))
        document.add(new Field(VERSION_FIELD, resultConfiguration.versionNumber.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(MODEL_CLASS_FIELD, resultConfiguration.modelClass.name, Store.YES, Index.ANALYZED))
        document.add(new Field(ITEM_TYPE_FIELD, ItemType.RESULT_CONFIGURATION.toString(), Store.YES, Index.ANALYZED))

        return document
    }

    private static Document doCreateDocument(Simulation simulation) {
        Document document = new Document()
        document.add(new Field(NAME_FIELD, simulation.name, Store.YES, Index.ANALYZED))
        document.add(new Field(MODEL_CLASS_FIELD, simulation.modelClass.name, Store.YES, Index.ANALYZED))
        document.add(new Field(ITEM_TYPE_FIELD, ItemType.RESULT.toString(), Store.YES, Index.ANALYZED))
        document.add(new Field(TAGS_FIELD, simulation.tags*.name.join(" "), Store.NO, Index.ANALYZED))

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
                return parameterization
            case ItemType.RESULT_CONFIGURATION:
                VersionNumber versionNumber = new VersionNumber(document.getField(VERSION_FIELD).stringValue())
                ResultConfiguration resultConfiguration = new ResultConfiguration(name)
                resultConfiguration.modelClass = modelClass
                resultConfiguration.versionNumber = versionNumber
                return resultConfiguration
            case ItemType.RESULT:
                Simulation simulation = new Simulation(name)
                simulation.modelClass = modelClass
                return simulation

        }
    }

    public static enum ItemType {
        PARAMETERIZATION, RESULT_CONFIGURATION, RESULT
    }
}
