package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

class MultiDimensionalParameterizationTableTreeNode extends ParameterizationTableTreeNode {

    private Model simulationModel

    public MultiDimensionalParameterizationTableTreeNode(String path, ParametrizedItem item, Model simulationModel) {
        super(path, item);
        this.@simulationModel = simulationModel;
    }

    public AbstractMultiDimensionalParameter getMultiDimensionalValue(int column) {
        MultiDimensionalParameterHolder parameterHolder = parametrizedItem.getParameterHolder(parameterPath, column - 1)
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
        ParameterHolder parameterHolder = parametrizedItem.getParameterHolder(parameterPath, column - 1)
        LOG.debug("Setting value to node @ ${path} P${column - 1}")
        parameterHolder.value = value
    }

    public doGetExpandedCellValue(int column) {
        MultiDimensionalParameterHolder parameter = parametrizedItem.getParameterHolder(parameterPath, column - 1)
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
