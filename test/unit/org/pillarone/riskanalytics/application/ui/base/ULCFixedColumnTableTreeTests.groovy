package org.pillarone.riskanalytics.application.ui.base

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ULCFixedColumnTableTreeTests extends AbstractP1RATTestCase {
    ULCFixedColumnTableTree tree

    ULCComponent createContentPane() {
        SimpleTableTreeNode rootNode = createRootNode()
        DefaultTableTreeModel model = new DefaultTableTreeModel(rootNode, ["one", "two", "three", "four", "five", "six"] as String[])
        ULCFixedColumnTableTree tree = new ULCFixedColumnTableTree(model, 1, [500, 500, 500, 500, 500, 500] as int[])
        tree.setCellSelectionEnabled true
        return tree
    }

    SimpleTableTreeNode createRootNode() {
        SimpleTableTreeNode rootNode = new SimpleTableTreeNode("root")
        List childdren = []
        (1..100).each {
            TestTableTreeNode child = new TestTableTreeNode("child" + it, "child1Value At " + it)
            childdren << child
        }

        childdren.each {
            createChild it, 10
            rootNode.add(it)

        }
        return rootNode
    }

    public void testSleep() {
//        Thread.sleep 1000000
    }

    private void createChild(SimpleTableTreeNode child, int depth) {
        if (depth == 0) {
            return
        }
        TestTableTreeNode newChild = new TestTableTreeNode("child in depth" + depth, "" + depth)
        child.add(newChild)
        depth--
        createChild(newChild, depth)
    }

    class TestTableTreeNode extends SimpleTableTreeNode {

        String cellValues

        def TestTableTreeNode(name, String cellValues) {
            super(name);
            this.cellValues = cellValues
        }

        protected String getCellValue(int column) {
            return cellValues
        }
    }

}
