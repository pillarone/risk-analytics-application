package org.pillarone.riskanalytics.application.ui

import com.ulcjava.base.application.ULCPollingTimer
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class PollingSupport {

    private ULCPollingTimer pollingTimer
    @Delegate
    private final ActionListenerSupport support = new ActionListenerSupport()

    int delay = 2000

    @PostConstruct
    void initialize() {
        pollingTimer = new ULCPollingTimer(delay, new IActionListener() {
            @Override
            void actionPerformed(ActionEvent event) {
                support.actionPerformed(event)
            }
        })
        pollingTimer.start()
    }

    @PreDestroy
    void destroy() {
        pollingTimer.stop()
        pollingTimer = null
    }
}
