package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.function.MeanFunction
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.output.structure.ResultStructureTreeBuilder
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.application.ui.base.model.FilteringTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.action.keyfigure.DefaultToggleValueProvider
import org.pillarone.riskanalytics.application.ui.result.action.keyfigure.ToggleKeyFigureAction
import org.pillarone.riskanalytics.application.ui.result.view.IFunctionListener
import org.pillarone.riskanalytics.application.ui.result.view.ItemsComboBoxModel
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class ResultViewModel extends AbstractCommentableItemModel {

    private List<IFunctionListener> listeners = new ArrayList()

    int periodCount

    List resultStructures
    ItemsComboBoxModel selectionViewModel
    ConfigObject allResults = null

    public ResultViewModel(Model model, ModelStructure structure, Simulation simulation) {
        super(model, simulation, structure)

        model.init()
        resultStructures = ModellingItemFactory.getResultStructuresForModel(model.class)
        selectionViewModel = new ItemsComboBoxModel(resultStructures, "DEFAULT_VIEW" + model.name)
        simulation.load()
        buildTreeStructure(selectionViewModel.getSelectedObject())
    }


    protected void buildTreeStructure(ResultStructure resultStructure) {
        ParameterizationDAO.withTransaction {status ->
            //parameterization is required for certain models to obtain period labels
            Parameterization parameterization = item.parameterization
            if (!parameterization.isLoaded())
                parameterization.load(false)

            if (!allResults) {
                //All pre-calculated results, used in the RTTM. We already create it here because this is the fastest way to obtain
                //all result paths for this simulation run
                allResults = initPostSimulationCalculations(item.simulationRun)

            }
            Set paths = new HashSet()
            //look through all periods, not all paths may have a result in the first period
            for (Map<String, Map> periodResults in allResults.values()) {
                paths.addAll(obtainAllPaths(periodResults))
            }

            def simulationRun = item.simulationRun

            resultStructure.load()
            builder = new ResultStructureTreeBuilder(obtainsCollectors(simulationRun, paths.toList()), model, resultStructure, item)

            def localTreeRoot = builder.buildTree()
            periodCount = simulationRun.periodCount

            ToggleKeyFigureAction meanAction = new ToggleKeyFigureAction(new MeanFunction(), new DefaultToggleValueProvider(null), this, null)

            // todo (msh): This is normally done in super ctor but here the simulationRun is required for the treeModel
            treeModel = new FilteringTableTreeModel(getResultTreeTableModel(model, meanAction, parameterization, simulationRun, localTreeRoot, allResults), filter)
            nodeNames = extractNodeNames(treeModel)
        }

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

    protected ITableTreeModel getResultTreeTableModel(Model model, ToggleKeyFigureAction meanAction, Parameterization parameterization, simulationRun, ITableTreeNode treeRoot, ConfigObject results) {
        ResultTableTreeModel tableTreeModel = new ResultTableTreeModel(treeRoot, simulationRun, parameterization, meanAction.getFunction(), model)
        tableTreeModel.simulationModel = model
        tableTreeModel.results = results
        return tableTreeModel
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
            //do not change list size, because the view always increases the model index of new columns
            int index = treeModel.functions.indexOf(function)
            while (index >= 0) {
                treeModel.functions[index] = null
                index = treeModel.functions.indexOf(function)
            }
            treeModel.columnCount -= periodCount
            notifyFunctionRemoved(function)
        }
    }

    void adjust(int adjustment) {
        treeModel.numberDataType.maxFractionDigits = treeModel.numberDataType.maxFractionDigits + adjustment
        treeModel.numberDataType.minFractionDigits = treeModel.numberDataType.minFractionDigits + adjustment
        refreshNodes()
    }

    boolean isFunctionAdded(IFunction function) {
        for (IFunction f in treeModel.functions) {
            if (f != null && f.getName().equals(function.getName())) {
                return true
            }
        }
        return false
    }

    public void resultStructureChanged() {
        buildTreeStructure(selectionViewModel.getSelectedObject())
    }

    protected List<String> obtainAllPaths(ConfigObject paths) {
        ResultViewUtils.obtainAllPaths(paths)
    }

}
