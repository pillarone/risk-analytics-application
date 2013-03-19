package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ResultConfigurationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import org.pillarone.riskanalytics.application.ui.main.view.item.ResourceUIItem

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
            Closure okAction = {String commentText ->
                if (!uiItem.isLoaded()) {
                    uiItem.load()
                }
                createNewVersion(uiItem, commentText)
            }
            NewVersionCommentDialog versionCommentDialog = new NewVersionCommentDialog(okAction)
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

    private void createNewVersion(ResourceUIItem resource) {
        resource.createNewVersion(resource.getModel())
    }

    private void createNewVersion(def node) {
        ULCAlert alert = new ULCAlert(UlcUtilities.getWindowAncestor(tree), "Not supported", "Creating a new version is currently not supported for this item type", "Ok")
        alert.messageType = ULCAlert.INFORMATION_MESSAGE
        alert.show()
    }

    ModellingUIItem getUIItem() {
        if (modellingUIItem) return modellingUIItem
        return (ModellingUIItem) getSelectedUIItem()
    }

}
