package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleCollectorIterationNode extends DefaultMutableTableTreeNode {
    static final int PATH_NAME_INDEX = 0
    static final int SINGLE_VALUE_INDEX = 1
    static final int FIELD_NAME_INDEX = 2
    static final int ITERATION_INDEX = 3
    static final int PERIOD_INDEX = 4
    static final int VALUE_INDEX = 5
    //list of singleValueResult(pathName,value, fieldName, iteration, period, valueIndex)
    List singleValueResults = []
    int selectedNodesSize

    /**
     *
     * @param singleValueResult as array of values (pathName,value, fieldName, iteration, period, valueIndex)
     * @param selectedNodeIndex
     * @param selectedNodesSize
     * @return
     */
    public SingleCollectorIterationNode(singleValueResult, int selectedNodeIndex, int selectedNodesSize) {
        super([String.valueOf(singleValueResult[VALUE_INDEX]+1)] as Object[], true)
        this.singleValueResults[selectedNodeIndex] = [singleValueResult];
        this.selectedNodesSize = selectedNodesSize
    }

    /**
     * singleValueResults is a list of list of SingleValueResult indexed by nodeIndex and period
     *
     * @param columnIndex
     * @return
     */
    public Object getValueAtIndex(int columnIndex) {
        int periodIndex = (columnIndex - 1) / selectedNodesSize
        int nodeIndex = (columnIndex - 1) % selectedNodesSize
        def result
        if (singleValueResults[nodeIndex]) {
            for (Object obj: singleValueResults[nodeIndex]) {
                if (obj[PERIOD_INDEX] == periodIndex) {
                    return obj[SINGLE_VALUE_INDEX]
                }
            }
        }
        return ""
    }
}

