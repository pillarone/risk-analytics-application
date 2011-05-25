package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.ULCMenu
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.base.action.GenerateReportAction
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.main.view.CompareSimulationMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.ReportMenu
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.ui.main.action.*

class SimulationNode extends ItemNode {
    //checkBox selected simulations
    boolean display = true
    // flag for hidden/display simulations
    boolean hidden = false

    public SimulationNode(AbstractUIItem simulationUIItem) {
        super(simulationUIItem, false, true)
    }

    public void setUserObject(Object userObject) {
        if (renameable) {
            abstractUIItem.item.rename(userObject)
            setValueAt("${abstractUIItem.item.name}".toString(), 0)
        }
    }

    public ULCPopupMenu getPopupMenu(MainSelectionTableTreeCellRenderer renderer, ULCTableTree tree) {
        if (renderer.popupMenus['simulationNodePopUpMenu']) return renderer.popupMenus['simulationNodePopUpMenu']
        ULCPopupMenu simulationNodePopUpMenu = new ULCPopupMenu()
        simulationNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, abstractUIItem.mainModel)))

        simulationNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree, abstractUIItem.mainModel)))
        simulationNodePopUpMenu.add(new ULCMenuItem(new RenameAction(tree, abstractUIItem.mainModel)))
        ULCMenuItem compareSimulationMenuItem = new CompareSimulationMenuItem(new CompareSimulationsAction(tree, abstractUIItem.mainModel))
        tree.addTreeSelectionListener(compareSimulationMenuItem)
        simulationNodePopUpMenu.add(compareSimulationMenuItem)


        ULCMenu reportsMenu = new ReportMenu("Reports")
        reportsMenu.add(new ULCMenuItem(new GenerateReportAction("Management Summary", tree, abstractUIItem.mainModel)))
        reportsMenu.add(new ULCMenuItem(new GenerateReportAction("Actuary Summary", tree, abstractUIItem.mainModel)))
        tree.addTreeSelectionListener(reportsMenu)
        simulationNodePopUpMenu.add(reportsMenu)
        simulationNodePopUpMenu.addSeparator()
        simulationNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, abstractUIItem.mainModel)))
        renderer.popupMenus['simulationNodePopUpMenu'] = simulationNodePopUpMenu
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


}