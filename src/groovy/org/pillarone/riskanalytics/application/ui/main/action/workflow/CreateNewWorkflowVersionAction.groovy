package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.action.CreateNewMajorVersion
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.core.workflow.WorkflowException

class CreateNewWorkflowVersionAction extends AbstractWorkflowAction {
    protected static Log LOG = LogFactory.getLog(CreateNewWorkflowVersionAction)

    // forbid meddling via -DCreateNewWorkflowVersion.promiscuous=false
    private static boolean promiscuous = System.getProperty("CreateNewWorkflowVersion.promiscuous","true").equalsIgnoreCase("true") //breaks tests when false!

    CreateNewWorkflowVersionAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("NewWorkflowVersion", tree, model)
    }

    @Override
    void doActionPerformed(ActionEvent event) {
        Parameterization parameterization = getSelectedItem()

        // PMO-2765 Juan described (20140425 chat) other users been creating new versions of his models w/o asking.
        //
        if( !promiscuous  ){  //forbid meddling via -DCreateNewMajorVersion.promiscuous=false

            if( ownerCanVetoUser(parameterization?.creator) ){
                String msg = "${parameterization?.creator.username} owns ${parameterization?.getNameAndVersion()}. \n(Hint: Save your own copy to work on.)"
                LOG.info(msg)
                ULCAlert alert = new ULCAlert(
                        UlcUtilities.getWindowAncestor(tree),
                        "Cannot Create New Workflow Version",
                        msg,
                        "Ok")
                alert.messageType = ULCAlert.INFORMATION_MESSAGE
                alert.show()
                return
            }
        }

        SortedSet allVersions = new TreeSet(VersionNumber.getExistingVersions(parameterization))
        if (parameterization.versionNumber != allVersions.last()) {
            throw new WorkflowException( parameterization.getNameAndVersion(),
                                         toStatus(),
                                         "Cannot create a new version. A newer version already exists: ${allVersions.last()}"   )
        }
        super.doActionPerformed(event)
    }



    @Override
    Status toStatus() {
        return Status.DATA_ENTRY
    }


}
