package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleCollectorIterationRootNode extends DefaultMutableTableTreeNode {
    Object[][] nodeAmounts
    Map singleResultsMap
    int periodCount
    int selectedNodesSize
    int iteration

    public SingleCollectorIterationRootNode(int iteration, Map singleResultsMap, int periodCount) {
        super([String.valueOf(iteration)] as Object[])
        this.iteration = iteration
        this.singleResultsMap = singleResultsMap
        this.periodCount = periodCount
        initNodeAmounts()
    }

    private void initNodeAmounts() {
        selectedNodesSize = singleResultsMap.keySet().size()
        nodeAmounts = new Object[singleResultsMap.keySet().size()][periodCount]

        singleResultsMap.each {selectedNodeIndex, v ->
            //-1, iteration started with 1,2,....
            for (List values: v[iteration - 1]) {
                for (int period = 0; period < periodCount; period++) {
                    if (values[4] == period) {
                        if (!nodeAmounts[selectedNodeIndex][period])
                            nodeAmounts[selectedNodeIndex][period] = values[1]
                        else
                            nodeAmounts[selectedNodeIndex][period] += values[1]
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
