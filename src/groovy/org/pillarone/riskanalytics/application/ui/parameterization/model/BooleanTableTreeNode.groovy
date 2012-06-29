package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException

class BooleanTableTreeNode extends AbstractMultiValueParameterizationTableTreeNode {

    public BooleanTableTreeNode(List parameters) {
        super(parameters)
    }

    public void setValueAt(Object value, int column) {
        ParameterHolder parameterHolder = parameter.get(column - 1)
        if (parameterHolder != null) {
            LOG.debug("Setting value to node @ ${path} P${column - 1}")
            parameterHolder?.value = value
        } else {
            throw new RiskAnalyticsInconsistencyException("Trying to set value to ${path} P${column - 1}, but parameter holder is null. ${parameter}")
        }
    }

    public Object getExpandedCellValue(int column) {
        String value = parameter.get(column - 1)?.businessObject?.toString()
        return Boolean.valueOf(value)
    }

    public List initValues() {
        return [Boolean.TRUE, Boolean.FALSE]
    }
}
