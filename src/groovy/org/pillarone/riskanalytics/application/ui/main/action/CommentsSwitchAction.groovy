package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.event.ActionEvent
import grails.util.Holders
import org.pillarone.riskanalytics.application.ui.main.view.DetailViewManager
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.result.view.ResultView
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CommentsSwitchAction extends AbstractAction {
    boolean comment = false

    CommentsSwitchAction(String text) {
        super(text);
    }

    void actionPerformed(ActionEvent event) {
        if (enabled) {
            IDetailView detailView = detailViewManager.openDetailView
            if (detailView instanceof ResultView) {
                detailView.getModel().navigationSelected()
            }
            if (detailView instanceof ParameterView) {
                detailView.getModel().navigationSelected()
            }
        }
    }

    private DetailViewManager getDetailViewManager() {
        Holders.grailsApplication.mainContext.getBean('detailViewManager', DetailViewManager)
    }

    @Override
    boolean isEnabled() {
        IDetailView detailView = detailViewManager.openDetailView
        detailView instanceof ParameterView || detailView instanceof ResultView
    }
}
