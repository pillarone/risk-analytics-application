package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import java.text.DateFormat
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.SimulationRun

import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.dataaccess.DeterminsiticResultAccessor
import org.pillarone.riskanalytics.core.simulation.ContinuousPeriodCounter
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter


public class DeterministicResultTableTreeModel extends AbstractTableTreeModel {

    private Parameterization parameterization
    private SimulationRun simulationRun
    private ITableTreeNode root
    private Map nodeValuesCache = [:]
    private List<String> columnNames = []
    ULCNumberDataType numberDataType

    /**
     * Uses a ContinuousPeriodCounter to create the column names.
     * The begin of the first period can be obtained from the simulation run.
     * The period length is fix per model.
     */
    public DeterministicResultTableTreeModel(ITableTreeNode rootNode, SimulationRun simulationRun, Parameterization parameterization) {
        this.root = rootNode
        this.simulationRun = simulationRun
        this.parameterization = parameterization
        DeterministicModel model = (DeterministicModel) parameterization.modelClass.newInstance()
        IPeriodCounter columnLabelGenerator = new ContinuousPeriodCounter(simulationRun.beginOfFirstPeriod, model.periodLength)
        def dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, UIUtils.getClientLocale())
        getColumnCount().times {it ->
            GregorianCalendar end = columnLabelGenerator.getCurrentPeriodEnd().toGregorianCalendar()
            columnNames << dateFormat.format(end.time)
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
        return getNodeValues(node)[period]
    }

    List getNodeValues(node) {
        List values = nodeValuesCache[node]
        if (values == null) {
            values = DeterminsiticResultAccessor.getAllPeriodValuesFromView(simulationRun, node.field, node.collector, node.path)
            nodeValuesCache[node] = values
        }
        return values
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
        if(column == 0) return "Name"

        return columnNames.get(column - 1)
    }

    public boolean isStochasticValue(ResultTableTreeNodenode, int i) {
        false
    }


    private int getPeriodIndex(int column) {
        int periodIndex = (column - 1) % simulationRun.periodCount
        return periodIndex
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