package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.IParametrizedItemListener
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.application.ui.base.model.FilteringTableTreeModel

class ParameterViewModel extends AbstractParametrizedViewModel {

    public ParameterViewModel(Model model, Parameterization parameterization, ModelStructure structure) {
        super(model, parameterization, structure);
    }

    protected ITableTreeModel buildTree() {
        ParameterizationTableTreeModel model = super.buildTree()
        model.simulationModel = this.model
        return model
    }

    @Override
    protected AbstractParametrizedTableTreeModel createTableTreeModel(Object builder) {
        return new ParameterizationTableTreeModel(builder)
    }

    @Override
    protected createTreeBuilder() {
        ParameterizationTreeBuilder builder = new ParameterizationTreeBuilder(model, structure, item)
        periodCount = builder.periodCount
        return builder
    }

}


