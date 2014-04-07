package org.pillarone.riskanalytics.application.ui
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener

import java.util.concurrent.CopyOnWriteArraySet

class ActionListenerSupport {
    private final Set<IActionListener> listeners = new CopyOnWriteArraySet<IActionListener>()

    void addActionListener(IActionListener listener) {
        listeners.add(listener)
    }

    void removeActionListener(IActionListener listener) {
        listeners.remove(listener)
    }

    void actionPerformed(ActionEvent event) {
        listeners.each {
            it.actionPerformed(event)
        }
    }
}
