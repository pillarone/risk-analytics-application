package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem

class ModellingItemNode extends ItemNode {

    ModellingItemNode(ModellingUIItem modellingUIItem, boolean leaf) {
        super(modellingUIItem, modellingUIItem.nameAndVersion, leaf)
    }

    @Override
    ModellingUIItem getItemNodeUIItem() {
        return super.itemNodeUIItem as ModellingUIItem
    }

    String getUserObject() {
        itemNodeUIItem.nameAndVersion
    }

    void setUserObject(String userObject) {
        itemNodeUIItem.item.rename(userObject)
        setValueAt(itemNodeUIItem.nameAndVersion, 0)
    }

    String toString() {
        userObject
    }
}
