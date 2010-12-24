package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.result.view.SingleCollectorIterationNode
import org.pillarone.riskanalytics.application.ui.result.view.SingleCollectorIterationRootNode

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleValueTreeBuilder {

    Map singleResultsMap = [:]
    int iterations = 0
    int selectedNodesSize
    int periodCount
    DefaultMutableTableTreeNode root



    public SingleValueTreeBuilder(singleResultsMap, iterations, selectedNodesSize, int periodCount) {
        this.singleResultsMap = singleResultsMap;
        this.iterations = iterations;
        this.selectedNodesSize = selectedNodesSize
        this.periodCount = periodCount
    }

    public void build() {
        root = new DefaultMutableTableTreeNode("root")
        buildTreeNodes()
    }

    public def buildTreeNodes() {
        (1..iterations).each {int iteration ->
            root.add(createIterationNode(iteration))
        }
    }

    private def createIterationNode(int iteration) {
        SingleCollectorIterationRootNode iterationNode = createNode(iteration)
        singleResultsMap.each {k, v ->
            //-1, iteration started with 1,2,....
            for (List values: v[iteration - 1]) {
                SingleCollectorIterationNode node = new SingleCollectorIterationNode(values, k, selectedNodesSize, periodCount)
                iterationNode.add(node)
            }
        }
        return iterationNode
    }

    private ITableTreeNode createNode(int iteration) {
        new SingleCollectorIterationRootNode(iteration, singleResultsMap, periodCount)
    }

}
