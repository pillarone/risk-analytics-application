package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode

class ItemGroupNode extends DefaultMutableTableTreeNode {
    Class itemClass

    public ItemGroupNode(String name, Class itemClass) {
        super([name] as Object[])
        this.itemClass = itemClass
    }
}
