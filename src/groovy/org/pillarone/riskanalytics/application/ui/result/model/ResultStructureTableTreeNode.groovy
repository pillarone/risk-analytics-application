package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.I18NUtilities

class ResultStructureTableTreeNode extends SimpleTableTreeNode {

    private Class modelClass

    public ResultStructureTableTreeNode(String name, Class modelClass) {
        super(name);
        this.modelClass = modelClass
    }

    String getDisplayName() {
        if (cachedDisplayName == null) {
            cachedDisplayName = lookUp(null, "")
        }
        return cachedDisplayName
    }

    public String getToolTip() {
        if (!cachedToolTip) {
            String value = name
            cachedToolTip = lookUp(value, TOOLTIP)
            if (!cachedToolTip)
                cachedToolTip = super.getToolTip()
        }
        return cachedToolTip
    }


    private String lookUp(String value, String tooltip) {
        return I18NUtilities.getResultStructureString(modelClass, name, tooltip)
    }


}
