package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.view.DetailViewManager
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationResultUIItem
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.result.view.ResultView

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentsSwitchAction extends AbstractAction {
    boolean comment = false
    private final RiskAnalyticsMainModel model

    CommentsSwitchAction(RiskAnalyticsMainModel model, String text) {
        super(text);
        this.model = model
    }

    void actionPerformed(ActionEvent event) {
        if (enabled) {
            IDetailView detailView = getDetailView(model.currentItem)
            if (detailView instanceof ResultView) {
                detailView.getModel().navigationSelected()
            }
            if (detailView instanceof ParameterView) {
                detailView.getModel().navigationSelected()
            }
        }
    }

    IDetailView getDetailView(AbstractUIItem uiItem) {
        Holders.grailsApplication.mainContext.getBean('detailViewManager', DetailViewManager).getDetailViewForItem(uiItem)
    }

    @Override
    boolean isEnabled() {
        return (model.currentItem instanceof ParameterizationUIItem) || (model.currentItem instanceof SimulationResultUIItem)
    }
}
