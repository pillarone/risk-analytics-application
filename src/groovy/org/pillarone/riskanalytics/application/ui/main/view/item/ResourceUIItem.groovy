package org.pillarone.riskanalytics.application.ui.main.view.item
import com.ulcjava.base.application.ULCContainer
import grails.util.Holders
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.resource.model.ResourceViewModel
import org.pillarone.riskanalytics.application.ui.resource.view.ResourceView
import org.pillarone.riskanalytics.core.simulation.item.Resource

@CompileStatic
class ResourceUIItem extends ModellingUIItem {

    ResourceUIItem(Resource item) {
        super(item)
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
    void close() {
        ResourceViewModel viewModel = riskAnalyticsMainModel.viewModelsInUse[this] as ResourceViewModel
        Resource resource = item as Resource
        resource.removeListener(viewModel)
        super.close()
    }

    @Override
    ULCContainer createDetailView() {
        return new ResourceView(viewModel as ResourceViewModel, riskAnalyticsMainModel).content
    }

    @Override
    ResourceViewModel getViewModel() {
        Resource resource = item as Resource
        ResourceViewModel model = new ResourceViewModel(resource)
        model.mainModel = riskAnalyticsMainModel
        resource.addListener(model)
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
