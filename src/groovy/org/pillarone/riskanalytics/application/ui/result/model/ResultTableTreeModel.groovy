package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.function.NodeNameFunction
import org.pillarone.riskanalytics.application.dataaccess.function.ResultFunction
import org.pillarone.riskanalytics.application.dataaccess.function.SingleIteration
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.model.AsynchronTableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.PostSimulationCalculationAccessor
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class ResultTableTreeModel extends AsynchronTableTreeModel {

    List functions = []
    Map isStochasticValueForPeriod = [:]
    private ITableTreeNode rootNode
    SimulationRun simulationRun
    int columnCount
    private Parameterization parameterization
    boolean usesDeterministicModel
    ULCNumberDataType numberDataType

    protected ResultTableTreeModel(ITableTreeNode rootNode, SimulationRun simulationRun, Parameterization parameterization, IFunction mean) {
        this.rootNode = rootNode
        this.simulationRun = simulationRun
        this.parameterization = parameterization
        functions << new NodeNameFunction()
        simulationRun.periodCount.times {
            functions << mean
        }
        columnCount = 1 + simulationRun.periodCount
        usesDeterministicModel = DeterministicModel.isAssignableFrom(parameterization.modelClass)
    }

    public String getColumnName(int i) {
        String name = null
        if (i < functions.size()) {
            int periodIndex = (i - 1) % simulationRun.periodCount
            name = functions[i].getName(periodIndex)
            if (i > 0) {
                String periodLabel = parameterization.getPeriodLabel(periodIndex)
                name = name + " " + periodLabel
            }
        }
        return name
    }

    public int getColumnCount() {
        return columnCount
    }

    protected boolean loadAsynchronous(int column, def node) {
        if (usesDeterministicModel) {
            return false
        }
        boolean isResultCell = column > 0 && node instanceof ResultTableTreeNode
        if (isResultCell) {
            int periodIndex = (column - 1) % simulationRun.periodCount
            def result = PostSimulationCalculationAccessor.getResult(simulationRun, periodIndex, ResultFunction.getPath(node), node.collector, node.field, functions[column].keyFigureName, functions[column].keyFigureParameter)
            return result == null
        } else {
            return false
        }
    }

    public def getAsynchronValue(Object node, int i) {
        int periodIndex = (i - 1) % simulationRun.periodCount
        IFunction function = functions[i]
        if (isStochasticValue(node, i)) {
            return function.evaluate(simulationRun, periodIndex, node)
        } else {
            if (function.calculateForNonStochasticalValues()) {
                return function.evaluate(simulationRun, periodIndex, node)
            } else {
                return null
            }
        }
    }

    public boolean isStochasticValue(SimpleTableTreeNode node, int i) {
        true
    }

    public boolean isStochasticValue(ResultTableTreeNode node, int i) {
        if (i == 0) {
            return true
        }
        if (!isFunctionStochastic(functions[i])) {
            return false
        }
        // TODO (Jul 8, 2009, msh): if there is only one iteration, there can be no different values
        if (usesDeterministicModel) {
            return false
        }

        int periodIndex = (i - 1) % simulationRun.periodCount
        if (!isStochasticValueForPeriod.containsKey(getKey(simulationRun, periodIndex, node))) {
            simulationRun.periodCount.times {
                def result = PostSimulationCalculationAccessor.getResult(simulationRun, it, ResultFunction.getPath(node), node.collector, node.field, PostSimulationCalculation.IS_STOCHASTIC)
                if (result != null) {
                    isStochasticValueForPeriod[getKey(simulationRun, it, node)] = result.result == 0
                } else {
                    isStochasticValueForPeriod[getKey(simulationRun, it, node)] = ResultAccessor.hasDifferentValues(simulationRun, it, ResultFunction.getPath(node), node.collector, node.field)
                }
            }
        }
        return isStochasticValueForPeriod[getKey(simulationRun, periodIndex, node)]
    }

    private boolean isFunctionStochastic(IFunction function) {
        return true
    }

    private boolean isFunctionStochastic(SingleIteration function) {
        return false
    }

    private getKey(simulationRun, periodIndex, node) {
        "${simulationRun.id}:${node.path}:${node.field}:$periodIndex".toString()
    }

    public Object getRoot() {
        rootNode
    }

    public Object getChild(Object node, int i) {
        node.getChildAt(i)
    }

    public int getChildCount(Object node) {
        node.childCount
    }

    public boolean isLeaf(Object o) {
        getChildCount(o) == 0
    }

    public int getIndexOfChild(Object parent, Object child) {
        parent.getIndex(child)
    }



    private ULCNumberDataType getNumberDataType() {
        if (!numberDataType) {
            numberDataType = LocaleResources.numberDataType
            numberDataType.setGroupingUsed true
            numberDataType.setMinFractionDigits 2
            numberDataType.setMaxFractionDigits 2
        }
        return numberDataType
    }

}