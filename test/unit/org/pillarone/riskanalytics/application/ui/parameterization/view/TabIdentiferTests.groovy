package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.view.TabIdentifier

class TabIdentiferTests extends GroovyTestCase {


    void testEquals() {
        SimpleTableTreeNode node = new SimpleTableTreeNode("nodeName")

        def ti1 = new TabIdentifier(path: new TreePath(node), columnIndex: 1)
        def ti2 = new TabIdentifier(path: new TreePath(node), columnIndex: 1)

        assertTrue ti1.equals(ti2)
        assertTrue ti1.hashCode() == ti2.hashCode()
    }

    void testMap() {
        Map map = [:]

        SimpleTableTreeNode node = new SimpleTableTreeNode("nodeName")

        def ti1 = new TabIdentifier(path: new TreePath(node), columnIndex: 1)

        map.put(ti1, 1)

        def ti2 = new TabIdentifier(path: new TreePath(node), columnIndex: 1)

        assertEquals 1, map.get(ti2)
    }

}