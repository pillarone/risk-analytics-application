package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tree.DefaultMutableTreeNode

class ItemNode extends DefaultMutableTreeNode {

    Object item
    boolean renameable

    def ItemNode(item, leaf = true, renameable = true) {
        super(item.name, leaf)
        this.item = item;
        this.renameable = renameable
    }

    def ItemNode(item, name, leaf, renameable) {
        super(name, leaf)
        this.item = item;
        this.renameable = renameable
    }

}
