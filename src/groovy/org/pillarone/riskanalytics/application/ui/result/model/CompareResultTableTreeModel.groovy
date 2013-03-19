package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.datatype.ULCNumberDataType
import java.text.NumberFormat
import org.pillarone.riskanalytics.application.ui.base.model.AsynchronTableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.util.SimulationUtilities
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.dataaccess.function.*
import org.pillarone.riskanalytics.core.output.SimulationRun

class CompareResultTableTreeModel extends AsynchronTableTreeModel {

    private List<SimulationRun> simulationRuns
    private List<ConfigObject> resultsList
    List<Simulation> simulations
    private List<SimulationRun> hiddenSimulations = []
    private List<IFunction> functions = []
    private List<AbstractComparisonFunction> compareFunctions = []
    private int minPeriodCount
    private NodeNameFunction nameFunction = new NodeNameFunction()
    private SimpleTableTreeNode root
    double minValue = 0
    double maxValue = 0
    ULCNumberDataType numberDataType
    List periodLabels = []

    boolean orderByKeyfigure

    private NumberFormat numberFormat = NumberFormat.getInstance()

    public CompareResultTableTreeModel(SimpleTableTreeNode rootNode, List<Simulation> simulations, IFunction mean, List<ConfigObject> resultsList) {
        super();
        this.root = rootNode
        if (simulations.size() < 2) {
            throw new IllegalArgumentException("At least 2 simulationRuns required.")
        }
        this.simulationRuns = simulations*.simulationRun
        this.simulations = simulations
        minPeriodCount = SimulationUtilities.getMinSimulationsPeriod(simulationRuns)
        this.resultsList = resultsList
        orderByKeyfigure = true

        if (mean) {
            addFunction(mean)
        }
        initPeriodLabels()
    }

    public Object getAsynchronValue(Object node, int column) {
        IFunction function = getFunction(column).clone()
        int periodIndex = getPeriodIndex(column)
        int simulationIndex = getSimulationRunIndex(column)
        ComparisonSimulationRunHolder simulationRunHolder = new ComparisonSimulationRunHolder(simulationRuns)
        simulationRunHolder.referenceRun = simulationRuns[0]
        simulationRunHolder.runToCompare = getSecondRun(column)

        def parameter = function instanceof IParametrizedFunction ? function.parameter : null
        if (isResultFunction(function, node) && isValuePreCalculated(periodIndex, node.path, node.field, function.keyFigureName, parameter, simulationIndex)) {
            return getPreCalculatedValue(periodIndex, node.path, node.field, function.keyFigureName, parameter, simulationIndex)
        } else if (function instanceof AbstractComparisonFunction && (node instanceof ResultTableTreeNode)) {
            return function.evaluate(simulationRunHolder, periodIndex, node)
        } else {
            return function.evaluate(new SimulationRunHolder(simulationRuns[simulationIndex]), periodIndex, node as SimpleTableTreeNode)
        }

    }

    private boolean isResultFunction(IFunction function, node) {
        return (function instanceof AbstractResultFunction) && !(function instanceof AbstractComparisonFunction) && (node instanceof ResultTableTreeNode)
    }

    public String getColumnName(int column) {
        int runIndex = getSimulationRunIndex(column);
        if (runIndex >= 0) {
            return "${SimulationUtilities.RESULT_CHAR_PREFIXES[runIndex]} : ${periodLabels[runIndex][getPeriodIndex(column)]}: ${getFunction(column).displayName}"
        } else {
            runIndex = simulationRuns.indexOf(getSecondRun(column))
            return "${SimulationUtilities.RESULT_CHAR_PREFIXES[runIndex]} :${periodLabels[runIndex][getPeriodIndex(column)]}-${periodLabels[0][getPeriodIndex(column)]} : ${getFunction(column).displayName}: ${getFunction(column).underlyingFunction.displayName}"
        }
    }

    protected boolean loadAsynchronous(int column, Object node) {
        try {
            boolean isResultCell = column > 0 && node instanceof ResultTableTreeNode
            if (isResultCell) {
                int periodIndex = getPeriodIndex(column)
                AbstractResultFunction currentFunction = getFunction(column).clone()
                def parameter = currentFunction instanceof IParametrizedFunction ? currentFunction.parameter : null
                boolean isPreCalculated = isValuePreCalculated(periodIndex, node.path, node.field, currentFunction.keyFigureName, parameter)
                return !isPreCalculated
            } else {
                return false
            }
        } catch (Exception ex) {
            return true
        }
    }

    int getSimulationRunIndex(int column) {
        if (column == 0) return 0
        int blockSize = simulationRuns.size() + compareFunctions.size() * (simulationRuns.size() - 1)
        int blockPosition = (column - 1) % blockSize
        return blockPosition < simulationRuns.size() ? blockPosition : -1
    }

