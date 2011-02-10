package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode

class ItemNode extends DefaultMutableTableTreeNode {

    Object item
    boolean renameable
    Map values = [:]

    def ItemNode(item, leaf = true, renameable = true) {
        super([item.name] as Object[])
        this.item = item;
        this.renameable = renameable
    }

    def ItemNode(item, name, leaf, renameable) {
        super([name] as Object[])
        this.item = item;
        this.renameable = renameable
    }

}
