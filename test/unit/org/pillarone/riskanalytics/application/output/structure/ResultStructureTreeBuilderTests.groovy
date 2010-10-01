package org.pillarone.riskanalytics.application.output.structure

import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources

class ResultStructureTreeBuilderTests extends GroovyTestCase {

    protected void setUp() {
        super.setUp();
        LocaleResources.setTestMode()
    }

    protected void tearDown() {
        super.tearDown();
        LocaleResources.clearTestMode()
    }



    void testPaths() {
        ResultStructure resultStructure = new ResultStructure("name")
        resultStructure.mappings.put("A:[%dyn1%]:B:C", "node1:node2:[%dyn1%]:out1:field")
        resultStructure.mappings.put("X:Y:Z", "node1:node2:out1:field")
        resultStructure.mappings.put("1:[%dyn2%]:2:3", "node1:node3:[%dyn2%]:out2:field")
        resultStructure.mappings.put("A:[%dyn3%]:B:[%dyn4%]:C", "node1:node4:[%dyn3%]:node5:[%dyn4%]:out3:field")

        List allPaths = [
                "node1:node2:nodeA:out1:field",
                "node1:node2:nodeB:out1:field",
                "node1:node2:nodeC:XYZ:out1:field", //should NOT be matched by node1:node2:[%dyn1%]:out1:field
                "node1:node2:out1:field",
                "node1:node3:nodeC:out2:field",
                "node1:node3:nodeD:out2:field",
                "node1:node4:nodeX:node5:nodeY:out3:field",
                "node1:node4:nodeX:node5:nodeZ:out3:field",
                "node1:node4:nodeW:node5:nodeY:out3:field",
                "node1:node4:nodeW:node5:nodeZ:out3:field",
        ]
        ResultStructureTreeBuilder treeBuilder = new ResultStructureTreeBuilder(allPaths, null, resultStructure, null)
        Map transformedPaths = treeBuilder.transformedPaths

        assertEquals allPaths.size() - 1, transformedPaths.size()

        assertEquals "node1:node2:out1:field", transformedPaths.get("X:Y:Z")
        assertEquals "node1:node2:nodeA:out1:field", transformedPaths.get("A:nodeA:B:C")
        assertEquals "node1:node2:nodeB:out1:field", transformedPaths.get("A:nodeB:B:C")
        assertEquals "node1:node3:nodeC:out2:field", transformedPaths.get("1:nodeC:2:3")
        assertEquals "node1:node3:nodeD:out2:field", transformedPaths.get("1:nodeD:2:3")
        assertEquals "node1:node4:nodeX:node5:nodeY:out3:field", transformedPaths.get("A:nodeX:B:nodeY:C")
        assertEquals "node1:node4:nodeX:node5:nodeZ:out3:field", transformedPaths.get("A:nodeX:B:nodeZ:C")
        assertEquals "node1:node4:nodeW:node5:nodeY:out3:field", transformedPaths.get("A:nodeW:B:nodeY:C")
        assertEquals "node1:node4:nodeW:node5:nodeZ:out3:field", transformedPaths.get("A:nodeW:B:nodeZ:C")
    }

    void testBuildTree() {
        ResultStructure resultStructure = new ResultStructure("name")
        resultStructure.mappings.put("model:A:[%dyn1%]:B:C", "node1:node2:[%dyn1%]:out1:field")
        resultStructure.mappings.put("model:X:Y:Z", "node1:node2:out1:field")
        resultStructure.mappings.put("model:1:[%dyn2%]:2:3", "node1:node3:[%dyn2%]:out2:field")

        List allPaths = [
                "node1:node2:nodeA:out1:field",
                "node1:node2:nodeB:out1:field",
                "node1:node2:out1:field",
                "node1:node3:nodeC:out2:field",
                "node1:node3:nodeD:out2:field",
        ]
        ResultStructureTreeBuilder treeBuilder = new ResultStructureTreeBuilder(allPaths, null, resultStructure, null)

        SimpleTableTreeNode model = treeBuilder.buildTree()
        assertEquals 3, model.childCount

        SimpleTableTreeNode x = model.getChildByName("X")
        assertEquals 1, x.childCount

        SimpleTableTreeNode y = x.getChildByName("Y")
        assertEquals 1, y.childCount

        SimpleTableTreeNode z = y.getChildByName("Z")
        assertEquals 0, z.childCount
        assertTrue z instanceof ResultTableTreeNode

        SimpleTableTreeNode a = model.getChildByName("A")
        assertEquals 2, a.childCount

        SimpleTableTreeNode nodeA = a.getChildByName("nodeA")
        assertEquals 1, nodeA.childCount

        y = nodeA.getChildByName("B")
        assertEquals 1, y.childCount

        z = y.getChildByName("C")
        assertEquals 0, z.childCount
        assertTrue z instanceof ResultTableTreeNode
        assertEquals "node1:node2:nodeA:out1", z.path
        assertEquals "field", z.field
        assertEquals "model:A:nodeA:B:C", z.actualTreePath

    }
}
