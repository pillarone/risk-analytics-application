package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ResourceUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ResultConfigurationUIItem
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CreateNewMajorVersion extends SingleItemAction {

    private static Log LOG = LogFactory.getLog(CreateNewMajorVersion)

    ModellingUIItem modellingUIItem

    public CreateNewMajorVersion(ULCTableTree tree) {
        super("NewMajorVersion", tree)
    }

    public CreateNewMajorVersion(ModellingUIItem modellingUIItem) {
        super("NewMajorVersion")
        this.modellingUIItem = modellingUIItem
    }


    public void doActionPerformed(ActionEvent event) {

// Probably this one is better without it, as there is no keyboard shortcut to reach it and
//
//        if( quitWithAlertIfCalledWhenDisabled() ){
//            return
//        }

        ModellingUIItem uiItem = getUIItem()
        if (uiItem instanceof ParameterizationUIItem) {
            Closure okAction = { String commentText ->
                if (!uiItem.isLoaded()) {
                    uiItem.load()
                }
                LOG.info("Creating new version of ${uiItem.nameAndVersion}")
                createNewVersion(uiItem, commentText)
            }
            NewVersionCommentDialog versionCommentDialog = new NewVersionCommentDialog(okAction)
            versionCommentDialog.show()
        } else {
            createNewVersion(getUIItem())
        }
    }

    private void createNewVersion(ParameterizationUIItem item, String commentText) {
        item.createNewVersion(commentText)
    }

    private void createNewVersion(ResultConfigurationUIItem template) {
        template.createNewVersion()
    }

    private void createNewVersion(ResourceUIItem resource) {
        resource.createNewVersion()
    }

    private void createNewVersion(def node) {
        ULCAlert alert = new ULCAlert(UlcUtilities.getWindowAncestor(tree), "Not supported", "Creating a new version is currently not supported for this item type $node", "Ok")
        alert.messageType = ULCAlert.INFORMATION_MESSAGE
        alert.show()
    }

    ModellingUIItem getUIItem() {
        if (modellingUIItem) return modellingUIItem
        return (ModellingUIItem) getSelectedUIItem()
    }
}
