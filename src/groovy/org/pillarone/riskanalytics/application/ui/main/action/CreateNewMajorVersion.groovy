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
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CreateNewMajorVersion extends SelectionTreeAction {

    private static Log LOG = LogFactory.getLog(CreateNewMajorVersion)

    // forbid meddling via -DCreateNewMajorVersion.promiscuous=false
    public static boolean promiscuous = System.getProperty("CreateNewMajorVersion.promiscuous","true").equalsIgnoreCase("true") //breaks tests when false!

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
        item.createNewVersion(item.getModel(), commentText)
    }

    private void createNewVersion(ResultConfigurationUIItem template) {
        template.createNewVersion(template.getModel())
    }

    private void createNewVersion(ResourceUIItem resource) {
        resource.createNewVersion(resource.getModel())
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

    boolean isEnabled() {
        // If you accidentally do Create new version on a large selection, it leaves you very worried
        //
        if (getAllSelectedObjectsSimpler().size() > 1) {
            return false
        }

        // PMO-2765 Juan described (20140425 chat) other users been creating new versions of his models w/o asking.
        //
        if( !promiscuous  ){  //allow via -DCreateNewMajorVersion.promiscuous=true

            if( itemOwnerCanVetoCurrentUser(getUIItem()?.getItem()?.creator) ){
                LOG.info("Hint: Use Save As to create your own item.")
                return false
            }
        }
        return super.isEnabled()//generic checks like user roles
    }

}
