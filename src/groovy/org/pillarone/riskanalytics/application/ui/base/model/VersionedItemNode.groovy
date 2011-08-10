package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem

class VersionedItemNode extends ItemNode {

    public VersionedItemNode(AbstractUIItem abstractUIItem, leaf) {
        super(abstractUIItem, "${abstractUIItem.item.name} v${abstractUIItem.item.versionNumber.toString()}", leaf, true)
    }

    public Object getUserObject() {
        "${abstractUIItem.item.name} v${abstractUIItem.item.versionNumber.toString()}".toString()
    }

    public void setUserObject(Object userObject) {
        if (renameable) {
            abstractUIItem.item.rename(userObject)
            setValueAt("${abstractUIItem.item.name} v${abstractUIItem.item.versionNumber.toString()}".toString(), 0)
        }
    }

    public String toString() {
        getUserObject().toString()
    }
}
