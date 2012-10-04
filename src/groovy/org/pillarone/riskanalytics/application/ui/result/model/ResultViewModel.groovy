package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.dataaccess.function.FunctionDescriptor
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.base.model.FilteringTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.view.IFunctionListener
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class ResultViewModel extends AbstractResultViewModel {

    private List<IFunctionListener> listeners = new ArrayList()
    private List<FunctionDescriptor> openFunctions = []


    public ResultViewModel(Model model, ModelStructure structure, Simulation simulation) {
        super(model, simulation, structure)
    }

    /**
     * Loads all PostSimulationCalculations of a simulation and stores them in a map.
     * This is faster than creating a query for every cell when the result is needed.
     */
    protected ConfigObject initPostSimulationCalculations(SimulationRun simulationRun) {
        ResultViewUtils.initPostSimulationCalculations(simulationRun)
    }

    protected Map<String, ICollectingModeStrategy> obtainsCollectors(SimulationRun simulationRun, List allPaths) {
        ResultViewUtils.obtainsCollectors(simulationRun, allPaths)
    }

    protected ITableTreeModel getResultTreeTableModel(Model model, Parameterization parameterization, simulationRun, ITableTreeNode treeRoot, ConfigObject results) {
        ResultTableTreeModel tableTreeModel = new ResultTableTreeModel(treeRoot, simulationRun, parameterization, model)
        tableTreeModel.simulationModel = model
        tableTreeModel.results = results
        return tableTreeModel
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
        if (openFunctions.contains(function.createDescriptor())) {
            return
        }
        periodCount.times {
            getResultTableTreeModel().functions.add(function)
        }
        getResultTableTreeModel().columnCount += periodCount
        notifyFunctionAdded(function)
        getResultTableTreeModel().nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(treeModel.root) as Object[]))
        openFunctions << function.createDescriptor()
    }

    void removeFunction(IFunction function) {
        if (openFunctions.contains(function.createDescriptor())) {
            //do not change list size, because the view always increases the model index of new columns
            int index = getResultTableTreeModel().functions.indexOf(function)
            while (index >= 0) {
                getResultTableTreeModel().functions[index] = null
                index = getResultTableTreeModel().functions.indexOf(function)
            }
            getResultTableTreeModel().columnCount -= periodCount
            notifyFunctionRemoved(function)
            openFunctions.remove(function.createDescriptor())
        }
    }

    void adjust(int adjustment) {
        getResultTableTreeModel().numberDataType.maxFractionDigits = getResultTableTreeModel().numberDataType.maxFractionDigits + adjustment
        getResultTableTreeModel().numberDataType.minFractionDigits = getResultTableTreeModel().numberDataType.minFractionDigits + adjustment
        refreshNodes()
    }

    public void resultStructureChanged() {
        openFunctions.clear()
        super.resultStructureChanged()
    }

    private ResultTableTreeModel getResultTableTreeModel() {
        ITableTreeModel model = treeModel
        if (model instanceof FilteringTableTreeModel) {
            model = model.model
        }
        if (model instanceof ResultTableTreeModel) {
            return model
        } else {
            throw new IllegalStateException("Result table tree model not found.")
        }
    }

}
