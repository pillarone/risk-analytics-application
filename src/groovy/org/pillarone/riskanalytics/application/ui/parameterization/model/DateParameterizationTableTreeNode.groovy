package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

class DateParameterizationTableTreeNode extends ParameterizationTableTreeNode {

    public DateParameterizationTableTreeNode(String path, ParametrizedItem item) {
        super(path, item);
    }


    public void setValueAt(Object value, int column) {
        ParameterHolder parameterHolder = parametrizedItem.getParameterHolder(parameterPath, column - 1)
        LOG.debug("Setting value to node @ ${path} P${column - 1}")
        parameterHolder.value = new DateTime(value)
    }

    public Object doGetExpandedCellValue(int column) {
        return parametrizedItem.getParameterHolder(parameterPath, column - 1)?.businessObject?.toDate()
    }

}
