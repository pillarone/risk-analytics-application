package org.pillarone.riskanalytics.application.ui.main.view

import com.google.common.eventbus.DeadEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import groovy.beans.Bindable
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.model.IRiskAnalyticsModelListener
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.search.ModellingItemEvent
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Resource
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class RiskAnalyticsMainModel {

    private static final Log LOG = LogFactory.getLog(RiskAnalyticsMainModel)

    @Delegate
    private final EventBus eventBus = new EventBus()

    @PostConstruct
    void initialize() {
        eventBus.register(this)
    }

    @PreDestroy
    void unregister() {
        eventBus.unregister(this)
    }

    @Subscribe
    void handleDeadEvents(DeadEvent deadEvent) {
        LOG.warn("no subscriber for event ${deadEvent.event} posted by ${deadEvent.source}")
    }

    //TODO move all detailView stuff to DetailViewManager (alls stuff below this line...)
    @Subscribe
    void closeDetailViewIfItemRemoved(ModellingItemEvent event) {
        if (event.eventType == CacheItemEvent.EventType.REMOVED) {
            closeDetailView(event.modellingItem)
        }
    }

    private void closeDetailView(ModellingItem modellingItem) {
        Model model = createModel(modellingItem)
        notifyCloseDetailView(model, UIItemFactory.createItem(modellingItem, model))
    }

    private Model createModel(ModellingItem modellingItem) {
        if (modellingItem instanceof Resource) {
            return null
        }
        modellingItem.modelClass ? (modellingItem.modelClass.newInstance() as Model) : null
    }

    private final List<IRiskAnalyticsModelListener> modelListeners = []

    @Bindable
    AbstractUIItem currentItem


    void openItem(Model model, AbstractUIItem item) {
        if (!item.loaded) {
            item.load()
        }
        notifyOpenDetailView(model, item)
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
}
