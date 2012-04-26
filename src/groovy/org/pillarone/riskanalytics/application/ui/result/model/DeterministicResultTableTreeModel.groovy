package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.function.MeanFunction
import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractCommentableItemTableTreeModel
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.parameterization.ParameterApplicator

public class DeterministicResultTableTreeModel extends AbstractCommentableItemTableTreeModel {

    private Parameterization parameterization
    private SimulationRun simulationRun
    private ITableTreeNode root
    private Map nodeValuesCache = [:]
    private List<String> columnNames = []
    ULCNumberDataType numberDataType

    private ConfigObject allResults

    /**
     * Uses a ContinuousPeriodCounter to create the column names.
     * The begin of the first period can be obtained from the simulation run.
     * The period length is fix per model.
     */
    public DeterministicResultTableTreeModel(ITableTreeNode rootNode, SimulationRun simulationRun, Parameterization parameterization, ConfigObject allResults) {
        this.root = rootNode
        this.allResults = allResults
        this.simulationRun = simulationRun
        this.parameterization = parameterization
        DeterministicModel model = (DeterministicModel) parameterization.modelClass.newInstance()
        model.init()

        if (!parameterization.isLoaded()) {
            parameterization.load()
        }
        ParameterApplicator parameterApplicator = new ParameterApplicator(model: model, parameterization: parameterization)
        parameterApplicator.init()
        parameterApplicator.applyParameterForPeriod(0)

        IPeriodCounter columnLabelGenerator = model.createPeriodCounter(simulationRun.beginOfFirstPeriod)
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forStyle("S-").withLocale(UIUtils.getClientLocale())
        getColumnCount().times {it ->
            DateTime end = columnLabelGenerator.getCurrentPeriodEnd().minusDays(1)
            columnNames << end.toString(dateTimeFormatter)
            columnLabelGenerator++
        }
    }

    public int getColumnCount() {
        return simulationRun.periodCount
    }

    public Object getValueAt(Object node, int column) {
        if (column == 0) {
            return node.displayName
        }
        return valuefOfNode(node, getPeriodIndex(column))
    }

    def valuefOfNode(Object node, period) {
        return ""
    }

    def valuefOfNode(ResultTableTreeNode node, period) {
        Map periodMap = allResults[period.toString()]
        Map pathMap = periodMap[node.path]
        Map fieldMap = pathMap[node.field]

        return fieldMap[PostSimulationCalculation.MEAN]
    }

    public Object getRoot() {
        return root
    }

    public Object getChild(Object parent, int index) {
        return parent.getChildAt(index)
    }

    public int getChildCount(Object parent) {
        return parent.childCount
    }

    public boolean isLeaf(Object node) {
        return node.isLeaf
    }

    public int getIndexOfChild(Object parent, Object child) {
        for (int index = 0; index < parent.childCount; index++) {
            if (parent.getChildAt(index) == child) {
                return index
            }
        }
        return -1
    }

    public String getColumnName(int column) {
        if (column == 0) return "Name"

        return columnNames.get(column - 1)
    }

    public boolean isStochasticValue(ResultTableTreeNodenode, int i) {
        false
    }


    int getPeriodIndex(int column) {
        int periodIndex = (column - 1) % simulationRun.periodCount
        return periodIndex
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


    IFunction getFunction(int columnIndex) {
        return new MeanFunction()
    }

}