package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.Formatter
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException

class MultiDimensionalParameterizationTableTreeNode extends ParameterizationTableTreeNode {

    private Model simulationModel

    public MultiDimensionalParameterizationTableTreeNode(List parameter, Model simulationModel) {
        super(parameter);
        this.@simulationModel = simulationModel;
    }

    public AbstractMultiDimensionalParameter getMultiDimensionalValue(int column) {
        MultiDimensionalParameterHolder parameterHolder = parameter.get(column - 1)
        AbstractMultiDimensionalParameter instance = parameterHolder?.businessObject
        injectModel(instance)
        return instance
    }

    private void injectModel(AbstractMultiDimensionalParameter instance) {
        if (instance != null) {
            instance.simulationModel = simulationModel
        }
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

    public getExpandedCellValue(int column) {
        MultiDimensionalParameterHolder parameter = parameter.get(column - 1)
        if (parameter != null) {
            AbstractMultiDimensionalParameter instance = parameter.businessObject
            injectModel(instance)
            return org.pillarone.riskanalytics.application.ui.util.Formatter.format(instance, UIUtils.clientLocale)
        } else {
            return null
        }
    }

    boolean isCellEditable(int i) {
        false
    }

}
