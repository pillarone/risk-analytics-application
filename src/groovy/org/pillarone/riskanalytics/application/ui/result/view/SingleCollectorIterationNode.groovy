package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleCollectorIterationNode extends DefaultMutableTableTreeNode {
    // array contains single value result: pathName,value, fieldName, iteration, period
    Object[] singleValueResult
    int selectedNodesSize
    //selected node index
    int selectedNodeIndex
    int periodCount

    public SingleCollectorIterationNode(singleValueResult, int selectedNodeIndex, int selectedNodesSize, int periodCount) {
        super([singleValueResult[2] + ":" + singleValueResult[4]] as Object[], true)
        this.singleValueResult = singleValueResult;
        this.selectedNodesSize = selectedNodesSize
        this.periodCount = periodCount
        this.selectedNodeIndex = selectedNodeIndex
    }

    public Object getValueAtIndex(int index) {
        int periodIndex = (index - 1) / selectedNodesSize
        int nodeIndex = (index - 1) % selectedNodesSize
        if (periodIndex == singleValueResult[4] && selectedNodeIndex == nodeIndex) {
            return singleValueResult[1]
        }
        return ""
    }
}

