package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException

class DateParameterizationTableTreeNode extends ParameterizationTableTreeNode {

    public DateParameterizationTableTreeNode(List parameter) {
        super(parameter);
    }


    public void setValueAt(Object value, int column) {
        ParameterHolder parameterHolder = parameter.get(column - 1)
        if (parameterHolder != null) {
            LOG.debug("Setting value to node @ ${path} P${column - 1}")
            parameterHolder?.value = new DateTime(value)
        } else {
            throw new RiskAnalyticsInconsistencyException("Trying to set value to ${path} P${column - 1}, but parameter holder is null. ${parameter}")
        }
    }

    public Object getExpandedCellValue(int column) {
        return parameter.get(column - 1)?.businessObject?.toDate()
    }

}
