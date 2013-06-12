package org.pillarone.riskanalytics.application.ui.base.action

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints


interface CopyPasteColumnMapping {

    Class getColumnType(int col)

}

class ConstraintColumnMapping implements CopyPasteColumnMapping {
    IMultiDimensionalConstraints constraints
    int startColumn

    Class getColumnType(int col) {
        return constraints.getColumnType(startColumn + col)
    }
}

class DefaultColumnMapping implements CopyPasteColumnMapping {

    Class getColumnType(int col) {
        return Object
    }
}
class ColumnMapping implements CopyPasteColumnMapping {
    private Map<Integer, Class> mappings
    private int offset

    ColumnMapping(Map<Integer,Class> mappings, int offset=0) {
        this.mappings = mappings
        this.offset = offset
    }

    @Override
    Class getColumnType(int col) {
        mappings[col]
    }
}
