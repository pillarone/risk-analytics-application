package org.pillarone.riskanalytics.application.example.constraint

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker


class CopyPasteConstraint implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = 'copypaste'

    Integer getColumnIndex(Class marker) {
        return marker == ITestComponentMarker ? 5 : null
    }

    boolean matches(int row, int column, Object value) {
        return true
    }

    String getName() {
        return IDENTIFIER
    }

    Class getColumnType(int column) {
        [Integer, Double, DateTime, Boolean, String, ITestComponentMarker][column]
    }

    boolean emptyComponentSelectionAllowed(int column) {
        return false
    }
}
