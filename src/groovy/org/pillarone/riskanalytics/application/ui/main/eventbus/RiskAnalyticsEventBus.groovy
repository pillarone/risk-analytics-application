package org.pillarone.riskanalytics.application.ui.main.eventbus

import com.google.common.eventbus.DeadEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class RiskAnalyticsEventBus {

    private static final Log LOG = LogFactory.getLog(RiskAnalyticsEventBus)

    @Delegate
    private final EventBus eventBus = new EventBus()

    @PostConstruct
    private void postConstruct() {
        eventBus.register(this)
    }

    @PreDestroy
    private void preDestroy() {
        eventBus.unregister(this)
    }

    @Subscribe
    void handleDeadEvents(DeadEvent deadEvent) {
        LOG.warn("no subscriber for event ${deadEvent.event} posted by ${deadEvent.source}")
    }
}
