package org.pillarone.riskanalytics.application.example.constraint

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class LinePercentage implements IMultiDimensionalConstraints {

    public static final String IDENTIFIER = "LINE_PERCENTAGE"

    boolean matches(int row, int column, Object value) {
        if (column == 0) {
            return value instanceof String
        }
        else {
            return value instanceof Double || value instanceof BigDecimal
        }
    }

    String getName() {
        return IDENTIFIER
    }

    Class getColumnType(int column) {
        return column == 0 ? ITestComponentMarker : BigDecimal
    }


}
