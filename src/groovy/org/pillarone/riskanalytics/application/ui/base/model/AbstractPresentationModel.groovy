package org.pillarone.riskanalytics.application.ui.base.model

class AbstractPresentationModel {

    Set listeners = new HashSet()

    void addModelChangedListener(IModelChangedListener listener) {
        listeners << listener
    }

    void removeModelChangedListener(IModelChangedListener listener) {
        listeners.remove(listener)
    }

    void fireModelChanged() {
        listeners.each {IModelChangedListener listener -> listener.modelChanged()}
    }
}

interface IModelChangedListener {
    void modelChanged()
}