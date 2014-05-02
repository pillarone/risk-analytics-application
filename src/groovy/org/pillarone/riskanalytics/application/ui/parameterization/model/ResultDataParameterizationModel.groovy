package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.components.DataSourceDefinition
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry
import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy
import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class ResultDataParameterizationModel {

    DataSourceDefinition definition
    boolean readOnly = false

    private List<IModelChangedListener> listeners = []

    void addListener(IModelChangedListener listener) {
        listeners << listener
    }

    void removeListener(IModelChangedListener listener) {
        listeners.remove(listener)
    }

    protected void fireModelChanged() {
        listeners*.modelChanged()
    }

    Collection<String> getModelClasses() {
        return ModelRegistry.instance.allModelClasses*.name
    }


    void setModelClass(String className) {
        definition.parameterization.modelClass = Thread.currentThread().contextClassLoader.loadClass(className)
        fireModelChanged()
    }

    Collection<String> getParameterizationsForModel() {
        return ModellingItemFactory.getParameterizationsForModel(definition.parameterization.modelClass)*.nameAndVersion
    }

    void setParameterization(String nameAndVersion) {
        int index = nameAndVersion.lastIndexOf(" v")

        definition.parameterization.name = nameAndVersion.subSequence(0, index)
        definition.parameterization.versionNumber = new VersionNumber(nameAndVersion.substring(index + 2))

        fireModelChanged()
    }

    String getPath() {
        return definition.path
    }

    void setPath(String path) {
        definition.path = path
        fireModelChanged()
    }

    String getFields() {
        return definition.fields.join(" ")
    }

    void setFields(String fields) {
        definition.fields = fields.split(" ")
        fireModelChanged()
    }

    String getPeriods() {
        return definition.periods.join(" ")
    }

    void setPeriods(List<Integer> periods) {
        definition.periods = periods
        fireModelChanged()
    }

    List<String> getCollectorStrategies() {
        return [CollectingModeFactory.getStrategy(AggregatedCollectingModeStrategy.IDENTIFIER), CollectingModeFactory.getStrategy(SingleValueCollectingModeStrategy.IDENTIFIER)]*.getDisplayName(LocaleResources.locale)
    }

    void setCollector(String displayName) {
        definition.collectorName = CollectingModeFactory.getAvailableStrategies().find { it.getDisplayName(LocaleResources.locale) == displayName }.identifier
        fireModelChanged()
    }
}
