package org.pillarone.riskanalytics.application.ui.main.view

import groovy.beans.Bindable
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.view.IModelItemChangeListener
import org.pillarone.riskanalytics.application.ui.main.model.IRiskAnalyticsModelListener
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.search.IModellingItemEventListener
import org.pillarone.riskanalytics.application.ui.search.ModellingItemCache
import org.pillarone.riskanalytics.application.ui.search.ModellingItemEvent
import org.pillarone.riskanalytics.application.ui.simulation.model.INewSimulationListener
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource
import java.beans.PropertyChangeListener

import static org.pillarone.riskanalytics.core.search.CacheItemEvent.EventType

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class RiskAnalyticsMainModel {

    private final List<IRiskAnalyticsModelListener> modelListeners = []
    private final List<IModelItemChangeListener> modelItemListeners = []
    private final List<INewSimulationListener> newSimulationListeners = []
    private final List<IModellingItemEventListener> modellingItemEventListeners = []

    @Bindable
    AbstractUIItem currentItem

    @Resource
    ModellingItemCache modellingItemCache
    private final MyModelItemEventListener listener = new MyModelItemEventListener()

    RiskAnalyticsMainModel() {
        addPropertyChangeListener('currentItem', { def event ->
            notifyChangedWindowTitle(currentItem)
        } as PropertyChangeListener)
    }

    @PostConstruct
    void initialize() {
        modellingItemCache.addItemEventListener(listener)
    }

    @PreDestroy
    void unregister() {
        modellingItemCache.removeItemEventListener(listener)
    }

    void openItem(Model model, AbstractUIItem item) {
        if (!item.loaded) {
            item.load()
        }
        notifyOpenDetailView(model, item)
    }

    void addModelItemChangedListener(IModelItemChangeListener listener) {
        modelItemListeners << listener
    }

    void removeModelItemChangedListener(IModelItemChangeListener listener) {
        modelItemListeners.remove(listener)
    }

    void fireModelItemChanged() {
        modelItemListeners.each { IModelItemChangeListener listener -> listener.modelItemChanged() }
    }

    void addModellingItemEventListener(IModellingItemEventListener listener) {
        modellingItemEventListeners << listener
    }

    void removeModellingItemEventListener(IModellingItemEventListener listener) {
        modellingItemEventListeners.remove(listener)
    }

    void fireModelItemEvent(ModellingItemEvent modellingItemEvent) {
        modellingItemEventListeners.each { it.onEvent(modellingItemEvent) }
    }

    void addModelListener(IRiskAnalyticsModelListener listener) {
        if (!modelListeners.contains(listener)) {
            modelListeners << listener
        }
    }

    void notifyOpenDetailView(Model model, AbstractUIItem item) {
        modelListeners.each { IRiskAnalyticsModelListener listener ->
            listener.openDetailView(model, item)
        }
    }

    void notifyOpenDetailView(Model model, ModellingItem item) {
        modelListeners.each { IRiskAnalyticsModelListener listener ->
            listener.openDetailView(model, item)
        }
    }

    void notifyCloseDetailView(Model model, AbstractUIItem item) {
        modelListeners.each { IRiskAnalyticsModelListener listener ->
            listener.closeDetailView(model, item)
        }
    }

    void notifyChangedDetailView(Model model, AbstractUIItem item) {
        setCurrentItem(item)
        modelListeners.each { IRiskAnalyticsModelListener listener ->
            listener.changedDetailView(model, item)
        }
    }

    void notifyChangedWindowTitle(AbstractUIItem abstractUIItem) {
        modelListeners.each { IRiskAnalyticsModelListener listener ->
            listener.windowTitle = abstractUIItem
        }
    }

    void addNewSimulationListener(INewSimulationListener newSimulationListener) {
        newSimulationListeners << newSimulationListener
    }

    void removeNewSimulationListener(INewSimulationListener newSimulationListener) {
        newSimulationListeners.remove(newSimulationListener)
    }

    void fireNewSimulation(Simulation simulation) {
        newSimulationListeners.each { INewSimulationListener newSimulationListener ->
            newSimulationListener.newSimulation(simulation)
        }
    }

    private class MyModelItemEventListener implements IModellingItemEventListener {
        @Override
        void onEvent(ModellingItemEvent event) {
            fireModelItemEvent(event)
            if (event.eventType == EventType.REMOVED) {
                closeDetailView(event.modellingItem)
            }
        }

        private void closeDetailView(ModellingItem modellingItem) {
            Model model = createModel(modellingItem)
            notifyCloseDetailView(model, UIItemFactory.createItem(modellingItem, model))
        }

        private Model createModel(ModellingItem modellingItem) {
            if (modellingItem instanceof org.pillarone.riskanalytics.core.simulation.item.Resource) {
                return null
            }
            modellingItem.modelClass ? (modellingItem.modelClass.newInstance() as Model) : null
        }
    }
}
