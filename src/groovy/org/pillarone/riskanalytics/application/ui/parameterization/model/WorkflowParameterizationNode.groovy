package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.action.workflow.CreateNewWorkflowVersionAction
import org.pillarone.riskanalytics.application.ui.main.action.workflow.RejectWorkflowAction
import org.pillarone.riskanalytics.application.ui.main.action.workflow.SendToProductionAction
import org.pillarone.riskanalytics.application.ui.main.action.workflow.SendToReviewAction
import org.pillarone.riskanalytics.application.ui.main.view.CompareParameterizationMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.SendToProductionMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.application.ui.main.action.*

class WorkflowParameterizationNode extends ParameterizationNode {

    public WorkflowParameterizationNode(ParameterizationUIItem parameterizationUIItem) {
        super(parameterizationUIItem);
    }

    @Override
    public ULCPopupMenu getPopupMenu(MainSelectionTableTreeCellRenderer renderer, ULCTableTree tree) {
        if (renderer.workflowMenus.get(getStatus())) return renderer.workflowMenus.get(getStatus())
        ULCPopupMenu dataEntry = new ULCPopupMenu()
        dataEntry.add(new ULCMenuItem(new OpenItemAction(tree, abstractUIItem.mainModel)))
        dataEntry.add(new ULCMenuItem(new SimulationAction(tree, abstractUIItem.mainModel)))
        dataEntry.addSeparator()
        CompareParameterizationMenuItem compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, abstractUIItem.mainModel))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        dataEntry.add(compareParameterizationMenuItem)
        dataEntry.add(new ULCMenuItem(new TagsAction(tree, abstractUIItem.mainModel)))
        dataEntry.addSeparator()
        dataEntry.add(new ULCMenuItem(new SaveAsAction(tree, abstractUIItem.mainModel)))
        dataEntry.add(new ULCMenuItem(new ExportItemAction(tree, abstractUIItem.mainModel)))
        dataEntry.addSeparator()
        dataEntry.add(new ULCMenuItem(new SendToReviewAction(tree, abstractUIItem.mainModel)))
        renderer.workflowMenus.put(Status.DATA_ENTRY, dataEntry)

        ULCPopupMenu rejected = new ULCPopupMenu()
        rejected.add(new ULCMenuItem(new OpenItemAction(tree, abstractUIItem.mainModel)))
        rejected.add(new ULCMenuItem(new SimulationAction(tree, abstractUIItem.mainModel)))
        rejected.addSeparator()
        compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, abstractUIItem.mainModel))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        rejected.add(compareParameterizationMenuItem)
        rejected.add(new ULCMenuItem(new TagsAction(tree, abstractUIItem.mainModel)))
        rejected.addSeparator()
        rejected.add(new ULCMenuItem(new SaveAsAction(tree, abstractUIItem.mainModel)))
        rejected.add(new ULCMenuItem(new ExportItemAction(tree, abstractUIItem.mainModel)))
        renderer.workflowMenus.put(Status.REJECTED, rejected)

        ULCPopupMenu inReview = new ULCPopupMenu()
        inReview.add(new ULCMenuItem(new OpenItemAction(tree, abstractUIItem.mainModel)))
        inReview.add(new ULCMenuItem(new SimulationAction(tree, abstractUIItem.mainModel)))
        inReview.addSeparator()
        compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, abstractUIItem.mainModel))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        inReview.add(compareParameterizationMenuItem)
        inReview.add(new ULCMenuItem(new TagsAction(tree, abstractUIItem.mainModel)))
        inReview.addSeparator()
        inReview.add(new ULCMenuItem(new SaveAsAction(tree, abstractUIItem.mainModel)))
        inReview.add(new ULCMenuItem(new ExportItemAction(tree, abstractUIItem.mainModel)))
        inReview.addSeparator()
        ULCMenuItem sendToProductionMenuItem = new SendToProductionMenuItem(new SendToProductionAction(tree, abstractUIItem.mainModel))
        inReview.add(sendToProductionMenuItem)
        tree.addTreeSelectionListener(sendToProductionMenuItem)
        inReview.add(new ULCMenuItem(new RejectWorkflowAction(tree, abstractUIItem.mainModel)))
        renderer.workflowMenus.put(Status.IN_REVIEW, inReview)

        ULCPopupMenu inProduction = new ULCPopupMenu()
        inProduction.add(new ULCMenuItem(new OpenItemAction(tree, abstractUIItem.mainModel)))
        inProduction.add(new ULCMenuItem(new SimulationAction(tree, abstractUIItem.mainModel)))
        inProduction.addSeparator()
        inProduction.add(new ULCMenuItem(new SaveAsAction(tree, abstractUIItem.mainModel)))
        inProduction.add(new ULCMenuItem(new ExportItemAction(tree, abstractUIItem.mainModel)))
        inProduction.addSeparator()
        inProduction.add(new ULCMenuItem(new CreateNewWorkflowVersionAction(tree, abstractUIItem.mainModel)))
        inProduction.addSeparator()
        compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, abstractUIItem.mainModel))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        inProduction.add(compareParameterizationMenuItem)
        inProduction.add(new ULCMenuItem(new TagsAction(tree, abstractUIItem.mainModel)))
        renderer.workflowMenus.put(Status.IN_PRODUCTION, inProduction)
        return renderer.workflowMenus.get(getStatus())
    }



    public Status getStatus() {
        abstractUIItem.item.status
    }
}
