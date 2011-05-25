package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.action.SimulationAction
import org.pillarone.riskanalytics.application.ui.main.view.MainSelectionTableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.main.view.item.ModelUIItem

class ModelNode extends ItemNode {

    public ModelNode(ModelUIItem modelUIItem) {
        super(modelUIItem, false, false)
    }

    @Override
    public ULCPopupMenu getPopupMenu(MainSelectionTableTreeCellRenderer renderer, ULCTableTree tree) {
        if (renderer.popupMenus['modelNodePopUpMenu']) return renderer.popupMenus['modelNodePopUpMenu']
        ULCPopupMenu modelNodePopUpMenu = new ULCPopupMenu()
        modelNodePopUpMenu.add(new ULCMenuItem(new SimulationAction(tree, abstractUIItem.mainModel)))
        renderer.popupMenus['modelNodePopUpMenu'] = modelNodePopUpMenu
        return modelNodePopUpMenu
    }


}