package org.pillarone.riskanalytics.application.example.constraint

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class LinePercentage implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "LINE_PERCENTAGE"

    boolean matches(int row, int column, Object value) {
        if (column == 0) {
            return value instanceof String
        }
        else {
            return value instanceof Double || value instanceof Integer
        }
    }

    String getName() {
        return IDENTIFIER
    }

    Class getColumnType(int column) {
        return column == 0 ? ITestComponentMarker : Double
    }

    Integer getColumnIndex(Class marker) {
        null
    }

    boolean emptyComponentSelectionAllowed(int column) {
        return false
    }
}
