package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.reports.IReportableNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.main.view.EnabledCheckingMenuItem
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationResultUIItem
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

@CompileStatic
class SimulationNode extends ItemNode implements IReportableNode {
    //checkBox selected simulations
    boolean display = true
    // flag for hidden/display simulations
    boolean hidden = false

    SimulationNode(SimulationResultUIItem simulationUIItem) {
        super(simulationUIItem, false)
    }

    @Override
    SimulationResultUIItem getItemNodeUIItem() {
        return super.itemNodeUIItem as SimulationResultUIItem
    }

    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        ULCPopupMenu simulationNodePopUpMenu = new ULCPopupMenu()
        simulationNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree)))
        simulationNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree)))
        simulationNodePopUpMenu.add(new ULCMenuItem(new CsvExportAction(tree)))
        simulationNodePopUpMenu.add(new EnabledCheckingMenuItem(new RenameAction(tree))) //PMO-2764
        simulationNodePopUpMenu.add(new EnabledCheckingMenuItem(new CompareSimulationsAction(tree)))
        simulationNodePopUpMenu.addSeparator()
        simulationNodePopUpMenu.add(new ULCMenuItem(new TagsAction(tree)))
        addReportMenus(simulationNodePopUpMenu, tree, true)
        simulationNodePopUpMenu.addSeparator()
        simulationNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree)))
        simulationNodePopUpMenu.addSeparator()
        simulationNodePopUpMenu.add(new ULCMenuItem(new ShowPropertiesAction(tree)))
        simulationNodePopUpMenu.addSeparator()
        simulationNodePopUpMenu.add(new ULCMenuItem(new UploadSimulationAction(tree)))
        return simulationNodePopUpMenu
    }

    String getToolTip() {
        StringBuilder builder = new StringBuilder("<html><div style='width:100px;'>")
        builder.append(UIUtils.getText(this.class, "numberOfIterations") + ": " + itemNodeUIItem.item.numberOfIterations)
        if (itemNodeUIItem.item.comment) {
            builder.append("<br>" + UIUtils.getText(MainSelectionTableTreeCellRenderer.class, "comment") + ": " + itemNodeUIItem.item.comment)
        }
        builder.append("</div></html>")
        return builder.toString()
    }

    List<Class> modelsToReportOn() {
        return [itemNodeUIItem.model.class]
    }

    List<ModellingItem> modellingItemsForReport() {
        return [itemNodeUIItem.item]
    }
}