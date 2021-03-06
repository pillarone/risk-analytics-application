package org.pillarone.riskanalytics.application.ui.base

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
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
        ULCFixedColumnTableTree tree = new ULCFixedColumnTableTree(model, 2, [100, 100, 100, 100, 100, 100] as int[])
        tree.setCellSelectionEnabled true
        HtmlToolTipTreeNodeRenderer renderer = new HtmlToolTipTreeNodeRenderer()

        tree.rowHeaderTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(renderer)
        }
        return tree
    }

    SimpleTableTreeNode createRootNode() {
        SimpleTableTreeNode rootNode = new SimpleTableTreeNode("root")
        List childdren = []
        (1..10).each {
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
//        Thread.sleep 10000
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

class HtmlToolTipTreeNodeRenderer extends DefaultTableTreeCellRenderer {

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        setToolTipText "<html><b style='color:red'>bold ${node.name}</b> test</html>"
        return component
    }

}
