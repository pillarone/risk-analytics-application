package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.ui.main.view.item.ItemNodeUIItem

class VersionedItemNode extends ItemNode {

    VersionedItemNode(ItemNodeUIItem abstractUIItem, boolean leaf) {
        super(abstractUIItem, abstractUIItem.nameAndVersion, leaf, true)
    }

    String getUserObject() {
        itemNodeUIItem.nameAndVersion
    }

    void setUserObject(String userObject) {
        if (renameable) {
            itemNodeUIItem.item.rename(userObject)
            setValueAt(itemNodeUIItem.nameAndVersion, 0)
        }
    }

    String toString() {
        userObject
    }
}
