package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.view.SingleCollectorIterationNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun

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
    int iterations = 10
    SimulationRun simulationRun
    def periodCount

    public SingleValueCollectorTableTreeModel(List nodes, SimulationRun simulationRun) {
        this.nodes = nodes?.sort {SimpleTableTreeNode node -> node.path }
        this.simulationRun = simulationRun
        this.periodCount = simulationRun?.periodCount
        singleValueResultsMap = [:]
    }

    public void init() {
        nodes.eachWithIndex { ResultTableTreeNode resultTableTreeNode, int nodeIndex ->
            singleValueResultsMap[nodeIndex] = getList(ResultAccessor.getSingleValueResults(resultTableTreeNode.collector, resultTableTreeNode.path, resultTableTreeNode.field, simulationRun))
        }
        builder = new SingleValueTreeBuilder(singleValueResultsMap, iterations, nodes.size(), simulationRun.periodCount)
        builder.build()
        this.root = builder.root
    }

    int getColumnCount() {
        return 1 + periodCount * nodes.size();
    }

    public String getColumnName(int i) {
        int periodIndex = (i - 1) / nodes.size()
        int nodeIndex = (i - 1) % nodes.size()
        return i == 0 ? columnNames[0] : nodes[nodeIndex].getDisplayName() + " N${nodeIndex} P${periodIndex}"
    }

    Object getValueAt(Object node, int i) {
        if (i == 0) {
            return "${node.getValueAt(0)}".toString()
        } else if (node instanceof SingleCollectorIterationNode) {
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

    List getList(List list) {
        def result = []

        def iterationList = []
        (1..iterations).each { int iteration ->
            iterationList = []
            (0..periodCount).each { int period ->
                iterationList.addAll(list.findAll {it[3] == iteration}.findAll {it[4] == period})
            }
            result << iterationList
        }
        return result

    }


}
