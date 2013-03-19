package org.pillarone.riskanalytics.application.ui.resulttemplate.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.base.model.VersionedItemNode
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.item.ResultConfigurationUIItem
import org.pillarone.riskanalytics.application.ui.main.action.*

class ResultConfigurationNode extends VersionedItemNode {

    public ResultConfigurationNode(ResultConfigurationUIItem resultConfigurationUIItem) {
        super(resultConfigurationUIItem, false)
    }

    @Override
    public ULCPopupMenu getPopupMenu( ULCTableTree tree) {
        ULCPopupMenu resultConfigurationNodePopUpMenu = new ULCPopupMenu()
        resultConfigurationNodePopUpMenu.name = "resultConfigurationNodePopUpMenu"
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, abstractUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, abstractUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.addSeparator()
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new RenameAction(tree, abstractUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new SaveAsAction(tree, abstractUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new CreateNewMajorVersion(tree, abstractUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree, abstractUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.addSeparator()
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, abstractUIItem.mainModel)))
        return resultConfigurationNodePopUpMenu

    }
}
