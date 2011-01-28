package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.application.dataaccess.function.CompareFunction
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.output.structure.ResultStructureTreeBuilder
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.FilteringTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.action.MeanAction
import org.pillarone.riskanalytics.application.ui.result.view.ICompareFunctionListener

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
        //All pre-calculated results, used in the RTTM. We already create it here because this is the fastest way to obtain
        //all result paths for this simulation run
        ConfigObject allResults = ResultViewModel.initPostSimulationCalculations(item[0]?.simulationRun)

        //List paths = ResultViewModel.obtainAllPaths(allResults."0")
        //Map collectors = ResultViewModel.obtainsCollectors(item[0]?.simulationRun, paths)
        Set paths = new HashSet()
        //look through all periods, not all paths may have a result in the first period
        for (Map<String, Map> periodResults in allResults.values()) {
            paths.addAll(ResultViewModel.obtainAllPaths(periodResults))
        }

        Class modelClass = model.class
        def simulationRun = item[0].simulationRun

        ResultStructure resultStructure = ModellingItemFactory.getResultStructuresForModel(modelClass)[0]

        resultStructure.load()
        builder = new ResultStructureTreeBuilder(ResultViewModel.obtainsCollectors(simulationRun, paths.toList()), modelClass, resultStructure, item[0])

        treeRoot = builder.buildTree()

        MeanAction meanAction = new MeanAction(this, null)
        List<ConfigObject> resultsList = []
        item.each {
            ConfigObject configObject = ResultViewModel.initPostSimulationCalculations(it.simulationRun)
            resultsList << configObject
        }
        ITableTreeModel resultTreeTableModel = new CompareResultTableTreeModel(treeRoot, item, meanAction.getFunction(), resultsList)
        // todo (msh): This is normally done in super ctor but here the simulationRun is required for the treeModel
        return new FilteringTableTreeModel(resultTreeTableModel, filter)
    }

    /**
     * add a function for every simulation s period
     *
     */
    void addFunction(IFunction function) {
        treeModel.clearCache()
        function instanceof CompareFunction ? treeModel.addCompareFunction(function) : treeModel.addFunction(function)
        notifyFunctionAdded(function)
    }

    void removeFunction(IFunction function) {
        treeModel.clearCache()
        treeModel.removeFunction(function)
        notifyFunctionRemoved(function)
    }

    void notifyFunctionAdded(IFunction function) {
        for (ICompareFunctionListener listener in listeners) {
            listener.functionAdded(function)
        }
    }

    boolean isFunctionAdded(IFunction function) {
        for (IFunction iFunction in treeModel.functions) {
            if (iFunction.name.equals(function.name))
                return true
        }
        return false
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

public class TestCompareSimulationsViewModel extends CompareSimulationsViewModel {

    public TestCompareSimulationsViewModel(Model model, ModelStructure structure, List simulations) {
        super(model, structure, simulations);
    }

    protected changeUpdateMode(def model) {

    }


}
