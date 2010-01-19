package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tree.DefaultMutableTreeNode

class ItemGroupNode extends DefaultMutableTreeNode {
    Class itemClass

    public ItemGroupNode(String name, Class itemClass) {
        this.itemClass = itemClass
        userObject = name
    }
}
