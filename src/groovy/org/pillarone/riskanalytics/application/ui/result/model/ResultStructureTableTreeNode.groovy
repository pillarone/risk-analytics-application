package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.I18NUtils

class ResultStructureTableTreeNode extends SimpleTableTreeNode {

    private Class modelClass

    public ResultStructureTableTreeNode(String name, Class modelClass) {
        super(name);
        this.modelClass = modelClass
    }

    String getDisplayName() {
        if (cachedDisplayName == null) {
            cachedDisplayName = I18NUtils.getResultStructureString(modelClass, name)
        }
        return cachedDisplayName
    }


}
