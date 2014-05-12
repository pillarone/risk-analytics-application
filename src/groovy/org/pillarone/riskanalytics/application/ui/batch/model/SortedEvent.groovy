package org.pillarone.riskanalytics.application.ui.batch.model

import groovy.transform.CompileStatic


@CompileStatic
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
