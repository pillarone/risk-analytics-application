package org.pillarone.riskanalytics.application.ui

import com.ulcjava.base.application.ULCPollingTimer
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import java.util.concurrent.CopyOnWriteArraySet

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class PollingSupport {
    private final Set<IActionListener> listeners = new CopyOnWriteArraySet<IActionListener>()

    private ULCPollingTimer pollingTimer

    private int delay = 2000

    @Required
    void setDelay(int delay) {
        this.delay = delay
    }

    @PostConstruct
    void initialize() {
        pollingTimer = new ULCPollingTimer(delay, new IActionListener() {
            @Override
            void actionPerformed(ActionEvent event) {
                fireActionEvent(event)
            }
        })
    }

    @PreDestroy
    private void destroy() {
        pollingTimer.stop()
        pollingTimer = null
    }

    void addActionListener(IActionListener listener) {
        listeners.add(listener)
        pollingTimer.start()
    }

    void removeActionListener(IActionListener listener) {
        listeners.remove(listener)
        if (listeners.empty) {
            pollingTimer.stop()
        }
    }

    private void fireActionEvent(ActionEvent event) {
        listeners.each {
            it.actionPerformed(event)
        }
    }
}
