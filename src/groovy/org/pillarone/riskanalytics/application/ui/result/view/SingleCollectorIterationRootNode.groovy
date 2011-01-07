package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.SingleValueTreeBuilder

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
/**
 * IterationNode root of singleValueResult
 */
class SingleCollectorIterationRootNode extends DefaultMutableTableTreeNode {
    Object[][] nodeAmounts
    Map singleResultsMap
    int periodCount
    int selectedNodesSize
    int iteration

    public SingleCollectorIterationRootNode(int iteration, Map singleResultsMap, int periodCount) {
        super(["iteration: " + iteration] as Object[])
        this.iteration = iteration
        this.singleResultsMap = singleResultsMap
        this.periodCount = periodCount
        initNodeAmounts()
    }

    /**
     * aggregate the value of nodes by selected nodes and period
     */
    private void initNodeAmounts() {
        selectedNodesSize = singleResultsMap.keySet().size()
        nodeAmounts = new Object[singleResultsMap.keySet().size()][periodCount]

        singleResultsMap.each {selectedNodeIndex, v ->
            //-1, iteration started with 1,2,....
            for (List values: SingleValueTreeBuilder.findAll((List) v, SingleCollectorIterationNode.ITERATION_INDEX, iteration)) {
                for (int period = 0; period < periodCount; period++) {
                    if (values[SingleCollectorIterationNode.PERIOD_INDEX] == period) {
                        if (!nodeAmounts[selectedNodeIndex][period])
                            nodeAmounts[selectedNodeIndex][period] = values[SingleCollectorIterationNode.SINGLE_VALUE_INDEX]
                        else
                            nodeAmounts[selectedNodeIndex][period] += values[SingleCollectorIterationNode.SINGLE_VALUE_INDEX]
                    }
                }
            }
        }
    }

    public Object getValueAtIndex(int index) {
        int periodIndex = (index - 1) / selectedNodesSize
        int nodeIndex = (index - 1) % selectedNodesSize
        return nodeAmounts[nodeIndex][periodIndex]
    }

}
