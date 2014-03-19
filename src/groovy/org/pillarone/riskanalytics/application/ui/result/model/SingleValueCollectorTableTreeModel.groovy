package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.AbstractPresentationModel
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.view.SingleCollectorIterationNode
import org.pillarone.riskanalytics.application.ui.result.view.SingleCollectorIterationRootNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleValueCollectorTableTreeModel extends AbstractTableTreeModel {

    static Log LOG = LogFactory.getLog(SingleValueCollectorTableTreeModel)
    List<String> columnNames = ["Iteration"]
    ITableTreeNode root
    List<ResultTableTreeNode> nodes

    SingleValueTreeBuilder builder
    Map singleValueResultsMap = [:]
    int iterations = 100
    SimulationRun simulationRun
    def periodCount
    List periodLabels = []
    int fromIteration = 1

    public SingleValueCollectorTableTreeModel(List nodes, SimulationRun simulationRun, boolean showPeriodLabel) {
        this.nodes = nodes?.sort { SimpleTableTreeNode node -> node.path }
        this.simulationRun = simulationRun
        this.periodCount = simulationRun?.periodCount
        singleValueResultsMap = [:]
        periodLabels = AbstractPresentationModel.loadPeriodLabels(simulationRun, showPeriodLabel)
    }

    public void init() {
        setIterations()
        nodes.eachWithIndex { ResultTableTreeNode resultTableTreeNode, int nodeIndex ->
            if (!singleValueResultsMap[nodeIndex])
                singleValueResultsMap[nodeIndex] = ResultAccessor.getSingleValueResults(SingleValueCollectingModeStrategy.IDENTIFIER, resultTableTreeNode.path, resultTableTreeNode.field, simulationRun)
        }
        builder = new SingleValueTreeBuilder(singleValueResultsMap, fromIteration, iterations, nodes.size(), simulationRun.periodCount)
        builder.build()
        this.root = builder.root
    }

    public void apply(int fromIndex, int toIndex) {
        fromIteration = fromIndex
        iterations = toIndex
        init()
        structureChanged()
    }

    int getColumnCount() {
        return 1 + periodCount * nodes.size();
    }

    public String getColumnName(int i) {
        int periodIndex = (i - 1) / nodes.size()
        int nodeIndex = (i - 1) % nodes.size()
        return i == 0 ? columnNames[0] : getShortPath(nodes[nodeIndex]) + " " + periodLabels[periodIndex]
    }

    public String getShortPath(ResultTableTreeNode node) {
        return node.getShortDisplayPath(nodes)
    }

    Object getValueAt(Object node, int i) {
        if (i == 0) {
            return "${node.getValueAt(0)}".toString()
        } else if ((node instanceof SingleCollectorIterationNode) || (node instanceof SingleCollectorIterationRootNode)) {
            return node.getValueAtIndex(i)
        }
        return ""
    }

    Object getRoot() {
        return builder.root
    }

    public Object getChild(Object parent, int index) {
        return parent.getChildAt(index)
    }

    public int getChildCount(Object node) {
        return node.childCount
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0
    }

    public int getIndexOfChild(Object parent, Object child) {
        return parent.getIndex(child)
    }

    public void setIterations() {
        this.iterations = Math.min(iterations, maxIteration)
    }

    int getMaxIteration() {
        return simulationRun.iterations
    }
}
