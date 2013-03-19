package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.IPopupMenuListener
import com.ulcjava.base.application.event.PopupMenuEvent
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class EnableParallelCoordinatesChart implements IPopupMenuListener {
    ULCMenuItem menuItem
    def rowHeaderTableTree

    public EnableParallelCoordinatesChart(ULCMenuItem menuItem, def rowHeaderTableTree) {
        this.menuItem = menuItem
        this.@rowHeaderTableTree = rowHeaderTableTree
    }

    public void popupMenuHasBecomeVisible(PopupMenuEvent event) {
        def paths = rowHeaderTableTree.selectedPaths.lastPathComponent
        List nodes = paths.findAll {it instanceof ResultTableTreeNode}
        if (nodes.size() > 1) {
            menuItem.enabled = true
        } else {
            menuItem.enabled = false
        }
    }

    public void popupMenuHasBecomeInvisible(PopupMenuEvent event) {}

    public void popupMenuCanceled(PopupMenuEvent event) {}
}
