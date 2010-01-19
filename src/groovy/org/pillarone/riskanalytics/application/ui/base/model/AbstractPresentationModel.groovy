package org.pillarone.riskanalytics.application.ui.base.model


class AbstractPresentationModel {
    // TODO (Jul 24, 2009, msh): use a Set in order to avoid duplicates
    List listeners = []

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