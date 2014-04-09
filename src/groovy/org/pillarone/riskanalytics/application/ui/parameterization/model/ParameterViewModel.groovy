package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class ParameterViewModel extends AbstractParametrizedViewModel {

    public ParameterViewModel(Model model, Parameterization parameterization, ModelStructure structure) {
        super(model, parameterization, structure);
    }

    protected ITableTreeModel buildTree() {
        ParameterizationTableTreeModel model = super.buildTree() as ParameterizationTableTreeModel
        model.simulationModel = this.model
        return model
    }

    @Override
    protected AbstractParametrizedTableTreeModel createTableTreeModel(Object builder) {
        return new ParameterizationTableTreeModel(builder as ParameterizationTreeBuilder)
    }

    @Override
    protected createTreeBuilder() {
        ParameterizationTreeBuilder builder = new ParameterizationTreeBuilder(model, structure, item)
        periodCount = builder.periodCount
        return builder
    }

    @Override
    Parameterization getItem() {
        super.@item as Parameterization
    }
}


