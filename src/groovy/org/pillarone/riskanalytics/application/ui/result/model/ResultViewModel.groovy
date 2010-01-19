package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.FilteringTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.action.MeanAction
import org.pillarone.riskanalytics.application.ui.result.model.DeterministicResultTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTreeBuilder
import org.pillarone.riskanalytics.application.ui.result.view.IFunctionListener
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class ResultViewModel extends AbstractModellingModel {

    private List<IFunctionListener> listeners = new ArrayList()

    int periodCount

    public ResultViewModel(Model model, ModelStructure structure, Simulation simulation) {
        super(model, simulation, structure)
        def simulationRun = item.simulationRun
        builder = new ResultTreeBuilder(model, structure, item)
        builder.applyResultPaths()
        treeRoot = builder.root
        periodCount = simulationRun.periodCount
        Parameterization parameterization = simulation.parameterization
        parameterization.load()
        MeanAction meanAction = new MeanAction(this, null)
        // todo (msh): This is normally done in super ctor but here the simulationRun is required for the treeModel
        treeModel = new FilteringTableTreeModel(getResultTreeTableModel(model, meanAction, parameterization, simulationRun, treeRoot), filter)
        nodeNames = extractNodeNames(treeModel)
    }

    private ITableTreeModel getResultTreeTableModel(Model model, MeanAction meanAction, Parameterization parameterization, simulationRun, ITableTreeNode treeRoot) {
        return new ResultTableTreeModel(treeRoot, simulationRun, parameterization, meanAction.getFunction())
    }

    private ITableTreeModel getResultTreeTableModel(DeterministicModel model, MeanAction meanAction, Parameterization parameterization, simulationRun, ITableTreeNode treeRoot) {
        return new DeterministicResultTableTreeModel(treeRoot, simulationRun, parameterization)
    }

    protected ITableTreeModel buildTree() {
    }

    void addFunctionListener(IFunctionListener listener) {
        listeners.add(listener)
    }

    void removeFunctionListener(IFunctionListener listener) {
        listeners.remove(listener)
    }

    protected void notifyFunctionAdded(IFunction function) {
        for (IFunctionListener listener in listeners) {
            listener.functionAdded(function)
        }
    }

    protected void notifyFunctionRemoved(IFunction function) {
        for (IFunctionListener listener in listeners) {
            listener.functionRemoved(function)
        }
    }

    protected void refreshNodes() {
        for (IFunctionListener listener in listeners) {
            listener.refreshNodes()
        }
    }

    void addFunction(IFunction function) {
        if (!treeModel.functions.contains(function)) {
            periodCount.times {
                treeModel.functions.add(function)
            }
            treeModel.columnCount += periodCount
            notifyFunctionAdded(function)
        }
        ((AbstractTableTreeModel) treeModel).nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(treeModel.root) as Object[]))
    }

    void removeFunction(IFunction function) {
        if (treeModel.functions.contains(function)) {
            treeModel.functions.remove(function)
            treeModel.columnCount -= periodCount
            notifyFunctionRemoved(function)
        }
    }

    void adjust(int adjustment) {
        treeModel.numberDataType.maxFractionDigits = treeModel.numberDataType.maxFractionDigits + adjustment
        treeModel.numberDataType.minFractionDigits = treeModel.numberDataType.minFractionDigits + adjustment
        refreshNodes()
    }
}