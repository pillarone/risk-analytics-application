package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import java.text.NumberFormat
import org.pillarone.riskanalytics.application.util.LocaleResources

class TreeNodeCopierTests extends GroovyTestCase {

    void testWriteIncludingChildren() {
        LocaleResources.setTestMode()
        ITableTreeNode rootNode = new DefaultMutableTableTreeNode(["a", "b", "c"] as Object[])
        rootNode.add(new DefaultMutableTableTreeNode(["a1", "b1", "c1"] as Object[]))

        ITableTreeModel model = new DefaultTableTreeModel(rootNode, ['a', 'b', 'c'] as String[])
        TestTreeNodeCopier copier = new TestTreeNodeCopier(model: model, columnOrder: [0, 1, 2])
        String result = copier.writeNode(rootNode, 3)
        println result
        assertTrue result.indexOf("\tb\tc\n") != -1
        assertTrue result.indexOf("\ta1\tb1\tc1\n") != -1
        assertTrue result.indexOf("[[a, b, c]]") != -1
        assertTrue result.indexOf("[[a, b, c], [a1, b1, c1]]") != -1
        LocaleResources.clearTestMode()
    }

    void testWriteLeaf() {
        LocaleResources.setTestMode()
        ITableTreeNode rootNode = new DefaultMutableTableTreeNode(["a", "b", "c"] as Object[])
        ITableTreeModel model = new DefaultTableTreeModel(rootNode, ['a', 'b', 'c'] as String[])

        String result = new TestTreeNodeCopier(model: model, columnOrder: [0, 1, 2]).writeNode(rootNode, 3)
        assertTrue result.indexOf("[[a, b, c]]") != -1
        assertTrue result.indexOf("\ta\tb\tc\n") != -1
        LocaleResources.clearTestMode()
    }

    void testWriteNullValues() {
        LocaleResources.setTestMode()
        ITableTreeNode rootNode = new DefaultMutableTableTreeNode([1.1d, 0.0d, null] as Object[])
        ITableTreeModel model = new DefaultTableTreeModel(rootNode, ['a', 'b', 'c'] as String[])

        String result = new TestTreeNodeCopier(model: model, columnOrder: [0, 1, 2]).writeNode(rootNode, 3)
        assertTrue result.indexOf("[[1.1, 0.0, null]]") != -1
        assertTrue result.indexOf("1.1\t0.0\t\n") != -1
        LocaleResources.clearTestMode()
    }

    void testColumnOrder() {
        LocaleResources.setTestMode()
        ITableTreeNode rootNode = new DefaultMutableTableTreeNode(["a", "b", "c"] as Object[])
        ITableTreeModel model = new DefaultTableTreeModel(rootNode, ['a', 'b', 'c'] as String[])

        String result = new TreeNodeCopier(model: model, columnOrder: [0, 2, 1]).writeNode(rootNode, 3)
        assertTrue result.indexOf("[[a, b, c]]") != -1
        assertTrue result.indexOf("a\tc\tb\n") != -1
        LocaleResources.clearTestMode()
    }
}

class TestTreeNodeCopier extends TreeNodeCopier {
    protected getCopyFormat() {
        NumberFormat format = NumberFormat.getInstance(new Locale("en", "US"))
        format.groupingUsed = false
        format.minimumFractionDigits = 1
        return format
    }
}