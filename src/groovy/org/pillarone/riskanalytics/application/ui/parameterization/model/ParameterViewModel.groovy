package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.IParametrizedItemListener
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.application.ui.base.model.FilteringTableTreeModel

class ParameterViewModel extends AbstractParametrizedViewModel implements IParametrizedItemListener {

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

    @Override
    void componentAdded(String path, Component component) {
        getActualTableTreeModel().componentAdded(path, component)
        changedCommentListeners*.updateCommentVisualization()
    }

    @Override
    void componentRemoved(String path) {
        getActualTableTreeModel().componentRemoved(path)
        changedCommentListeners*.updateCommentVisualization()
    }

    @Override
    void parameterValuesChanged(List<String> paths) {
        getActualTableTreeModel().parameterValuesChanged(paths)
    }

    protected ParameterizationTableTreeModel getActualTableTreeModel() {
        ITableTreeModel model = treeModel
        if (model instanceof FilteringTableTreeModel) {
            model = model.model
        }
        if (model instanceof ParameterizationTableTreeModel) {
            return model
        }

        throw new IllegalStateException("Table model not found.")
    }
}


