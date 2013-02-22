package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import com.google.common.collect.BiMap
import org.pillarone.riskanalytics.core.parameterization.IComboBoxBasedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.ComponentUtils
import com.google.common.collect.HashBiMap


class PeriodMultiDimensionalParameterTableModel extends MultiDimensionalParameterTableModel {
    private BiMap<String, String> localizedValues = HashBiMap.create()

    PeriodMultiDimensionalParameterTableModel(AbstractMultiDimensionalParameter multiDimensionalParam, boolean indexed) {
        super(multiDimensionalParam, indexed)
        initValues()
    }


    def getPossibleValues(int row, int col) {
        Object value = super.getPossibleValues(row, col)
        if (multiDimensionalParam.isMarkerCell(row, col - 1)) {
            String localizedValue = localizedValues.get(value)
            value = localizedValue != null ? localizedValue : value
        }
        return value
    }

    Object getValueAt(int row, int column) {
        Object value = super.getValueAt(row, column)

        if (multiDimensionalParam.isMarkerCell(row, column - 1)) {
            String localizedValue = localizedValues.get(value)
            value = localizedValue != null ? localizedValue : value
        }
        return value
    }

    @Override
    void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (multiDimensionalParam.isMarkerCell(rowIndex, columnIndex - 1)) {
            BiMap<String, String> localizedToValue = localizedValues.inverse()
            String enumValue = localizedToValue.get(value)
            value = enumValue != null ? enumValue : value
        }
        super.setValueAt(value, rowIndex, columnIndex)
    }



    private void initValues() {
        if (multiDimensionalParam instanceof IComboBoxBasedMultiDimensionalParameter) {
            Class markerClass = multiDimensionalParam.markerClass
            List components = multiDimensionalParam.simulationModel.getMarkedComponents(markerClass)
            for (Component c in components) {
                localizedValues.put(c.name, ComponentUtils.getNormalizedName(c.name))
            }
        }
    }
}
