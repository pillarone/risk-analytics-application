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
    List<String> columnNames = ["Iteration", "Result"]
    ITableTreeNode root
    List<ResultTableTreeNode> nodes

    SingleValueTreeBuilder builder
    List singleValueResults = []
    int iterations = 10
    SimulationRun simulationRun

    public SingleValueCollectorTableTreeModel(List nodes, SimulationRun simulationRun) {
        this.nodes = nodes.sort {SimpleTableTreeNode node -> node.path }
        this.simulationRun = simulationRun
    }

    public void init() {
        nodes.each { ResultTableTreeNode resultTableTreeNode ->
            singleValueResults.addAll(ResultAccessor.getSingleValueResults(resultTableTreeNode.collector, resultTableTreeNode.path, resultTableTreeNode.field, simulationRun))
        }
        builder = new SingleValueTreeBuilder(singleValueResults, iterations)
        builder.build()
        this.root = builder.root
    }

    int getColumnCount() {
        return columnNames.size();
    }

    public String getColumnName(int i) {
        return columnNames[i]
    }

    Object getValueAt(Object node, int i) {
        if (i == 0) {
            return "${node.getValueAt(0)}".toString()
        } else if (node instanceof SingleCollectorIterationNode) {
            return node.values[i]
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


}
