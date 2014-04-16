package org.pillarone.riskanalytics.application.ui.resulttemplate.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.ModellingItemNode
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.main.view.item.ResultConfigurationUIItem

@CompileStatic
class ResultConfigurationNode extends ModellingItemNode {

    ResultConfigurationNode(ResultConfigurationUIItem resultConfigurationUIItem) {
        super(resultConfigurationUIItem, false)
    }

    @Override
    ULCPopupMenu getPopupMenu(ULCTableTree tree) {
        ULCPopupMenu resultConfigurationNodePopUpMenu = new ULCPopupMenu()
        resultConfigurationNodePopUpMenu.name = "resultConfigurationNodePopUpMenu"
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new OpenItemAction(tree, itemNodeUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, itemNodeUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.addSeparator()
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new RenameAction(tree, itemNodeUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new SaveAsAction(tree, itemNodeUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new CreateNewMajorVersion(tree, itemNodeUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new ExportItemAction(tree, itemNodeUIItem.mainModel)))
        resultConfigurationNodePopUpMenu.addSeparator()
        resultConfigurationNodePopUpMenu.add(new ULCMenuItem(new DeleteAction(tree, itemNodeUIItem.mainModel)))
        return resultConfigurationNodePopUpMenu
    }
}
