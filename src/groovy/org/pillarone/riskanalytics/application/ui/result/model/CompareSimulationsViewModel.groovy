package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.application.dataaccess.function.CompareFunction
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.FilteringTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.action.MeanAction
import org.pillarone.riskanalytics.application.ui.result.model.CompareResultTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTreeBuilder
import org.pillarone.riskanalytics.application.ui.result.view.ICompareFunctionListener
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class CompareSimulationsViewModel extends AbstractModellingModel {

    private List<ICompareFunctionListener> listeners = []

    public CompareSimulationsViewModel(Model model, ModelStructure structure, List simulations) {
        super(model, simulations*.item, structure)
    }

    void addFunctionListener(ICompareFunctionListener listener) {
        listeners << listener
    }

    void removeListener(ICompareFunctionListener listener) {
        listeners.remove(listener)
    }

    protected ITableTreeModel buildTree() {
        builder = new ResultTreeBuilder(model, structure, item[0])
        builder.applyResultPaths()

        treeRoot = builder.root
        MeanAction meanAction = new MeanAction(this, null)

        ITableTreeModel resultTreeTableModel = new CompareResultTableTreeModel(treeRoot, item, meanAction.getFunction())//*.simulationRun
        // todo (msh): This is normally done in super ctor but here the simulationRun is required for the treeModel
        return new FilteringTableTreeModel(resultTreeTableModel, filter)
    }

    /**
     * add a function for every simulation s period
     *
     */
    void addFunction(IFunction function) {
        function instanceof CompareFunction ? treeModel.addCompareFunction(function) : treeModel.addFunction(function)
        notifyFunctionAdded(function)
    }

    void removeFunction(IFunction function) {
        treeModel.removeFunction(function)
        notifyFunctionRemoved(function)
    }

    void notifyFunctionAdded(IFunction function) {
        for (ICompareFunctionListener listener in listeners) {
            listener.functionAdded(function)
        }
    }

    void notifyFunctionRemoved(IFunction function) {
        for (ICompareFunctionListener listener in listeners) {
            listener.functionRemoved(function)
        }
    }

    void notifyFunctionsChanged() {
        for (ICompareFunctionListener listener in listeners) {
            listener.functionsChanged()
        }
    }

    void setReferenceSimulation(Simulation simulation) {
        treeModel.referenceSimulation = simulation
        treeModel.clearCache()
        notifyFunctionsChanged()
    }

    void addSimulation(Simulation simulation) {
        treeModel.addSimulation(simulation)
        notifyFunctionsChanged()
    }

    void removeSimulation(Simulation simulation) {
        treeModel.removeSimulation(simulation)
        notifyFunctionsChanged()
    }

    void setOrderByKeyfigure(boolean b) {
        treeModel.orderByKeyfigure = b
        notifyFunctionsChanged()
    }



    void setInterval(double min, double max) {
        treeModel.setInterval(min, max)
        notifyFunctionsChanged()
    }

    void adjust(int adjustment) {
        treeModel.numberDataType.maxFractionDigits = treeModel.numberDataType.maxFractionDigits + adjustment
        treeModel.numberDataType.minFractionDigits = treeModel.numberDataType.minFractionDigits + adjustment
        refreshNodes()
    }

    protected void refreshNodes() {
        for (ICompareFunctionListener listener in listeners) {
            listener.refreshNodes()
        }
    }

}
