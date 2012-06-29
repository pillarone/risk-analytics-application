package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.simulation.item.parameter.EnumParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException

class EnumParameterizationTableTreeNode extends AbstractMultiValueParameterizationTableTreeNode {

    public EnumParameterizationTableTreeNode(List parameter) {
        super(parameter);
    }

    public void setValueAt(Object value, int column) {
        ParameterHolder parameterHolder = parameter.get(column - 1)
        if (parameterHolder != null) {
            LOG.debug("Setting value to node @ ${path} P${column - 1}")
            parameterHolder?.value = getKeyForValue(value)
        } else {
            throw new RiskAnalyticsInconsistencyException("Trying to set value to ${path} P${column - 1}, but parameter holder is null. ${parameter}")
        }
    }

    public Object getExpandedCellValue(int column) {
        String value = parameter.get(column - 1)?.businessObject?.toString()
        if (value) {
            return getValueForKey(value)
        }
        else {
            return value
        }
    }

    public List initValues() {
        EnumParameterHolder enumParameterizationHolder = parameter.find { it != null }
        def possibleValues = enumParameterizationHolder.getBusinessObject().values()
        List allValues = []
        possibleValues.each {
            String resourceBundleKey = it.toString()
            String value = I18NUtils.findParameterDisplayName(parent, name + "." + resourceBundleKey)
            if (value == null) {
                value = I18NUtils.findEnumDisplayName(enumParameterizationHolder.getBusinessObject().class.name, it.toString())
            }
            if (value != null) {
                allValues << value
            } else {
                allValues << resourceBundleKey
            }
            localizedValues[value] = resourceBundleKey
            localizedKeys[resourceBundleKey] = value != null ? value : resourceBundleKey
        }
        return allValues
    }

}
