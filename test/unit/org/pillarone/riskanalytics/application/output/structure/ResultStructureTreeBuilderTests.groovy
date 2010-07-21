package org.pillarone.riskanalytics.application.output.structure

import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode


class ResultStructureTreeBuilderTests extends GroovyTestCase {

    void testPaths() {
        ResultStructure resultStructure = new ResultStructure("name")
        resultStructure.mappings.put("node1:node2:[%dyn1%]:out1:field", "A:[%dyn1%]:B:C")
        resultStructure.mappings.put("node1:node2:out1:field", "X:Y:Z")
        resultStructure.mappings.put("node1:node3:[%dyn2%]:out2:field", "1:[%dyn2%]:2:3")

        List allPaths = [
                "node1:node2:nodeA:out1:field",
                "node1:node2:nodeB:out1:field",
                "node1:node2:out1:field",
                "node1:node3:nodeC:out2:field",
                "node1:node3:nodeD:out2:field",
        ]
        ResultStructureTreeBuilder treeBuilder = new ResultStructureTreeBuilder(allPaths, null, resultStructure, null)
        Map transformedPaths = treeBuilder.transformedPaths

        assertEquals allPaths.size(), transformedPaths.size()

        assertEquals "X:Y:Z", transformedPaths.get("node1:node2:out1:field")
        assertEquals "A:nodeA:B:C", transformedPaths.get("node1:node2:nodeA:out1:field")
        assertEquals "A:nodeB:B:C", transformedPaths.get("node1:node2:nodeB:out1:field")
        assertEquals "1:nodeC:2:3", transformedPaths.get("node1:node3:nodeC:out2:field")
        assertEquals "1:nodeD:2:3", transformedPaths.get("node1:node3:nodeD:out2:field")
    }

    void testBuildTree() {
        ResultStructure resultStructure = new ResultStructure("name")
        resultStructure.mappings.put("node1:node2:[%dyn1%]:out1:field", "A:[%dyn1%]:B:C")
        resultStructure.mappings.put("node1:node2:out1:field", "X:Y:Z")
        resultStructure.mappings.put("node1:node3:[%dyn2%]:out2:field", "1:[%dyn2%]:2:3")

        List allPaths = [
                "node1:node2:nodeA:out1:field",
                "node1:node2:nodeB:out1:field",
                "node1:node2:out1:field",
                "node1:node3:nodeC:out2:field",
                "node1:node3:nodeD:out2:field",
        ]
        ResultStructureTreeBuilder treeBuilder = new ResultStructureTreeBuilder(allPaths, null, resultStructure, null)

        SimpleTableTreeNode root = treeBuilder.buildTree()
        assertEquals 3, root.childCount

        SimpleTableTreeNode x = root.getChildByName("X")
        assertEquals 1, x.childCount

        SimpleTableTreeNode y = x.getChildByName("Y")
        assertEquals 1, y.childCount

        SimpleTableTreeNode z = y.getChildByName("Z")
        assertEquals 0, z.childCount
        assertTrue z instanceof ResultTableTreeNode

        SimpleTableTreeNode a = root.getChildByName("A")
        assertEquals 2, a.childCount

        SimpleTableTreeNode nodeA = a.getChildByName("nodeA")
        assertEquals 1, nodeA.childCount

        y = nodeA.getChildByName("B")
        assertEquals 1, y.childCount

        z = y.getChildByName("C")
        assertEquals 0, z.childCount
        assertTrue z instanceof ResultTableTreeNode

    }
}
