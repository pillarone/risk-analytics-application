package org.pillarone.riskanalytics.application.ui

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import groovy.transform.Synchronized

class ActionListenerSupport {
    private final Set<IActionListener> listeners = Collections.synchronizedSet([] as Set<IActionListener>)

    @Synchronized('listeners')
    void addActionListener(IActionListener listener) {
        listeners.add(listener)
    }

    @Synchronized('listeners')
    void removeActionListener(IActionListener listener) {
        listeners.remove(listener)
    }

    @Synchronized('listeners')
    void actionPerformed(ActionEvent event) {
        listeners.each { it.actionPerformed(event) }
    }
}
