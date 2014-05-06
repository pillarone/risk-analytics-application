package org.pillarone.riskanalytics.application.ui.main.view.item

import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

abstract class ModellingUiItemWithModel extends ModellingUIItem {

    ModellingUiItemWithModel(ModellingItem item) {
        super(item)
    }

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        return null
    }

    @Override
    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        return null
    }

    @Override
    boolean deleteDependingResults() {
        return UIItemUtils.deleteDependingResults(model, this)
    }

    protected Model createModel() {
        Model model = item.modelClass.newInstance() as Model
        model.init()
        model
    }

    ModellingUIItem createNewVersion(boolean openNewVersion = true) {
        ModellingItem modellingItem = null
        item.daoClass.withTransaction { status ->
            if (!item.loaded) {
                item.load()
            }
            modellingItem = ModellingItemFactory.incrementVersion(item)
        }
        AbstractUIItem modellingUIItem = UIItemFactory.createItem(modellingItem)
        if (openNewVersion) {
            riskAnalyticsMainModel.notifyOpenDetailView(modellingUIItem)
        }
        return modellingUIItem
    }
}
