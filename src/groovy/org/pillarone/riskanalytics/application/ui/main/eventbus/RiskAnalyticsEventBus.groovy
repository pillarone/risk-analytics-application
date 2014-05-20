package org.pillarone.riskanalytics.application.ui.main.eventbus

import com.google.common.eventbus.*
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

    private Map<Object, Throwable> errors = [:]

    @Delegate
    private final EventBus eventBus = new EventBus(new ExceptionHandler())

    @PostConstruct
    private void postConstruct() {
        eventBus.register(this)
    }

    @PreDestroy
    private void preDestroy() {
        eventBus.unregister(this)
    }

    public void post(Object event) {
        eventBus.post(event)
        //propagate errors to poster to get same behaviour as before. see ExceptionHandler
        Throwable throwable = errors.remove(event)
        if (throwable) {
            throw throwable
        }
    }

    @Subscribe
    void handleDeadEvents(DeadEvent deadEvent) {
        LOG.warn("no subscriber for event ${deadEvent.event} posted by ${deadEvent.source}")
    }

    private class ExceptionHandler implements SubscriberExceptionHandler {
        SubscriberExceptionHandler loggingSubscriberExceptionHandler = new EventBus.LoggingSubscriberExceptionHandler('default')

        @Override
        void handleException(Throwable exception, SubscriberExceptionContext context) {
            loggingSubscriberExceptionHandler.handleException(exception, context)
            errors[context.event] = exception
        }
    }
}
