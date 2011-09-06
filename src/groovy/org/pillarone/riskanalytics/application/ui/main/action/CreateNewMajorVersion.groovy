package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ResultConfigurationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CreateNewMajorVersion extends SelectionTreeAction {

    ModellingUIItem modellingUIItem

    public CreateNewMajorVersion(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("NewMajorVersion", tree, model)
    }

    public CreateNewMajorVersion(ModellingUIItem modellingUIItem) {
        super("NewMajorVersion")
        this.modellingUIItem = modellingUIItem
    }



    public void doActionPerformed(ActionEvent event) {
        ModellingUIItem uiItem = getUIItem()
        if (uiItem instanceof ParameterizationUIItem) {
            Closure okAction = {ModellingUIItem modellingUIItem, String commentText ->
                createNewVersion(modellingUIItem, commentText)
            }
            NewVersionCommentDialog versionCommentDialog = new NewVersionCommentDialog(uiItem, okAction)
            versionCommentDialog.show()
        } else
            createNewVersion(getUIItem())
    }

    private void createNewVersion(ParameterizationUIItem item, String commentText) {
        item.createNewVersion(item.getModel(), commentText)
    }


    private void createNewVersion(ResultConfigurationUIItem template) {
        template.createNewVersion(template.getModel())
    }

    private void createNewVersion(def node) {}

    ModellingUIItem getUIItem() {
        if (modellingUIItem) return modellingUIItem
        return (ModellingUIItem) getSelectedUIItem()
    }

}
