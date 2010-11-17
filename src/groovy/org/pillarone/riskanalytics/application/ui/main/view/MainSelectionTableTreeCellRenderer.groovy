package org.pillarone.riskanalytics.application.ui.main.view

import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation

import org.pillarone.riskanalytics.core.workflow.Status

import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.base.action.GenerateReportAction
import org.pillarone.riskanalytics.application.ui.base.model.ItemGroupNode
import org.pillarone.riskanalytics.application.ui.base.model.ModelNode
import org.pillarone.riskanalytics.application.ui.batch.action.DeleteBatchAction
import org.pillarone.riskanalytics.application.ui.batch.action.NewBatchAction
import org.pillarone.riskanalytics.application.ui.batch.action.OpenBatchAction
import org.pillarone.riskanalytics.application.ui.batch.action.RunBatchAction
import org.pillarone.riskanalytics.application.ui.main.action.workflow.RejectWorkflowAction
import org.pillarone.riskanalytics.application.ui.main.action.workflow.SendToProductionAction
import org.pillarone.riskanalytics.application.ui.main.action.workflow.SendToReviewAction
import org.pillarone.riskanalytics.application.ui.main.action.workflow.StartWorkflowAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRootNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BatchRunNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.WorkflowParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.main.action.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MainSelectionTableTreeCellRenderer extends DefaultTableTreeCellRenderer {

    ULCPopupMenu parameterNodePopUpMenu
    ULCPopupMenu simulationNodePopUpMenu
    ULCPopupMenu groupNodePopUpMenu
    ULCPopupMenu parameterGroupNodePopUpMenu
    ULCPopupMenu simulationGroupNodePopUpMenu
    ULCPopupMenu modelNodePopUpMenu
    ULCPopupMenu batchesNodePopUpMenu
    ULCPopupMenu batchesRootNodePopUpMenu

    //ULCPopupMenu simulationTemplateNodePopUpMenu as it is the same as parameterNodePopupMenu
    ULCTableTree tree
    P1RATModel model

    private Map<Status, ULCPopupMenu> workflowMenus = new HashMap<Status, ULCPopupMenu>()

    public MainSelectionTableTreeCellRenderer(ULCTableTree tree, P1RATModel model) {
        this.tree = tree
        this.model = model
    }

    public void initPopUpMenu() {
        parameterNodePopUpMenu = new ULCPopupMenu()
        parameterNodePopUpMenu.name = "parameterNodePopUpMenu"
        parameterNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, model)))

        parameterNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new RenameAction(tree, model)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, model)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new SaveAsAction(tree, model)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new CreateNewMajorVersion(tree, model)))
        parameterNodePopUpMenu.add(new ULCMenuItem(getAction(StartWorkflowAction)))
        ULCMenuItem compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, model))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        parameterNodePopUpMenu.add(compareParameterizationMenuItem)
        parameterNodePopUpMenu.addSeparator()
        parameterNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, model)))
