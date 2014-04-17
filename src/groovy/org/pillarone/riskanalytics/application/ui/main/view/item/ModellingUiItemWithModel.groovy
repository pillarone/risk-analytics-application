package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Resource

abstract class ModellingUiItemWithModel extends ModellingUIItem {

    private Model model

    ModellingUiItemWithModel(Model model, ModellingItem item) {
        super(item)
        this.model = model
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
    ULCContainer createDetailView() {
        return null
    }

    @Override
    Object getViewModel() {
        return null
    }

    @Override
    boolean deleteDependingResults() {
        return UIItemUtils.deleteDependingResults(model, this)
    }

    Model getModel() {
        if (!model) {
            model = item.modelClass.newInstance() as Model
            model.init()
        }
        return model
    }

    void close() {
        riskAnalyticsMainModel.closeItem(model, this)
    }


    ModellingUIItem createNewVersion(Model model, boolean openNewVersion = true) {
        ModellingItem modellingItem = null
        item.daoClass.withTransaction { status ->
            if (!item.loaded) {
                item.load()
            }
            modellingItem = ModellingItemFactory.incrementVersion(item)
        }
        riskAnalyticsMainModel.fireModelChanged()
        AbstractUIItem modellingUIItem = UIItemFactory.createItem(modellingItem, model)
        if (openNewVersion) {
            riskAnalyticsMainModel.openItem(model, modellingUIItem)
        }
        return modellingUIItem
    }

    @Override
    ModellingItem addItem(ModellingUIItem modellingUIItem, String name) {
        ModellingUiItemWithModel uiItemWithModel = modellingUIItem as ModellingUiItemWithModel
        ModellingItem newItem = super.addItem(modellingUIItem, name)
        uiItemWithModel.model
        if (!(newItem instanceof Resource)) { //re-create model (PMO-1961) - do nothing if it's a resource
            Model modelInstance = newItem?.modelClass?.newInstance() as Model
            modelInstance?.init()
        }
    }
}
