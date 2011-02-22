package org.pillarone.riskanalytics.application.ui.base.model

class VersionedItemNode extends ItemNode {

    public VersionedItemNode(item, leaf) {
        super(item, "$item.name v${item.versionNumber.toString()}", leaf, true)
    }

    public Object getUserObject() {
        "${item.name} v${item.versionNumber.toString()}".toString()
    }

    public void setUserObject(Object userObject) {
        if (renameable) {
            item.rename(userObject)
            setValueAt("${item.name} v${item.versionNumber.toString()}".toString(), 0)
        }
    }

    public String toString() {
        getUserObject().toString()
    }
}
