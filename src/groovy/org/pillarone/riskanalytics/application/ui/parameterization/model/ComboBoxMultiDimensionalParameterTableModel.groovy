package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import org.pillarone.riskanalytics.core.parameterization.IComboBoxBasedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.ComponentUtils
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter


class ComboBoxMultiDimensionalParameterTableModel extends MultiDimensionalParameterTableModel {

    private BiMap<String, String> localizedValues = HashBiMap.create()

    ComboBoxMultiDimensionalParameterTableModel(AbstractMultiDimensionalParameter multiDimensionalParam, boolean indexed) {
        super(multiDimensionalParam, indexed)
        initValues()
    }

    @Override
    Object getValueAt(int row, int column) {
        Object value = super.getValueAt(row, column)
        column--

        if (isMarkerCell(multiDimensionalParam, row, column)) {
            String localizedValue = localizedValues.get(value)
            value = localizedValue != null ? localizedValue : value
        }
        return value
    }

    @Override
    void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (isMarkerCell(multiDimensionalParam, rowIndex, columnIndex)) {
            BiMap<String, String> localizedToValue = localizedValues.inverse()
            String enumValue = localizedToValue.get(value)
            value = enumValue != null ? enumValue : value
        }
        super.setValueAt(value, rowIndex, columnIndex)
    }

    @Override
    def getPossibleValues(int row, int col) {
        Object values = super.getPossibleValues(row, col)
        col--
        if (isMarkerCell(multiDimensionalParam, row, col)) {
            List newList = []
            for (def value in values) {
                String localizedValue = localizedValues.get(value)
                newList << localizedValue != null ? localizedValue : value
            }
            values = newList
        }
        return values
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

    private boolean isMarkerCell(AbstractMultiDimensionalParameter parameter, int row, int column) {
        return false
    }

    private boolean isMarkerCell(ComboBoxTableMultiDimensionalParameter parameter, int row, int column) {
        return row > 0 && column >= 0
    }

    private boolean isMarkerCell(ComboBoxMultiDimensionalParameterTableModel parameter, int row, int column) {
        return (row == 0 && column == 0) ? false : row == 0 || column == 0
    }


}
