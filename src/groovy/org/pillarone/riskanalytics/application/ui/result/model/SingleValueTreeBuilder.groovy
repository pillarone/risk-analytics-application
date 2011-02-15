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
    int fromIteration



    public SingleValueTreeBuilder(Map singleResultsMap, int fromIteration, int iterations, int selectedNodesSize, int periodCount) {
        this.singleResultsMap = singleResultsMap;
        this.fromIteration = fromIteration
        this.iterations = iterations;
        this.selectedNodesSize = selectedNodesSize
        this.periodCount = periodCount
    }

    public void build() {
        root = new DefaultMutableTableTreeNode("root")
        buildTreeNodes()
    }

    public def buildTreeNodes() {
        (fromIteration..iterations).each {int iteration ->
            root.add(createIterationNode(iteration))
        }
    }


    private def createIterationNode(int iteration) {
        SingleCollectorIterationRootNode iterationNode = createNode(iteration)
        def valueIndexNodesMap = [:]
        singleResultsMap.each {k, v ->
            //-1, iteration started with 1,2,....
            for (List values: findAll((List) v, SingleCollectorIterationNode.ITERATION_INDEX, iteration)) {
                SingleCollectorIterationNode node
                if (valueIndexNodesMap[values[SingleCollectorIterationNode.VALUE_INDEX]]) {
                    node = valueIndexNodesMap[values[SingleCollectorIterationNode.VALUE_INDEX]]
                    if (node.singleValueResults[k])
                        node.singleValueResults[k] << values
                    else
                        node.singleValueResults[k] = [values]
                } else {
                    node = new SingleCollectorIterationNode(values, k, selectedNodesSize)
                    iterationNode.add(node)
                    valueIndexNodesMap[values[SingleCollectorIterationNode.VALUE_INDEX]] = node
                }

            }
        }
        return iterationNode
    }

    private ITableTreeNode createNode(int iteration) {
        new SingleCollectorIterationRootNode(iteration, singleResultsMap, periodCount)
    }

    static List findAll(List list, int index, Object value) {
        List result = new ArrayList()
        for (Object obj: list) {
            if (obj[index] == value)
                result.add(obj)
        }
        return result
    }

}
