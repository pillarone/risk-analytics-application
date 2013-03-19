package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.EnumParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

class EnumParameterizationTableTreeNode extends AbstractMultiValueParameterizationTableTreeNode {

    public EnumParameterizationTableTreeNode(String path, ParametrizedItem item) {
        super(path, item)
    }

    public void setValueAt(Object value, int column) {
        int period = column - 1
        LOG.debug("Setting value to node @ ${parameterPath} P${period}")
        parametrizedItem.updateParameterValue(parameterPath, period, getKeyForValue(value))
    }

    public Object doGetExpandedCellValue(int column) {
        String value = parametrizedItem.getParameterHolder(parameterPath, column - 1)?.businessObject?.toString()
        if (value) {
            return getValueForKey(value)
        }
        else {
            return value
        }
    }

    public List initValues() {
        EnumParameterHolder enumParameterizationHolder = parametrizedItem.getParameterHoldersForAllPeriods(parameterPath)[0]
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
