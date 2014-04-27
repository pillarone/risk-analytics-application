package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.application.ui.main.action.CreateNewMajorVersion
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.core.workflow.StatusChangeService

abstract class AbstractWorkflowAction extends SelectionTreeAction {

    private StatusChangeService service = getService()

    public AbstractWorkflowAction(String name, ULCTableTree tree, RiskAnalyticsMainModel model) {
        super(name, tree, model)
    }

    // This method is shared by subclasses
    //
    void doActionPerformed(ActionEvent event) {
        Parameterization item = getSelectedItem()
        if (!item.isLoaded()) {
            item.load()
        }
        Status toStatus = toStatus()

        if (toStatus == Status.DATA_ENTRY) {
            Closure changeStatusAction = { String commentText ->
                ExceptionSafe.protect {
                    AbstractUIItem uiItem = getSelectedUIItem()
                    if (!uiItem.isLoaded()) {
                        uiItem.load()
                    }
                    Parameterization parameterization = changeStatus(item, toStatus)
                    Tag versionTag = Tag.findByName(NewCommentView.VERSION_COMMENT)
                    parameterization.addTaggedComment("v${parameterization.versionNumber}: ${commentText}", versionTag)
                    parameterization.save()
                }
            }
            NewVersionCommentDialog versionCommentDialog = new NewVersionCommentDialog(changeStatusAction)
            versionCommentDialog.show()
        } else {
            changeStatus(item, toStatus)
        }

    }

    protected Parameterization changeStatus(Parameterization item, Status toStatus) {
        Parameterization parameterization = service.changeStatus(item, toStatus)
        parameterization.save()
        ParameterizationDAO dao = parameterization.dao as ParameterizationDAO
        parameterization = (Parameterization) ModellingItemFactory.getItem(dao)
        parameterization.load()
        return parameterization
    }

    abstract Status toStatus()

    boolean isEnabled() {
        // If you accidentally do Create new version on a large selection, it leaves you very worried
        //
        if (getAllSelectedObjectsSimpler().size() > 1) {
            return false
        }

        // PMO-2765 Juan described (20140425 chat) other users been creating new versions of his models w/o asking.
        //
        if( !CreateNewMajorVersion.promiscuous  ){  //forbid meddling via -DCreateNewMajorVersion.promiscuous=false

            Parameterization p = getSelectedItem()
            if( ownerCanVetoUser(p?.creator) ){
                return false
            }
        }
        return super.isEnabled()//generic checks like user roles
    }

    StatusChangeService getService() {
        try {
            return StatusChangeService.getService()
        } catch (Exception ex) {
            LOG.info("StatusChangeService.getService() threw: ", ex)
        }
        return null
    }


}
