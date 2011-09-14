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
    Map singleResultsMap
    int periodCount
    int selectedNodesSize
    int iteration

    public SingleCollectorIterationRootNode(int iteration, Map singleResultsMap, int periodCount) {
        super(["iteration: " + iteration] as Object[])
        this.iteration = iteration
        this.singleResultsMap = singleResultsMap
        this.periodCount = periodCount
    }

    public Object getValueAtIndex(int index) {
        "" //TODO show aggregate
    }

}
