package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.result.view.SingleCollectorIterationNode

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
        DefaultMutableTableTreeNode iterationNode = createNode(String.valueOf(iteration))
        singleResultsMap.each {k, v ->
            //-1, iteration started with 1,2,....
            v[iteration - 1].each { def values ->
                SingleCollectorIterationNode node = new SingleCollectorIterationNode(values, k, selectedNodesSize, periodCount)
                iterationNode.add(node)
            }
        }
        iterationNode
    }

    private ITableTreeNode createNode(String name) {
        new DefaultMutableTableTreeNode(name)
    }

}
