package org.pillarone.riskanalytics.application.ui.main.view.item

import grails.util.Holders
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.eventbus.RiskAnalyticsEventBus
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

abstract class ModellingUiItemWithModel<T extends IDetailView> extends ModellingUIItem<T> {

    ModellingUiItemWithModel(ModellingItem item) {
        super(item)
    }

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        return null
    }

    RiskAnalyticsEventBus getRiskAnalyticsEventBus() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsEventBus', RiskAnalyticsEventBus)
    }

    @Override
    boolean deleteDependingResults() {
        return UIItemUtils.deleteDependingResults(item)
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
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(modellingUIItem))
        }
        return modellingUIItem
    }
}
