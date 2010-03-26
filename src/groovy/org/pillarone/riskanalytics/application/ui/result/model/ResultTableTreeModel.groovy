package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.tabletree.ITableTreeNode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.function.NodeNameFunction
import org.pillarone.riskanalytics.application.dataaccess.function.ResultFunction
import org.pillarone.riskanalytics.application.dataaccess.function.SingleIteration
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.model.AsynchronTableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.parameterization.ParameterApplicator
import org.pillarone.riskanalytics.core.simulation.LimitedContinuousPeriodCounter

class ResultTableTreeModel extends AsynchronTableTreeModel {

    List functions = []
    Map isStochasticValueForPeriod = [:]
    private ITableTreeNode rootNode
    SimulationRun simulationRun
    int columnCount
    private Parameterization parameterization
    boolean usesDeterministicModel
    ULCNumberDataType numberDataType

    private List<String> periodLabels = []
    //A map to store all pre-calculated key figures, a ConfigObject is used to easily create nested maps
    private ConfigObject results = new ConfigObject()
    //used to create keys for the result map
    private NumberFormat numberFormat = NumberFormat.getInstance()

    protected ResultTableTreeModel(ITableTreeNode rootNode, SimulationRun simulationRun, Parameterization parameterization, IFunction mean) {
        this.rootNode = rootNode
        this.simulationRun = simulationRun
        this.parameterization = parameterization
        functions << new NodeNameFunction()
        simulationRun.periodCount.times {
            functions << mean
        }
        columnCount = 1 + simulationRun.periodCount
        //TODO: is this still used despite of DRTTM?
        usesDeterministicModel = DeterministicModel.isAssignableFrom(parameterization.modelClass)

        initPeriodLabels()
        initPostSimulationCalculations()
    }

    /**
     * Loads all PostSimulationCalculations of a simulation and stores them in a map.
     * This is faster than creating a query for every cell when the result is needed.
     */
    private void initPostSimulationCalculations() {
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
        Model model = parameterization.modelClass.newInstance()
        model.init()
        //because certain models need the parameterization to determine their period dates
        ParameterApplicator applicator = new ParameterApplicator(model: model, parameterization: parameterization)
        applicator.init()
        applicator.applyParameterForPeriod(0)
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy")
        IPeriodCounter periodCounter = model.createPeriodCounter(simulationRun.beginOfFirstPeriod)
        if (periodCounter != null) {
            periodCounter.reset()
            if (periodCounter instanceof LimitedContinuousPeriodCounter) {
                simulationRun.periodCount.times {
                    periodLabels << format.format(periodCounter.getCurrentPeriodEnd().toDate())
                    periodCounter.next()
                }
            }
            else {
                simulationRun.periodCount.times {
                    periodLabels << format.format(periodCounter.getCurrentPeriodStart().toDate())
                    periodCounter.next()
                }
            }
        } else if(!parameterization.periodLabels.empty){
            periodLabels = parameterization.getPeriodLabels()
        } else {
            simulationRun.periodCount.times { int i ->
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
        if (usesDeterministicModel) {
            return false
        }
        boolean isResultCell = column > 0 && node instanceof ResultTableTreeNode
        if (isResultCell) {
            int periodIndex = (column - 1) % simulationRun.periodCount
            ResultFunction currentFunction = functions[column]
            return !isValuePreCalculated(periodIndex, ResultFunction.getPath(node), node.field, currentFunction.keyFigureName, currentFunction.keyFigureParameter)
        } else {
            return false
        }
    }

    public def getAsynchronValue(Object node, int i) {
        int periodIndex = (i - 1) % simulationRun.periodCount
        IFunction function = functions[i]
        if (isStochasticValue(node, i)) {
            if (function instanceof ResultFunction && node instanceof ResultTableTreeNode && isValuePreCalculated(periodIndex, ResultFunction.getPath(node), node.field, function.keyFigureName, function.keyFigureParameter)) {
                return getPreCalculatedValue(periodIndex, ResultFunction.getPath(node), node.field, function.keyFigureName, function.keyFigureParameter)
            } else {
                return function.evaluate(simulationRun, periodIndex, node)
            }
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
            numberDataType = LocaleResources.numberDataType
            numberDataType.setGroupingUsed true
            numberDataType.setMinFractionDigits 2
            numberDataType.setMaxFractionDigits 2
        }
        return numberDataType
    }

}