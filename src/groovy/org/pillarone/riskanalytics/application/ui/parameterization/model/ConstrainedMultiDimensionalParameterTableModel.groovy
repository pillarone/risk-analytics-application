package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.ComponentUtils

class ConstrainedMultiDimensionalParameterTableModel extends MultiDimensionalParameterTableModel {

    private Map<Integer, BiMap<String, String>> localizedValues = [:]

    ConstrainedMultiDimensionalParameterTableModel(ConstrainedMultiDimensionalParameter multiDimensionalParam, boolean indexed) {
        super(multiDimensionalParam, indexed)
        initEnumValues()
    }

    ConstrainedMultiDimensionalParameter getMultiDimensionalParameter() {
        return multiDimensionalParam as ConstrainedMultiDimensionalParameter
    }

    private void initEnumValues() {
        for (int i = 0; i < multiDimensionalParameter.valueColumnCount; i++) {
            final Class columnType = multiDimensionalParameter.getConstraints().getColumnType(i)
            if (columnType.isEnum()) {
                BiMap values = HashBiMap.create()
                for (def enumValue in columnType.values()) {
                    final String displayName = I18NUtils.findEnumDisplayName(columnType, enumValue.toString())
                    values.put(enumValue.toString(), displayName != null ? displayName : enumValue.toString())
                }
                localizedValues.put(i, values)
            } else if(IComponentMarker.isAssignableFrom(columnType)) {
                List components = multiDimensionalParameter.simulationModel.getMarkedComponents(columnType)
                BiMap<String,String> values = HashBiMap.create()
                for (Component c in components) {
                    values.put(c.name, ComponentUtils.getNormalizedName(c.name))
                }
                localizedValues.put(i, values)
            }
        }
    }

    @Override
    Object getValueAt(int row, int column) {
        Object value = super.getValueAt(row, column)
        column--

        if (localizedValues.keySet().contains(column)) {
            BiMap<String, String> valueToLocalized = localizedValues.get(column)
            String localizedValue = valueToLocalized.get(value)
            value = (localizedValue ?: value)
        }
        return value
    }

    @Override
    def getPossibleValues(int row, int col) {
        Object values = super.getPossibleValues(row, col)
        col--
        if (values instanceof List && localizedValues.keySet().contains(col)) {
            BiMap<String, String> valueToLocalized = localizedValues.get(col)

            List newList = []
            for (def value in values) {
                String localizedValue = valueToLocalized.get(value)
                newList << (localizedValue ?: value)
            }
            values = newList
        }
        return values
    }

    @Override
    void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (localizedValues.keySet().contains(columnIndex -1)) {
            BiMap<String, String> localizedToValue = localizedValues.get(columnIndex -1).inverse()
            String enumValue = localizedToValue.get(value)
            value = enumValue != null ? enumValue : value
        }
        super.setValueAt(value, rowIndex, columnIndex)
    }


}
