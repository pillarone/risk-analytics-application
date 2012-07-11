package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

class BooleanTableTreeNode extends AbstractMultiValueParameterizationTableTreeNode {

    public BooleanTableTreeNode(String path, ParametrizedItem item) {
        super(path, item)
    }

    public void setValueAt(Object value, int column) {
        ParameterHolder parameterHolder = parametrizedItem.getParameterHolder(parameterPath, column - 1)
        LOG.debug("Setting value to node @ ${path} P${column - 1}")
        parameterHolder.value = value
    }

    public Object doGetExpandedCellValue(int column) {
        String value = parametrizedItem.getParameterHolder(parameterPath, column - 1)?.businessObject?.toString()
        return Boolean.valueOf(value)
    }

    public List initValues() {
        return [Boolean.TRUE, Boolean.FALSE]
    }
}
