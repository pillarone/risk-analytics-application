package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.TabbedPaneManagerHelper
import org.pillarone.riskanalytics.application.ui.parameterization.model.CompareParameterViewModel
import org.pillarone.riskanalytics.application.ui.result.view.CompareParameterizationsView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareParameterizationUIItem extends AbstractUIItem {

    private List<Parameterization> parameterizations

    public CompareParameterizationUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel, List<Parameterization> parameterizations) {
        super(mainModel, simulationModel)
        this.parameterizations = parameterizations
    }

    String createTitle() {
        return TabbedPaneManagerHelper.getTabTitle(Parameterization)
    }

    ULCContainer createDetailView() {
        CompareParameterizationsView view = new CompareParameterizationsView(getViewModel())
        return view.content
    }

    AbstractModellingModel getViewModel() {
        CompareParameterViewModel model = new CompareParameterViewModel(model, parameterizations, ModelStructure.getStructureForModel(this.model.class))
        return model
    }

    void close() {
    }

    Object getItem() {
        return parameterizations
    }

    @Override
    boolean isLoaded() {
        return false
    }

    @Override
    void load(boolean completeLoad) {
        parameterizations.each {Parameterization parameterization ->
            if (!parameterization.isLoaded())
                parameterization.load(true)
        }
    }

    @Override
    String getToolTip() {
        return TabbedPaneManagerHelper.getToolTip(parameterizations)
    }

    @Override
    ULCIcon getIcon() {
        return UIUtils.getIcon("parametrization-active.png")
    }


}
