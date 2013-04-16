package org.pillarone.riskanalytics.application.example.constraint

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints

@CompileStatic
class EnumConstraint implements IMultiDimensionalConstraints {

    final String name = 'enumConstraint'

    boolean matches(int row, int column, Object value) {
        return true
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
