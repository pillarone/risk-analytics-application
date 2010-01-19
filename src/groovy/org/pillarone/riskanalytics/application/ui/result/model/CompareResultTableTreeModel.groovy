package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.datatype.ULCNumberDataType
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.util.SimulationUtilities
import org.pillarone.riskanalytics.application.dataaccess.function.CompareFunction
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.function.NodeNameFunction
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.model.AsynchronTableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class CompareResultTableTreeModel extends AsynchronTableTreeModel {

    private List<SimulationRun> simulationRuns
    List<Simulation> simulations
    private List<SimulationRun> hiddenSimulations = []
    private List<IFunction> functions = []
    private List<CompareFunction> compareFunctions = []
    private int minPeriodCount
    private NodeNameFunction nameFunction = new NodeNameFunction()
    private SimpleTableTreeNode root
    double minValue = 0
    double maxValue = 0
    ULCNumberDataType numberDataType

    boolean orderByKeyfigure

    public CompareResultTableTreeModel(SimpleTableTreeNode rootNode, List<Simulation> simulations, IFunction mean) {
        super();
        this.root = rootNode
        if (simulations.size() < 2) {
            throw new IllegalArgumentException("At least 2 simulationRuns required.")
        }
        this.simulationRuns= simulations*.simulationRun
        this.simulations= simulations
        minPeriodCount = SimulationUtilities.getMinSimulationsPeriod(simulationRuns)
        orderByKeyfigure = true

        if (mean) {
            addFunction(mean)
        }
    }

    public Object getAsynchronValue(Object node, int column) {
        int simulationIndex = getSimulationRunIndex(column)
        SimulationRun run = simulationIndex >= 0 ? simulationRuns.get(simulationIndex) : null

        IFunction function = getFunction(column).clone()
        return function.evaluate(run, getPeriodIndex(column), node)
    }

    public String getColumnName(int column) {
        int runIndex = getSimulationRunIndex(column);
        if (runIndex >= 0) {
            return "${SimulationUtilities.RESULT_CHAR_PREFIXES[runIndex]} : P${getPeriodIndex(column)} : ${getFunction(column).name}"
        } else {
            runIndex = simulationRuns.indexOf(getFunction(column).runB)
            return "${SimulationUtilities.RESULT_CHAR_PREFIXES[runIndex]} :P${getPeriodIndex(column)} : ${getFunction(column).name}: ${getFunction(column).underlyingFunction.name}"
        }
    }

    protected boolean loadAsynchronous(int column, Object node) {
        column > 0 && node instanceof ResultTableTreeNode
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
        if (function instanceof CompareFunction) {
            function.underlyingFunction = functions.get(functionIndex)
        }
        return function
    }

    private IFunction getCompareFunction(int blockPosition) {
        blockPosition = blockPosition - simulationRuns.size()
        int compareFunctionIndex = blockPosition / (simulationRuns.size() - 1)
        int simulationIndex = blockPosition % (simulationRuns.size() - 1) + 1
        IFunction function = compareFunctions.get(compareFunctionIndex)
        function.runB = simulationRuns.get(simulationIndex)
        return function
    }

    public void addFunction(IFunction function) {
        functions << function
    }

    public void addCompareFunction(CompareFunction function) {
        function.runA = simulationRuns.get(0)
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
            if (function instanceof CompareFunction) {
                return hiddenSimulations.contains(function.runB)
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
        for (CompareFunction cf in compareFunctions) {
            cf.runA = simulationRuns.get(0)
            if (cf.runA == cf.runB) {
                cf.runB = simulationRuns.get(1)
            }
        }
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

    public ULCNumberDataType getNumberDataType() {
        if (!numberDataType) {
            numberDataType = LocaleResources.numberDataType
            numberDataType.setGroupingUsed true
            numberDataType.setMinFractionDigits 2
            numberDataType.setMaxFractionDigits 2
        }
        return numberDataType
    }


}
