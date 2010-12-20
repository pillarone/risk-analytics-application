package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.result.view.SingleCollectorIterationNode
import org.pillarone.riskanalytics.core.output.SingleValueResult

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleValueTreeBuilder {

    List<SingleValueResult> singleResults = []
    int iterations = 0
    DefaultMutableTableTreeNode root



    public SingleValueTreeBuilder(singleResults, iterations) {
        this.singleResults = singleResults;
        this.iterations = iterations;
    }

    public void build() {
        root = new DefaultMutableTableTreeNode("root")
        buildTreeNodes()
    }

    public def buildTreeNodes() {
        (0..iterations).each {int iteration ->
            root.add(createIterationNode(iteration))
        }
    }

    private def createIterationNode(int iteration) {
        DefaultMutableTableTreeNode iterationNode = createNode(String.valueOf(iteration))

        def secondLevelNodes = singleResults.findAll { it[3] == iteration}
        secondLevelNodes.each {
            SingleCollectorIterationNode node = new SingleCollectorIterationNode(it)
            iterationNode.add(node)
        }
        iterationNode
    }

    private ITableTreeNode createNode(String name) {
        new DefaultMutableTableTreeNode(name)
    }

}
