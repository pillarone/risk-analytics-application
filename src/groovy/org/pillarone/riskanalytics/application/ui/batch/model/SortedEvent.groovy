package org.pillarone.riskanalytics.application.ui.batch.model


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
