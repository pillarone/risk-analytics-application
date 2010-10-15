package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.I18NUtils

import org.pillarone.riskanalytics.core.output.AggregatedCollectingModeStrategy

class ResultTableTreeNode extends SimpleTableTreeNode {
    String collector = AggregatedCollectingModeStrategy.IDENTIFIER
    Class modelClass

    private String path, field

    public ResultTableTreeNode(String name) {
        super(name)
    }

    public ResultTableTreeNode(String name, Class modelClass) {
        super(name)
        this.modelClass = modelClass
    }

    void setResultPath(String resultPath) {
        path = resultPath.substring(0, resultPath.lastIndexOf(":"))
        field = resultPath?.substring(resultPath.lastIndexOf(":") + 1)
    }

    String getActualTreePath() {
        if (parent) {
            return "${parent?.path}:$name"
        } else {
            return name
        }
    }

    String getPath() {
        return path
    }

    String getField() {
        if (field) {
            return field
        }
        else {
            return name
        }
    }

    public String getDisplayName() {
        if (cachedDisplayName == null) {
            cachedDisplayName = I18NUtils.getResultStructureString(modelClass, name)
        }
        return cachedDisplayName
    }


}