package org.pillarone.riskanalytics.application.ui.main.view.item

import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

abstract class ItemNodeUIItem extends AbstractUIItem {

    ItemNodeUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel) {
        super(mainModel, simulationModel)
    }

    abstract VersionNumber getVersionNumber()

    abstract Class getItemClass()

    String getNameAndVersion() {
        "${name} v${versionNumber.toString()}"
    }

    abstract Object getItem()

}
