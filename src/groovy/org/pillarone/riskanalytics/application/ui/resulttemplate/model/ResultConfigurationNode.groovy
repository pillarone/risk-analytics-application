package org.pillarone.riskanalytics.application.ui.resulttemplate.model

import org.pillarone.riskanalytics.application.ui.base.model.VersionedItemNode
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

class ResultConfigurationNode extends VersionedItemNode {

    public ResultConfigurationNode(ResultConfiguration resultConfiguration) {
        super(resultConfiguration, false)
    }
}
