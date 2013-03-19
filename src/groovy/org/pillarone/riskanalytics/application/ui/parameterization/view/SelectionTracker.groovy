package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.event.ITableTreeModelListener
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TableTreeModelEvent
import com.ulcjava.base.application.event.TreeSelectionEvent
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.shared.UlcEventCategories
import com.ulcjava.base.shared.UlcEventConstants

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTracker implements ITableTreeModelListener, ITreeSelectionListener {

    ULCFixedColumnTableTree tableTree
    TreePath selectedPath
    int selectedColumn




    public SelectionTracker(ULCFixedColumnTableTree tableTree) {
        this.tableTree = tableTree
        tableTree.rowHeaderTableTree.model.addTableTreeModelListener this
        tableTree.rowHeaderTableTree.selectionModel.addTreeSelectionListener this
        tableTree.rowHeaderTableTree.selectionModel.setEventDeliveryMode(UlcEventCategories.SELECTION_CHANGED_EVENT_CATEGORY, UlcEventConstants.ASYNCHRONOUS_MODE)
        tableTree.rowHeaderTableTree.setEventDeliveryMode(UlcEventCategories.SELECTION_CHANGED_EVENT_CATEGORY, UlcEventConstants.ASYNCHRONOUS_MODE)
        tableTree.viewPortTableTree.selectionModel.addTreeSelectionListener this
        tableTree.viewPortTableTree.selectionModel.setEventDeliveryMode(UlcEventCategories.SELECTION_CHANGED_EVENT_CATEGORY, UlcEventConstants.ASYNCHRONOUS_MODE)
        tableTree.viewPortTableTree.setEventDeliveryMode(UlcEventCategories.SELECTION_CHANGED_EVENT_CATEGORY, UlcEventConstants.ASYNCHRONOUS_MODE)
    }

    public void tableTreeStructureChanged(TableTreeModelEvent tableTreeModelEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void tableTreeNodeStructureChanged(TableTreeModelEvent tableTreeModelEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void tableTreeNodesInserted(TableTreeModelEvent tableTreeModelEvent) {
        selectedAffectedNode(tableTreeModelEvent)
    }

    public void tableTreeNodesRemoved(TableTreeModelEvent tableTreeModelEvent) {
        selectedAffectedNode(tableTreeModelEvent)
    }

    private void selectedAffectedNode(TableTreeModelEvent tableTreeModelEvent) {
        TreePath parentPath = tableTreeModelEvent.treePath
        TreePath scrollingPath = parentPath.lastPathComponent.childCount > 0 ? parentPath.pathByAddingChild(parentPath.lastPathComponent.getChildAt(-1)) : parentPath
        TreePath selectionPath = parentPath.lastPathComponent.childCount > 0 ? parentPath.pathByAddingChild(parentPath.lastPathComponent.getChildAt(0)) : parentPath
        tableTree.expandPath parentPath
        def yPosition = tableTree.getVerticalScrollBar().getPosition()
        if (selectedColumn > 0) {
            tableTree.viewPortTableTree.scrollCellToVisible scrollingPath, selectedColumn - 1
            tableTree.viewPortTableTree.selectionModel.setSelectionPath(selectionPath)

        } else {
            tableTree.rowHeaderTableTree.scrollCellToVisible scrollingPath, 0
            tableTree.rowHeaderTableTree.selectionModel.setSelectionPath(selectionPath)
        }
        restoreColumnSelection()
        tableTree.getVerticalScrollBar().setPosition(yPosition)
    }

    private restoreColumnSelection() {
        if (selectedColumn == 0) {
            tableTree.rowHeaderTableTree.setColumnSelectionInterval(selectedColumn, selectedColumn)
        } else {
            tableTree.viewPortTableTree.setColumnSelectionInterval(selectedColumn - 1, selectedColumn - 1)
        }
    }

    public void tableTreeNodesChanged(TableTreeModelEvent tableTreeModelEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void valueChanged(TreeSelectionEvent event) {

        def currentSelection

        event.paths.each {pathsElement ->
            if (event.isAddedPath(pathsElement)) {
                currentSelection = pathsElement
            }
        }
        if (currentSelection) {
            selectedPath = currentSelection

            int col = tableTree.rowHeaderTableTree.getSelectedColumn()

            if (col >= 0) {
                selectedColumn = col
            } else {
                selectedColumn = tableTree.viewPortTableTree.getSelectedColumn() + 1
            }
        }

    }


}
