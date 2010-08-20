package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.tabletree.ITableTreeNode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.function.NodeNameFunction
import org.pillarone.riskanalytics.application.dataaccess.function.ResultFunction
import org.pillarone.riskanalytics.application.dataaccess.function.SingleIteration
import org.pillarone.riskanalytics.application.ui.base.model.AsynchronTableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class ResultTableTreeModel extends AsynchronTableTreeModel {

    List functions = []
    Map isStochasticValueForPeriod = [:]
    private ITableTreeNode rootNode
    SimulationRun simulationRun
    int columnCount
    private Parameterization parameterization
    ULCNumberDataType numberDataType

    private List<String> periodLabels = []
    //A map to store all pre-calculated key figures, a ConfigObject is used to easily create nested maps
    //Will be injected by RVM
    ConfigObject results
    //used to create keys for the result map
    private NumberFormat numberFormat = NumberFormat.getInstance()

    Model simulationModel

    protected ResultTableTreeModel(ITableTreeNode rootNode, SimulationRun simulationRun, Parameterization parameterization, IFunction mean, Model model) {
        this.simulationModel = model
        this.rootNode = rootNode
        this.simulationRun = simulationRun
        this.parameterization = parameterization
        functions << new NodeNameFunction()
        simulationRun.periodCount.times {
            functions << mean
        }
        columnCount = 1 + simulationRun.periodCount
        //TODO: is this still used despite of DRTTM?

        initPeriodLabels()
    }

    protected Double getPreCalculatedValue(int period, String path, String field, String keyFigure) {
        getPreCalculatedValue(period, path, field, keyFigure, null)
    }

    /**
     * Returns the result of a post simulation calculation
     */
    protected Double getPreCalculatedValue(int period, String path, String field, String keyFigure, def param) {
        Map current = results
        if (!current.containsKey(period.toString())) return null
        current = current[period.toString()]

        if (!current.containsKey(path)) return null
        current = current[path]

        if (!current.containsKey(field)) return null
        current = current[field]

        if (!current.containsKey(keyFigure)) return null
        current = current[keyFigure]

        String p = param != null ? numberFormat.format(param) : "null"

        if (!current.containsKey(p)) return null
        return current[p]
    }

    protected boolean isValuePreCalculated(int period, String path, String field, String keyFigure) {
        getPreCalculatedValue(period, path, field, keyFigure, null) != null
    }

    protected boolean isValuePreCalculated(int period, String path, String field, String keyFigure, def param) {
        getPreCalculatedValue(period, path, field, keyFigure, param) != null
    }

    private void initPeriodLabels() {
        //Whenever possible, use the saved period labels
        if (!parameterization.periodLabels.empty) {
            periodLabels = parameterization.getPeriodLabels()
            return
        }
        //Saving period labels is not possible for certain period counters.. they have to be resolved here
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy")
        IPeriodCounter periodCounter = simulationModel.createPeriodCounter(simulationRun.beginOfFirstPeriod)
        if (periodCounter != null) {
            periodCounter.reset()
            simulationRun.periodCount.times {
                periodLabels << format.format(periodCounter.getCurrentPeriodStart().toDate())
                periodCounter.next()
            }
        } else {
            simulationRun.periodCount.times {int i ->
                periodLabels << "P$i"
            }
        }
    }

    public String getColumnName(int i) {
        String name = null
        if (i < functions.size()) {
            int periodIndex = (i - 1) % simulationRun.periodCount
            name = functions[i].getName(periodIndex)
            if (i > 0) {
                String periodLabel = periodLabels.get(periodIndex)
                name = name + " " + periodLabel
            }
        }
        return name
    }

    public int getColumnCount() {
        return columnCount
    }

    protected boolean loadAsynchronous(int column, def node) {
        try {
            boolean isResultCell = column > 0 && node instanceof ResultTableTreeNode
            if (isResultCell) {
                int periodIndex = (column - 1) % simulationRun.periodCount
                ResultFunction currentFunction = functions[column]
                boolean isPreCalculated = isValuePreCalculated(periodIndex, ResultFunction.getPath(node), node.field, currentFunction.keyFigureName, currentFunction.keyFigureParameter)
                return !isPreCalculated && !(currentFunction instanceof SingleIteration)
            } else {
                return false
            }
        } catch (Exception ex) {
            return true
        }
    }

    public def getAsynchronValue(Object node, int i) {
        int periodIndex = (i - 1) % simulationRun.periodCount
        IFunction function = functions[i]
        if (isStochasticValue(node, i)) {
            if (function instanceof ResultFunction && node instanceof ResultTableTreeNode && isValuePreCalculated(periodIndex, ResultFunction.getPath(node), node.field, function.keyFigureName, function.keyFigureParameter)) {
                return getPreCalculatedValue(periodIndex, ResultFunction.getPath(node), node.field, function.keyFigureName, function.keyFigureParameter)
            } else {
                //check if the function is not removed
                return function?.evaluate(simulationRun, periodIndex, node)
            }
        } else {
            if (function.calculateForNonStochasticalValues()) {
                if (function instanceof ResultFunction && node instanceof ResultTableTreeNode && isValuePreCalculated(periodIndex, ResultFunction.getPath(node), node.field, function.keyFigureName, function.keyFigureParameter)) {
                    return getPreCalculatedValue(periodIndex, ResultFunction.getPath(node), node.field, function.keyFigureName, function.keyFigureParameter)
                } else {
                    //check if the function is not removed
                    return function?.evaluate(simulationRun, periodIndex, node)
                }
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

        int periodIndex = (i - 1) % simulationRun.periodCount
        if (!isStochasticValueForPeriod.containsKey(getKey(simulationRun, periodIndex, node))) {
            simulationRun.periodCount.times {
                def result = getPreCalculatedValue(it, ResultFunction.getPath(node), node.field, PostSimulationCalculation.IS_STOCHASTIC)
                if (result != null) {
                    isStochasticValueForPeriod[getKey(simulationRun, it, node)] = result == 0
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
            numberDataType = DataTypeFactory.numberDataType
            numberDataType.setGroupingUsed true
            numberDataType.setMinFractionDigits 2
            numberDataType.setMaxFractionDigits 2
        }
        return numberDataType
    }

}
