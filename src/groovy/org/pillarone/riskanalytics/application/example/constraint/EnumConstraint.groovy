package org.pillarone.riskanalytics.application.example.constraint

import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints

class EnumConstraint implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "enumConstraint"

    boolean matches(int row, int column, Object value) {
        return true
    }

    String getName() {
        IDENTIFIER
    }

    Class getColumnType(int column) {
        return ExampleEnum
    }

    Integer getColumnIndex(Class marker) {
        return null
    }

    boolean emptyComponentSelectionAllowed(int column) {
        return false
    }
}
