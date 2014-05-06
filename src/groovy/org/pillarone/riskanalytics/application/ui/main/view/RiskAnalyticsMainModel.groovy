package org.pillarone.riskanalytics.application.ui.main.view
import com.google.common.eventbus.DeadEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.model.IRiskAnalyticsModelListener
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
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

    //TODO move all detailView stuff to DetailViewManager or MainView (all stuff below this line...)
    private final List<IRiskAnalyticsModelListener> modelListeners = []

    AbstractUIItem currentItem

    void addModelListener(IRiskAnalyticsModelListener listener) {
        if (!modelListeners.contains(listener)) {
            modelListeners << listener
        }
    }

    void notifyOpenDetailView(AbstractUIItem item) {
        modelListeners.each { IRiskAnalyticsModelListener listener ->
            listener.openDetailView(item)
        }
    }

    void notifyCloseDetailView(AbstractUIItem item) {
        modelListeners.each { IRiskAnalyticsModelListener listener ->
            listener.closeDetailView(item)
        }
    }

    void notifyChangedDetailView(AbstractUIItem item) {
        modelListeners.each { IRiskAnalyticsModelListener listener ->
            listener.changedDetailView(item)
        }
    }
}
