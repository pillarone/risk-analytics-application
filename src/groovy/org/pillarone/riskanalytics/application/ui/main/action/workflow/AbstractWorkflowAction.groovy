package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.core.workflow.StatusChangeService
import org.pillarone.riskanalytics.application.ui.base.model.TableTreeBuilderUtils
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe

abstract class AbstractWorkflowAction extends SelectionTreeAction {

    private StatusChangeService service = getService()

    public AbstractWorkflowAction(String name, ULCTableTree tree, RiskAnalyticsMainModel model) {
        super(name, tree, model)
    }

    void doActionPerformed(ActionEvent event) {
        Parameterization item = getSelectedItem()
        if (!item.isLoaded()) {
            item.load()
        }
        Status toStatus = toStatus()

        if (toStatus == Status.DATA_ENTRY) {
            Closure changeStatusAction = {String commentText ->
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
        parameterization = (Parameterization) ModellingItemFactory.getItem(parameterization.dao, parameterization.modelClass)
        parameterization.load()
        if (!item.is(parameterization)) {
            model.navigationTableTreeModel.addNodeForItem(parameterization)
        } else {
            ParameterizationUIItem parameterizationUIItem = (ParameterizationUIItem) UIItemFactory.createItem(parameterization, null, model)
            ITableTreeNode paramNode = TableTreeBuilderUtils.findNodeForItem(model.navigationTableTreeModel.root, parameterizationUIItem)
            model.navigationTableTreeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(paramNode) as Object[]))
        }
        parameterization
    }

    abstract Status toStatus()

    final boolean isEnabled() {
        return super.isEnabled() && isActionEnabled()
    }


    protected boolean isActionEnabled() {
        return true
    }

    StatusChangeService getService() {
        try {
            return StatusChangeService.getService()
        } catch (Exception ex) {}
        return null
    }


}
