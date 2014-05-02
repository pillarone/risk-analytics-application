package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.components.DataSourceDefinition
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.DataSourceParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder


class ResultDataParameterizationTableTreeNode extends ParameterizationTableTreeNode {

    ResultDataParameterizationTableTreeNode(String path, ParametrizedItem item) {
        super(path, item)
    }

    @Override
    void setValueAt(Object value, int column) {
        int period = column - 1
        LOG.debug("Setting value (${value}) to node @ ${parameterPath} P${period}")
        parametrizedItem.updateParameterValue(parameterPath, period, value)
    }

    @Override
    Object doGetExpandedCellValue(int column) {
        DataSourceParameterHolder parameter = parametrizedItem.getParameterHolder(parameterPath, column - 1)

        return parameter.definition.toString()
    }

    @Override
    boolean isCellEditable(int i) {
        return false
    }

    DataSourceDefinition getDefinition(int column) {
        DataSourceParameterHolder parameter = parametrizedItem.getParameterHolder(parameterPath, column - 1)

        return parameter.definition
    }
}
