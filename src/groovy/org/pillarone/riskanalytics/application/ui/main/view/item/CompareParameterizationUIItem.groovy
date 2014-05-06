package org.pillarone.riskanalytics.application.ui.main.view.item
import com.ulcjava.base.application.util.ULCIcon
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
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
    final Model model

    CompareParameterizationUIItem(Model model, List<Parameterization> parameterizations) {
        this.model = model
        this.parameterizations = parameterizations
    }

    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    String createTitle() {
        return TabbedPaneManagerHelper.getTabTitle(Parameterization)
    }

    IDetailView createDetailView() {
        return new CompareParameterizationsView(viewModel as CompareParameterViewModel, riskAnalyticsMainModel)
    }

    private CompareParameterViewModel getViewModel() {
        return new CompareParameterViewModel(model, parameterizations, ModelStructure.getStructureForModel(model.class))
    }

    void close() {
    }

    @Override
    boolean isDeletable() {
        return false
    }

    @Override
    boolean isLoaded() {
        return false
    }

    @Override
    void load(boolean completeLoad) {
        parameterizations.each { Parameterization parameterization ->
            if (!parameterization.loaded)
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

    String getWindowTitle() {
        String windowTitle = model ? model.name : ""
        windowTitle += " " + createTitle()
        return windowTitle
    }
}