//
        ULCPopupMenu dataEntry = new ULCPopupMenu()
        dataEntry.add(new ULCMenuItem(new OpenItemAction(tree, model)))
        dataEntry.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        dataEntry.add(new ULCMenuItem(new SimulationAction(tree, model)))
        dataEntry.add(new ULCMenuItem(getAction(SendToReviewAction)))
        dataEntry.add(new ULCMenuItem(new SaveAsAction(tree, model)))
        compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, model))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        dataEntry.add(compareParameterizationMenuItem)
        workflowMenus.put(Status.DATA_ENTRY, dataEntry)

        ULCPopupMenu rejected = new ULCPopupMenu()
        rejected.add(new ULCMenuItem(new OpenItemAction(tree, model)))
        rejected.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        rejected.add(new ULCMenuItem(new SimulationAction(tree, model)))
        rejected.add(new ULCMenuItem(new SaveAsAction(tree, model)))
        compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, model))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        rejected.add(compareParameterizationMenuItem)
        workflowMenus.put(Status.REJECTED, rejected)

        ULCPopupMenu inReview = new ULCPopupMenu()
        inReview.add(new ULCMenuItem(new OpenItemAction(tree, model)))
        inReview.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        inReview.add(new ULCMenuItem(new SimulationAction(tree, model)))
        inReview.add(new ULCMenuItem(getAction(SendToProductionAction)))
        inReview.add(new ULCMenuItem(getAction(RejectWorkflowAction)))
        inReview.add(new ULCMenuItem(new SaveAsAction(tree, model)))
        compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, model))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        inReview.add(compareParameterizationMenuItem)
        workflowMenus.put(Status.IN_REVIEW, inReview)

        ULCPopupMenu inProduction = new ULCPopupMenu()
        inProduction.add(new ULCMenuItem(new OpenItemAction(tree, model)))
        inProduction.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        inProduction.add(new ULCMenuItem(new SimulationAction(tree, model)))
        inProduction.add(new ULCMenuItem(new SaveAsAction(tree, model)))
        compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, model))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        inProduction.add(compareParameterizationMenuItem)
        workflowMenus.put(Status.IN_PRODUCTION, inProduction)

        simulationNodePopUpMenu = new ULCPopupMenu()
        simulationNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, model)))

        simulationNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        simulationNodePopUpMenu.add(new ULCMenuItem(new RenameAction(tree, model)))
        ULCMenuItem compareSimulationMenuItem = new CompareSimulationMenuItem(new CompareSimulationsAction(tree, model))
        tree.addTreeSelectionListener(compareSimulationMenuItem)
        simulationNodePopUpMenu.add(compareSimulationMenuItem)


        ULCMenu reportsMenu = new ReportMenu("Reports")
        reportsMenu.add(new ULCMenuItem(getAction(GenerateReportAction, "Management Summary")))
        reportsMenu.add(new ULCMenuItem(getAction(GenerateReportAction, "Actuary Summary")))
        tree.addTreeSelectionListener(reportsMenu)
        simulationNodePopUpMenu.add(reportsMenu)
        simulationNodePopUpMenu.addSeparator()
        simulationNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, model)))

        groupNodePopUpMenu = new ULCPopupMenu()
        if (isStandAlone())
            groupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, model, 'ExportAll', true)))
        groupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, model, false)))
        groupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, model, true)))
        groupNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, model)))
        groupNodePopUpMenu.addSeparator()
        groupNodePopUpMenu.add(new ULCMenuItem(new DeleteAllGroupAction(tree, model, "DeleteAllResultTemplates")))

        parameterGroupNodePopUpMenu = new ULCPopupMenu()
        if (isStandAlone()) {
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, model, 'ExportAll', false)))
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, model, 'ExportAll', true)))
        }
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, model, false)))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, model, true)))
        if (isStandAlone())
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAllAction(tree, model, "importAllFromDir")))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, model)))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new CreateDefaultParameterizationAction(tree, model)))
        parameterGroupNodePopUpMenu.addSeparator()
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new DeleteAllGroupAction(tree, model, "DeleteAllParameters")))

        simulationGroupNodePopUpMenu = new ULCPopupMenu()
        if (isStandAlone())
            simulationGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, model, 'ExportAll', false)))
        simulationGroupNodePopUpMenu.addSeparator()
        simulationGroupNodePopUpMenu.add(new ULCMenuItem(new DeleteAllGroupAction(tree, model, "DeleteAllSimulations")))

        modelNodePopUpMenu = new ULCPopupMenu()
        modelNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, model)))

        batchesNodePopUpMenu = new ULCPopupMenu()
        batchesNodePopUpMenu.name = "batchesNodePopUpMenu"
        batchesNodePopUpMenu.add(new ULCMenuItem(new OpenBatchAction(tree, model)))
        batchesNodePopUpMenu.add(new ULCMenuItem(new NewBatchAction(tree, model)))
        batchesNodePopUpMenu.add(new ULCMenuItem(new RunBatchAction(tree, model)))
        batchesNodePopUpMenu.addSeparator()
        batchesNodePopUpMenu.add(new ULCMenuItem(new DeleteBatchAction(tree, model)))

        batchesRootNodePopUpMenu = new ULCPopupMenu()
        batchesRootNodePopUpMenu.add(new ULCMenuItem(new NewBatchAction(tree, model)))
    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        setFont(node)
        IRendererComponent component = super.getTableTreeCellRendererComponent(tree, value, selected, expanded, leaf, hasFocus, node)
        setPopUpMenu(component, node)
        setToolTip(component, node)
        setIcon(getIcon(node))
        return component

    }


    void setPopUpMenu(ULCComponent component, def node) {
        component.setComponentPopupMenu(null)
    }

    void setPopUpMenu(ULCComponent component, ParameterizationNode node) {
        component.setComponentPopupMenu(parameterNodePopUpMenu)
    }

    void setPopUpMenu(ULCComponent component, WorkflowParameterizationNode node) {
        component.setComponentPopupMenu(workflowMenus.get(node.status))
    }

    void setPopUpMenu(ULCComponent component, ResultConfigurationNode node) {
        component.setComponentPopupMenu(parameterNodePopUpMenu)
    }

    void setPopUpMenu(ULCComponent component, SimulationNode node) {
        component.setComponentPopupMenu(simulationNodePopUpMenu)
    }

    void setPopUpMenu(ULCComponent component, BatchRunNode node) {
        component.setComponentPopupMenu(batchesNodePopUpMenu)
    }

    void setFont(ParameterizationNode node) {
        if (node.parent && !(node.parent instanceof SimulationNode)) {
            setFont(new Font(getFont().getName(), !node.item.valid ? Font.ITALIC : Font.PLAIN, getFont().getSize()))
            setForeground(!node.item.valid ? Color.gray : null)
        }
    }

    void setFont(def node) {
        setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()))
        setForeground(null)
    }

    void setToolTip(ULCComponent component, SimulationNode node) {
        if (node instanceof SimulationNode) {
            StringBuilder builder = new StringBuilder("<html><div style='width:100px;'>")
            builder.append(UIUtils.getText(this.class, "numberOfIterations") + ": " + node.item.numberOfIterations)
            if (node.item.comment)
                builder.append("<br>" + UIUtils.getText(this.class, "comment") + ": " + node.item.comment)
            builder.append("</div></html>")
            component.setToolTipText String.valueOf(builder.toString())
        }
    }

    void setToolTip(ULCComponent component, def node) {
        component.setToolTipText String.valueOf("")
    }

    void setToolTip(ULCComponent component, ParameterizationNode node) {
        component.setToolTipText node.item.status == Status.NONE ? String.valueOf("") : node.item.status.displayName
    }

    void setToolTip(ULCComponent component, ResultConfigurationNode node) {
        component.setToolTipText String.valueOf("")
    }

    void setToolTip(ULCComponent component, BatchRunNode node) {
        component.setToolTipText String.valueOf("")
    }

    void setPopUpMenu(ULCComponent component, ItemGroupNode node) {
        if (node.itemClass == Simulation) {
            component.setComponentPopupMenu(simulationGroupNodePopUpMenu)
        } else if (node.itemClass == Parameterization) {
            component.setComponentPopupMenu(parameterGroupNodePopUpMenu)
        } else {
            component.setComponentPopupMenu(groupNodePopUpMenu)
        }
    }

    ULCIcon getIcon(def node) {
        null
    }

    ULCIcon getIcon(ItemGroupNode node) {
        if (node.itemClass == Simulation) {
            return UIUtils.getIcon("results-active.png")
        } else if (node.itemClass == ResultConfiguration) {
            return UIUtils.getIcon("resulttemplate-active.png")
        } else if (node.itemClass == Parameterization) {
            return UIUtils.getIcon("parametrization-active.png")
        }
    }

    void setPopUpMenu(ULCComponent component, ModelNode node) {
        component.setComponentPopupMenu(modelNodePopUpMenu)
    }

    void setPopUpMenu(ULCComponent component, BatchRootNode node) {
        component.setComponentPopupMenu(batchesRootNodePopUpMenu)
    }

    AbstractAction getAction(Class clazz, String name = null) {
        try {
            switch (clazz) {
                case StartWorkflowAction: return new StartWorkflowAction(tree, model)
                case RejectWorkflowAction: return new RejectWorkflowAction(tree, model)
                case SendToReviewAction: return new SendToReviewAction(tree, model)
                case SendToProductionAction: return new SendToProductionAction(tree, model)
                case GenerateReportAction: return new GenerateReportAction(name, tree, model)
            }
        } catch (Exception ex) {}
    }

    boolean isStandAlone() {
        try {
            return UserContext.isStandAlone()
        } catch (Exception ex) {}
    }
}