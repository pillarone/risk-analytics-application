package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.tabletree.DefaultTableTreeCellRenderer
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.ULCIcon
import org.codehaus.groovy.grails.commons.ApplicationHolder
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
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.workflow.Status
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.main.action.workflow.CreateNewWorkflowVersionAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MainSelectionTableTreeCellRenderer extends DefaultTableTreeCellRenderer {

    ULCPopupMenu parameterNodePopUpMenu
    ULCPopupMenu resultConfigurationNodePopUpMenu
    ULCPopupMenu simulationNodePopUpMenu
    ULCPopupMenu resultConfigurationGroupNodePopUpMenu
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
        parameterNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, model)))
        parameterNodePopUpMenu.addSeparator()
        ULCMenuItem compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, model))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        parameterNodePopUpMenu.add(compareParameterizationMenuItem)
        parameterNodePopUpMenu.add(new ULCMenuItem(new TagsAction(tree, model)))
        parameterNodePopUpMenu.addSeparator()
        parameterNodePopUpMenu.add(new ULCMenuItem(new RenameAction(tree, model)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new SaveAsAction(tree, model)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new CreateNewMajorVersion(tree, model)))
        parameterNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        if (!UserContext.isStandAlone()) {
            def transactionsEnabled = ApplicationHolder.getApplication().getConfig().getProperty("transactionsEnabled")
            if (transactionsEnabled != null && transactionsEnabled == true) {
                parameterNodePopUpMenu.addSeparator()
                parameterNodePopUpMenu.add(new ULCMenuItem(new StartWorkflowAction(tree, model)))
            }
        }
        parameterNodePopUpMenu.addSeparator()
        parameterNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, model)))

        resultConfigurationNodePopUpMenu = new ULCPopupMenu()
        resultConfigurationNodePopUpMenu.name = "resultConfigurationNodePopUpMenu"
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, model)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, model)))
        resultConfigurationNodePopUpMenu.addSeparator()
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new RenameAction(tree, model)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new SaveAsAction(tree, model)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new CreateNewMajorVersion(tree, model)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        resultConfigurationNodePopUpMenu.addSeparator()
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, model)))

        ULCPopupMenu dataEntry = new ULCPopupMenu()
        dataEntry.add(new ULCMenuItem(new OpenItemAction(tree, model)))
        dataEntry.add(new ULCMenuItem(new SimulationAction(tree, model)))
        dataEntry.addSeparator()
        compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, model))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        dataEntry.add(compareParameterizationMenuItem)
        dataEntry.add(new ULCMenuItem(new TagsAction(tree, model)))
        dataEntry.addSeparator()
        dataEntry.add(new ULCMenuItem(new SaveAsAction(tree, model)))
        dataEntry.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        dataEntry.addSeparator()
        dataEntry.add(new ULCMenuItem(new SendToReviewAction(tree, model)))
        workflowMenus.put(Status.DATA_ENTRY, dataEntry)

        ULCPopupMenu rejected = new ULCPopupMenu()
        rejected.add(new ULCMenuItem(new OpenItemAction(tree, model)))
        rejected.add(new ULCMenuItem(new SimulationAction(tree, model)))
        rejected.addSeparator()
        compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, model))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        rejected.add(compareParameterizationMenuItem)
        rejected.add(new ULCMenuItem(new TagsAction(tree, model)))
        rejected.addSeparator()
        rejected.add(new ULCMenuItem(new SaveAsAction(tree, model)))
        rejected.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        workflowMenus.put(Status.REJECTED, rejected)

        ULCPopupMenu inReview = new ULCPopupMenu()
        inReview.add(new ULCMenuItem(new OpenItemAction(tree, model)))
        inReview.add(new ULCMenuItem(new SimulationAction(tree, model)))
        inReview.addSeparator()
        compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, model))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        inReview.add(compareParameterizationMenuItem)
        inReview.add(new ULCMenuItem(new TagsAction(tree, model)))
        inReview.addSeparator()
        inReview.add(new ULCMenuItem(new SaveAsAction(tree, model)))
        inReview.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        inReview.addSeparator()
        ULCMenuItem sendToProductionMenuItem = new SendToProductionMenuItem(new SendToProductionAction(tree, model))
        inReview.add(sendToProductionMenuItem)
        tree.addTreeSelectionListener(sendToProductionMenuItem)
        inReview.add(new ULCMenuItem(new RejectWorkflowAction(tree, model)))
        workflowMenus.put(Status.IN_REVIEW, inReview)

        ULCPopupMenu inProduction = new ULCPopupMenu()
        inProduction.add(new ULCMenuItem(new OpenItemAction(tree, model)))
        inProduction.add(new ULCMenuItem(new SimulationAction(tree, model)))
        inProduction.addSeparator()
        inProduction.add(new ULCMenuItem(new SaveAsAction(tree, model)))
        inProduction.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        inProduction.addSeparator()
        inProduction.add(new ULCMenuItem(new CreateNewWorkflowVersionAction(tree, model)))
        inProduction.addSeparator()
        compareParameterizationMenuItem = new CompareParameterizationMenuItem(new CompareParameterizationsAction(tree, model))
        tree.addTreeSelectionListener(compareParameterizationMenuItem)
        inProduction.add(compareParameterizationMenuItem)
        inProduction.add(new ULCMenuItem(new TagsAction(tree, model)))
        workflowMenus.put(Status.IN_PRODUCTION, inProduction)

        simulationNodePopUpMenu = new ULCPopupMenu()
        simulationNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, model)))

        simulationNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree, model)))
        simulationNodePopUpMenu.add(new ULCMenuItem(new RenameAction(tree, model)))
        ULCMenuItem compareSimulationMenuItem = new CompareSimulationMenuItem(new CompareSimulationsAction(tree, model))
        tree.addTreeSelectionListener(compareSimulationMenuItem)
        simulationNodePopUpMenu.add(compareSimulationMenuItem)


        ULCMenu reportsMenu = new ReportMenu("Reports")
        reportsMenu.add(new ULCMenuItem(new GenerateReportAction("Management Summary", tree, model)))
        reportsMenu.add(new ULCMenuItem(new GenerateReportAction("Actuary Summary", tree, model)))
        tree.addTreeSelectionListener(reportsMenu)
        simulationNodePopUpMenu.add(reportsMenu)
        simulationNodePopUpMenu.addSeparator()
        simulationNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, model)))

        resultConfigurationGroupNodePopUpMenu = new ULCPopupMenu()
        if (UserContext.isStandAlone())
            resultConfigurationGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, model, 'ExportAll', true)))
        resultConfigurationGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, model, false)))
        resultConfigurationGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, model, true)))
        resultConfigurationGroupNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, model)))
        resultConfigurationGroupNodePopUpMenu.add(new ULCMenuItem(new CreateDefaultResultConfigurationAction(tree, model)))
        resultConfigurationGroupNodePopUpMenu.addSeparator()
        resultConfigurationGroupNodePopUpMenu.add(new ULCMenuItem(new DeleteAllGroupAction(tree, model, "DeleteAllResultTemplates")))

        parameterGroupNodePopUpMenu = new ULCPopupMenu()
        if (UserContext.isStandAlone()) {
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, model, 'ExportAll', false)))
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ExportItemGroupAction(tree, model, 'ExportAll', true)))
        }
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, model, false)))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAction(tree, model, true)))
        if (UserContext.isStandAlone())
            parameterGroupNodePopUpMenu.add(new ULCMenuItem(new ImportAllAction(tree, model, "importAllFromDir")))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, model)))
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new CreateDefaultParameterizationAction(tree, model)))
        parameterGroupNodePopUpMenu.addSeparator()
        parameterGroupNodePopUpMenu.add(new ULCMenuItem(new DeleteAllGroupAction(tree, model, "DeleteAllParameters")))

        simulationGroupNodePopUpMenu = new ULCPopupMenu()
        if (UserContext.isStandAlone())
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
        component.setComponentPopupMenu(resultConfigurationNodePopUpMenu)
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
            component.setComponentPopupMenu(resultConfigurationGroupNodePopUpMenu)
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


    boolean isStandAlone() {
        try {
            return UserContext.isStandAlone()
        } catch (Exception ex) {}
    }

}