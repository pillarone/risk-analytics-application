package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.resource.model.ResourceViewModel
import org.pillarone.riskanalytics.application.ui.resource.view.ResourceView
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Resource

@CompileStatic
class ResourceUIItem extends ModellingUIItem {

    ResourceUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel, Resource item) {
        super(mainModel, simulationModel, item)
    }

    @Override
    void close() {
        ResourceViewModel viewModel = mainModel.viewModelsInUse[this] as ResourceViewModel
        Resource resource = item as Resource
        resource.removeListener(viewModel)
        super.close()
    }

    @Override
    ULCContainer createDetailView() {
        return new ResourceView(viewModel as ResourceViewModel, mainModel).content
    }

    @Override
    ResourceViewModel getViewModel() {
        Resource resource = item as Resource
        ResourceViewModel model = new ResourceViewModel(resource)
        model.mainModel = mainModel
        resource.addListener(model)
        return model
    }

    @Override
    boolean isVersionable() {
        return true
    }

    @Override
    Model getModel() {
        return null
    }

    @Override
    Resource getItem() {
        super.getItem() as Resource
    }
}
