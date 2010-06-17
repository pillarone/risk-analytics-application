package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import java.text.NumberFormat
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.FilteringTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.action.MeanAction
import org.pillarone.riskanalytics.application.ui.result.view.IFunctionListener
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class ResultViewModel extends AbstractModellingModel {

    private List<IFunctionListener> listeners = new ArrayList()

    int periodCount

    public ResultViewModel(Model model, ModelStructure structure, Simulation simulation) {
        super(model, simulation, structure)

        model.init()
        ParameterizationDAO.withTransaction {status ->
            //parameterization is required for certain models to obtain period labels
            Parameterization parameterization = simulation.parameterization
            parameterization.load(false)

            //All pre-calculated results, used in the RTTM. We already create it here because this is the fastest way to obtain
            //all result paths for this simulation run
            ConfigObject allResults = initPostSimulationCalculations(simulation.simulationRun)
            List paths = allResults."0".keySet().toList()

            def simulationRun = item.simulationRun
            builder = new ResultTreeBuilder(model, structure, item, paths)
            builder.applyResultPaths()


            treeRoot = builder.root
            periodCount = simulationRun.periodCount

            MeanAction meanAction = new MeanAction(this, null)

            // todo (msh): This is normally done in super ctor but here the simulationRun is required for the treeModel
            treeModel = new FilteringTableTreeModel(getResultTreeTableModel(model, meanAction, parameterization, simulationRun, treeRoot, allResults), filter)
            nodeNames = extractNodeNames(treeModel)
        }

    }

    /**
     * Loads all PostSimulationCalculations of a simulation and stores them in a map.
     * This is faster than creating a query for every cell when the result is needed.
     */
    public static ConfigObject initPostSimulationCalculations(SimulationRun simulationRun) {
        NumberFormat numberFormat = NumberFormat.getInstance()
        ConfigObject results = new ConfigObject()

        List<Object[]> calculations = PostSimulationCalculation.executeQuery("SELECT period, path.pathName, field.fieldName, keyFigure, keyFigureParameter, result FROM org.pillarone.riskanalytics.core.output.PostSimulationCalculation as p " +
                " WHERE p.run.id = ? order by p.keyFigureParameter asc", [simulationRun.id])
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

    private ITableTreeModel getResultTreeTableModel(Model model, MeanAction meanAction, Parameterization parameterization, simulationRun, ITableTreeNode treeRoot, ConfigObject results) {
        ResultTableTreeModel tableTreeModel = new ResultTableTreeModel(treeRoot, simulationRun, parameterization, meanAction.getFunction(), model)
        tableTreeModel.simulationModel = model
        tableTreeModel.results = results
        return tableTreeModel
    }

    private ITableTreeModel getResultTreeTableModel(DeterministicModel model, MeanAction meanAction, Parameterization parameterization, simulationRun, ITableTreeNode treeRoot, ConfigObject results) {
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
            periodCount.times {
                treeModel.functions.remove(function)
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
        for (IFunction iFunction in treeModel.functions) {
            if (iFunction.name.equals(function.name))
                return true
        }
        return false
    }
}
