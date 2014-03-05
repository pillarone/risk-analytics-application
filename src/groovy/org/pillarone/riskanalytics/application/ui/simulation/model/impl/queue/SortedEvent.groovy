package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue


class SortedEvent<T> {

    SortedEvent(int[] oldIndices, int[] newIndices, T[] elements) {
        this.oldIndices = oldIndices
        this.newIndices = newIndices
        this.elements = elements
    }

    int[] oldIndices
    int[] newIndices
    T[] elements
}