    int getSimulationRunIndex(SimulationRun simulationRun) {
        return simulationRuns.indexOf(simulationRun)
    }

    int getPeriodIndex(int column) {
        if (column == 0) return 0
        int blockSize = simulationRuns.size() + compareFunctions.size() * (simulationRuns.size() - 1)
        int blockIndex = (column - 1) / blockSize
        return orderByKeyfigure ? blockIndex % minPeriodCount : blockIndex / functions.size()
    }

    IFunction getFunction(int column) {
        if (column == 0) return nameFunction
        int functionIndex

        int blockSize = simulationRuns.size() + compareFunctions.size() * (simulationRuns.size() - 1)
        int blockPosition = (column - 1) % blockSize
        if (orderByKeyfigure) {
            int functionBlockSize = blockSize * minPeriodCount
            functionIndex = (column - 1) / functionBlockSize
        } else {
            int blockIndex = (column - 1) / blockSize
            functionIndex = blockIndex % functions.size()
        }


        IFunction function = blockPosition < simulationRuns.size() ?
            functions.get(functionIndex) : getCompareFunction(blockPosition)
        if (function instanceof AbstractComparisonFunction) {
            function.underlyingFunction = functions.get(functionIndex)
        }
        return function
    }

    private SimulationRun getSecondRun(int column) {
        if (column == 0) return null
        int functionIndex

        int blockSize = simulationRuns.size() + compareFunctions.size() * (simulationRuns.size() - 1)
        int blockPosition = (column - 1) % blockSize
        blockPosition = blockPosition - simulationRuns.size()

        int simulationIndex = blockPosition % (simulationRuns.size() - 1) + 1
        return simulationRuns[simulationIndex]

    }

    private IFunction getCompareFunction(int blockPosition) {
        blockPosition = blockPosition - simulationRuns.size()
        int compareFunctionIndex = blockPosition / (simulationRuns.size() - 1)
        return compareFunctions.get(compareFunctionIndex)
    }

    public void addFunction(IFunction function) {
        functions << function
    }

    public void addCompareFunction(AbstractComparisonFunction function) {
        compareFunctions << function
    }

    void removeSimulation(Simulation simulation) {
        hiddenSimulations.add(simulation.simulationRun)
    }

    void addSimulation(Simulation simulation) {
        hiddenSimulations.remove(simulation.simulationRun)
    }

    boolean isHidden(int columnIndex) {
        int simulationIndex = getSimulationRunIndex(columnIndex)
        if (simulationIndex != -1) {
            SimulationRun simulationRun = simulationRuns.get(simulationIndex)
            return hiddenSimulations.contains(simulationRun)
        } else {
            IFunction function = getFunction(columnIndex)
            if (function instanceof AbstractComparisonFunction) {
                return hiddenSimulations.contains(getSecondRun(columnIndex))
            }
        }
        return false
    }

    void removeFunction(IFunction function) {
        functions.remove(function)
        compareFunctions.remove(function)
    }

    void setReferenceSimulation(Simulation simulation) {
        int index = simulationRuns.indexOf(simulation.simulationRun)
        if (index == 0) {
            return
        }

        Collections.swap(simulationRuns, 0, index)
        Collections.swap(resultsList, 0, index)
    }

    void setInterval(double min, double max) {
        minValue = min
        maxValue = max
    }

    public Object getRoot() {
        root
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

    public int getColumnCount() {
        1 + (simulationRuns.size() + compareFunctions.size() * (simulationRuns.size() - 1)) * minPeriodCount * functions.size()
    }

    protected boolean isValuePreCalculated(int period, String path, String field, String keyFigure, def param, int columnIndex) {
        getPreCalculatedValue(period, path, field, keyFigure, param, columnIndex) != null
    }

    /**
     * Returns the result of a post simulation calculation
     */
    protected Double getPreCalculatedValue(int period, String path, String field, String keyFigure, def param, int simulationIndex) {
        Map current = resultsList.get(simulationIndex)
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

    private void initPeriodLabels() {
        SimulationRun.withTransaction {status ->
            simulations.each {Simulation simulation ->
                if (!simulation.parameterization.isLoaded())
                    simulation.parameterization.load(false)
                def labels = []
                simulation.simulationRun.periodCount.times {int index ->
                    labels << simulation.parameterization.getPeriodLabel(index)
                }
                periodLabels << labels
            }
        }
    }



    public ULCNumberDataType getNumberDataType() {
        if (!numberDataType) {
            numberDataType = DataTypeFactory.numberDataType
            numberDataType.setGroupingUsed true
            numberDataType.setMinFractionDigits 2
            numberDataType.setMaxFractionDigits 2
        }
        return numberDataType
    }


}
