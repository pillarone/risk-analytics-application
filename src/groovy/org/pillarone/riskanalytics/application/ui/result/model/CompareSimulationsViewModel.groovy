package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.base.application.tabletree.ITableTreeModel
import java.text.NumberFormat
import org.pillarone.riskanalytics.application.dataaccess.function.CompareFunction
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.output.structure.ResultStructureTreeBuilder
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.FilteringTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.action.MeanAction
import org.pillarone.riskanalytics.application.ui.result.view.ICompareFunctionListener
import org.pillarone.riskanalytics.application.ui.result.view.ItemsComboBoxModel

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class CompareSimulationsViewModel extends AbstractModellingModel {

    private List<ICompareFunctionListener> listeners = []
    List resultStructures
    ItemsComboBoxModel selectionViewModel
    ConfigObject allResults = null
    List<ConfigObject> resultsList

    public CompareSimulationsViewModel(Model model, ModelStructure structure, List simulations) {
        super(model, simulations*.item, structure)
        model.init()
        buildTreeStructure()
        selectionViewModel = new ItemsComboBoxModel(resultStructures)
    }

    void addFunctionListener(ICompareFunctionListener listener) {
        listeners << listener
    }

    void removeListener(ICompareFunctionListener listener) {
        listeners.remove(listener)
    }

    @Override protected ITableTreeModel buildTree() {
        return null
    }


    public ITableTreeModel buildTreeStructure(ResultStructure resultStructure = null) {
        //All pre-calculated results, used in the RTTM. We already create it here because this is the fastest way to obtain
        //all result paths for this simulation run
        if (!allResults)
            allResults = initPostSimulationCalculations(item*.simulationRun)

        Set paths = new HashSet()
        //look through all periods, not all paths may have a result in the first period
        for (Map<String, Map> periodResults in allResults.values()) {
            paths.addAll(ResultViewUtils.obtainAllPaths(periodResults))
        }

        Class modelClass = model.class
        if (!resultStructures) {
            resultStructures = ModellingItemFactory.getResultStructuresForModel(modelClass)
        }

        if (!resultStructure)
            resultStructure = resultStructures[0]

        resultStructure.load()
        builder = new ResultStructureTreeBuilder(obtainsCollectors(item*.simulationRun, paths.toList()), modelClass, resultStructure, item[0])

        def treeRoot = builder.buildTree()

        MeanAction meanAction = new MeanAction(this, null)
        List<ConfigObject> resultsList = []
        if (!resultsList) {
            resultsList = []
            item.each {
                ConfigObject configObject = ResultViewUtils.initPostSimulationCalculations(it.simulationRun)
                resultsList << configObject
            }
        }
        ITableTreeModel resultTreeTableModel = new CompareResultTableTreeModel(treeRoot, item, meanAction.getFunction(), resultsList)
        treeModel = new FilteringTableTreeModel(resultTreeTableModel, filter)
    }

    public static Map<String, ICollectingModeStrategy> obtainsCollectors(List<SimulationRun> simulationRuns, List allPaths) {
        Map<String, ICollectingModeStrategy> result = [:]
        StringBuilder query = new StringBuilder("SELECT path.pathName, field.fieldName, collector.collectorName FROM org.pillarone.riskanalytics.core.output.PostSimulationCalculation as p where ")
        simulationRuns.eachWithIndex {SimulationRun simulationRun, int index ->
            query.append(" p.run.id = '" + simulationRun.id + "' ")
            if (index < simulationRuns.size() - 1)
                query.append(" or ")
        }
        List<Object[]> calculations = PostSimulationCalculation.executeQuery(query.toString())
        for (Object[] psc in calculations) {
            String path = "${psc[0]}:${psc[1]}"
            String collector = psc[2]
            if (allPaths.contains(path)) {
                result.put(path, CollectingModeFactory.getStrategy(collector))
            }
        }

        return result
    }

    /**
     * Loads all PostSimulationCalculations of a simulation and stores them in a map.
     * This is faster than creating a query for every cell when the result is needed.
     */
    public static ConfigObject initPostSimulationCalculations(List<SimulationRun> simulationRuns) {
        NumberFormat numberFormat = NumberFormat.getInstance()
        ConfigObject results = new ConfigObject()
        StringBuilder query = new StringBuilder("SELECT period, path.pathName, field.fieldName, keyFigure, keyFigureParameter, result FROM org.pillarone.riskanalytics.core.output.PostSimulationCalculation as p where ")
        simulationRuns.eachWithIndex {SimulationRun simulationRun, int index ->
            query.append(" p.run.id = '" + simulationRun.id + "' ")
            if (index < simulationRuns.size() - 1)
                query.append(" or ")
        }
        query.append(" order by p.keyFigureParameter asc")
        List<Object[]> calculations = PostSimulationCalculation.executeQuery(query.toString())
        for (Object[] psc in calculations) {
            Map periodMap = results[psc[0].toString()]
            Map pathMap = periodMap[psc[1]]
            Map fieldMap = pathMap[psc[2]]
            Map keyFigureMap = fieldMap[psc[3]]
            BigDecimal keyFigureParameter = psc[4]
            String param = keyFigureParameter != null ? numberFormat.format(keyFigureParameter) : "null"
            if (!keyFigureMap.containsKey(param)) {
                keyFigureMap[param] = psc[5]
            }
        }

        return results
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


    public void resultStructureChanged() {
        buildTreeStructure(selectionViewModel.getSelectedObject())
    }

}

public class TestCompareSimulationsViewModel extends CompareSimulationsViewModel {

    public TestCompareSimulationsViewModel(Model model, ModelStructure structure, List simulations) {
        super(model, structure, simulations);
    }

    protected changeUpdateMode(def model) {

    }


}
