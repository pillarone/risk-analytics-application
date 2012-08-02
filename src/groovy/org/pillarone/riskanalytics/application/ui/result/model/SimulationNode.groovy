package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.main.view.CompareSimulationMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

class SimulationNode extends ItemNode implements IReportableNode {
    //checkBox selected simulations
    boolean display = true
    // flag for hidden/display simulations
    boolean hidden = false
    Map<String, ULCPopupMenu> resultMenus = [:]

    public SimulationNode(AbstractUIItem simulationUIItem) {
        super(simulationUIItem, false, true)
    }

    public void setUserObject(Object userObject) {
        if (renameable) {
            abstractUIItem.item.rename(userObject)
            setValueAt("${abstractUIItem.item.name}".toString(), 0)
        }
    }

    public ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        String modelName = abstractUIItem.model.modelClass.simpleName
        if (resultMenus.containsKey(modelName)) return resultMenus[modelName]
        ULCPopupMenu simulationNodePopUpMenu = new ULCPopupMenu()
        simulationNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, abstractUIItem.mainModel)))

        simulationNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree, abstractUIItem.mainModel)))
        simulationNodePopUpMenu.add(new ULCMenuItem(new CsvExportAction(tree, abstractUIItem.mainModel)))
        simulationNodePopUpMenu.add(new ULCMenuItem(new RenameAction(tree, abstractUIItem.mainModel)))
        ULCMenuItem compareSimulationMenuItem = new CompareSimulationMenuItem(new CompareSimulationsAction(tree, abstractUIItem.mainModel))
        tree.addTreeSelectionListener(compareSimulationMenuItem)
        simulationNodePopUpMenu.add(compareSimulationMenuItem)
        simulationNodePopUpMenu.addSeparator()
        simulationNodePopUpMenu.add(new ULCMenuItem(new TagsAction(tree, abstractUIItem.mainModel)))
        addReportMenus(simulationNodePopUpMenu, tree, true)
        simulationNodePopUpMenu.addSeparator()
        simulationNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, abstractUIItem.mainModel)))
        resultMenus[modelName] = simulationNodePopUpMenu

        return simulationNodePopUpMenu
    }

    public String getToolTip() {
        StringBuilder builder = new StringBuilder("<html><div style='width:100px;'>")
        builder.append(UIUtils.getText(this.class, "numberOfIterations") + ": " + abstractUIItem.item.numberOfIterations)
        if (abstractUIItem.item.comment)
            builder.append("<br>" + UIUtils.getText(MainSelectionTableTreeCellRenderer.class, "comment") + ": " + abstractUIItem.item.comment)
        builder.append("</div></html>")
        return builder.toString()
    }

    List<Class> modelsToReportOn() {
        return [abstractUIItem.model.getClass()]
    }

    List<ModellingItem> modellingItemsForReport() {
        return [((ModellingItem) ((ModellingUIItem) abstractUIItem).item)]
    }
}