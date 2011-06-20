package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ResultConfigurationUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CreateNewMajorVersion extends SelectionTreeAction {
    //TODO: comment
    public CreateNewMajorVersion(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("NewMajorVersion", tree, model)
    }

    public void doActionPerformed(ActionEvent event) {
        createNewVersion(getSelectedUIItem())
    }

    private void createNewVersion(ParameterizationUIItem item) {
        item.createNewVersion(selectedModel)
    }


    private void createNewVersion(ResultConfigurationUIItem template) {
        template.createNewVersion(selectedModel)
    }

    private void createNewVersion(def node) {}

}
