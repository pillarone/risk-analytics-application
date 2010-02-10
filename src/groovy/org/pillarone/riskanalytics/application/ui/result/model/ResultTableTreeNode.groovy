package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.output.batch.AbstractBulkInsert

class ResultTableTreeNode extends SimpleTableTreeNode {
    String collector = AbstractBulkInsert.DEFAULT_COLLECTOR_NAME
    Class packetClass

    public ResultTableTreeNode(String name) {
        super(name)
    }

    public ResultTableTreeNode(String name, Class packetClass) {
        super(name)
        this.packetClass = packetClass
    }

    String getPath() {
        if (parent) {
            return "${parent?.path}"
        } else {
            return name
        }
    }

    String getField() {
        return name
    }

    public String getDisplayName() {
        if (cachedDisplayName != null)
            return cachedDisplayName

        String displayName
        displayName = I18NUtils.findDisplayNameByPacket(name)
        if (displayName == null)
            displayName = super.getDisplayName()
        else
            cachedDisplayName = displayName
        return displayName
    }


}