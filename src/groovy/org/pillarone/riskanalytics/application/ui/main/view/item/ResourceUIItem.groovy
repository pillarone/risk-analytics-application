package org.pillarone.riskanalytics.application.ui.main.view.item

import grails.util.Holders
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.resource.model.ResourceViewModel
import org.pillarone.riskanalytics.application.ui.resource.view.ResourceView
import org.pillarone.riskanalytics.core.components.IResource
import org.pillarone.riskanalytics.core.components.ResourceModelAdapter
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Resource

@CompileStatic
class ResourceUIItem extends ModellingUiItemWithModel {

    ResourceUIItem(Resource item) {
        super(item)
    }

    @Override
    protected Model createModel() {
        Model modelAdapter = new ResourceModelAdapter(item.modelClass.newInstance() as IResource)
        modelAdapter.init()
        modelAdapter
    }

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel', NavigationTableTreeModel)
    }

    @Override
    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    @Override
    IDetailView createDetailView() {
        return new ResourceView(viewModel, riskAnalyticsMainModel)
    }

    private ResourceViewModel getViewModel() {
        Resource resource = item as Resource
        ResourceViewModel model = new ResourceViewModel(resource)
        model.mainModel = riskAnalyticsMainModel
        return model
    }

    @Override
    boolean isVersionable() {
        return true
    }

    @Override
    Resource getItem() {
        super.getItem() as Resource
    }
}
