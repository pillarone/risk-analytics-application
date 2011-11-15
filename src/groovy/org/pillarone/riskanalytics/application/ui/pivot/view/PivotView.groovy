package org.pillarone.riskanalytics.application.ui.pivot.view

import com.ulcjava.base.application.ULCBoxPane
import org.pillarone.riskanalytics.application.ui.pivot.model.PivotModel

import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent

import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent

import com.ulcjava.base.application.ULCListSelectionModel
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.UlcUtilities
import org.pillarone.riskanalytics.application.ui.pivot.model.TreeStructureTableModel
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import org.pillarone.riskanalytics.application.ui.pivot.model.PreviewNode
import com.ulcjava.base.application.dnd.TransferHandler
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.dnd.Transferable
import com.ulcjava.base.application.dnd.DnDTreeData
import com.ulcjava.base.application.dnd.DataFlavor
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.dnd.DnDTableTreeData


class PivotView {

    ULCBoxPane content
    PivotModel pivotModel
    ULCTable dimensionTable
    ULCTable coordinateTable

    ULCTableTree previewTableTree

    ULCTable newTable

    PivotView(PivotModel pivotModel) {
        this.pivotModel = pivotModel
        initComponents()
    }

    protected void initComponents() {
        content = new ULCBoxPane(false)
        content.setPreferredSize(new Dimension (400,400))

        ULCBoxPane col1 = new ULCBoxPane (true)

        // TreeStructure
        ULCBoxPane treeStructurePane = new ULCBoxPane(4, 1)

        // Dimensions Table
        dimensionTable = new ULCTable(pivotModel.dimensionTableModel)
        dimensionTable.getSelectionModel().addListSelectionListener(new DimensionTableListSelectionListener())
        dimensionTable.getColumnModel().getColumn(0).setPreferredWidth(20)
        dimensionTable.getColumnModel().getColumn(1).setPreferredWidth(100)
        dimensionTable.setSelectionMode(ULCListSelectionModel.SINGLE_SELECTION)
        dimensionTable.setRowSelectionAllowed(true)
        dimensionTable.setPreferredScrollableViewportSize(new Dimension(120, 100))
        dimensionTable.setShowVerticalLines(false)
        dimensionTable.setTableHeader (null)

        ULCScrollPane dimensionTableScrollPane = new ULCScrollPane(dimensionTable)
        dimensionTableScrollPane.setPreferredSize(new Dimension(140, 100))
        treeStructurePane.add (ULCBoxPane.BOX_EXPAND_EXPAND, dimensionTableScrollPane)

        treeStructurePane.add (ULCBoxPane.BOX_LEFT_EXPAND, createMoveButtons(dimensionTable))

        // Coordinates Table
        int dimension_id = pivotModel.dimensionTableModel.getID(0);
        coordinateTable = new ULCTable(pivotModel.coordinateTableModels[dimension_id])
        coordinateTable.getColumnModel().getColumn(0).setPreferredWidth(20)
        coordinateTable.getColumnModel().getColumn(1).setPreferredWidth(100)
        coordinateTable.setSelectionMode(ULCListSelectionModel.SINGLE_SELECTION)
        coordinateTable.setRowSelectionAllowed(true)
        coordinateTable.setPreferredScrollableViewportSize(new Dimension(120, 100))
        coordinateTable.setShowVerticalLines(false)
        coordinateTable.setTableHeader (null)

        ULCScrollPane coordinateTableScrollPane = new ULCScrollPane(coordinateTable)
        coordinateTableScrollPane.setPreferredSize(new Dimension(140, 100))
        treeStructurePane.add (ULCBoxPane.BOX_EXPAND_EXPAND, coordinateTableScrollPane)

        treeStructurePane.add (ULCBoxPane.BOX_LEFT_EXPAND, createMoveButtons(coordinateTable))

        col1.add (ULCBoxPane.BOX_LEFT_TOP, treeStructurePane)


        // Preview Table Tree
        previewTableTree = new ULCTableTree(pivotModel.previewTableTreeModel);
        previewTableTree.setRootVisible(false);
        previewTableTree.setAutoResizeMode(ULCTableTree.AUTO_RESIZE_ALL_COLUMNS);

        previewTableTree.setDragEnabled (true)

        ULCScrollPane previewTreeScrollPane = new ULCScrollPane(previewTableTree)
        previewTreeScrollPane.setPreferredSize(new Dimension(320, 200))

        col1.add (ULCBoxPane.BOX_LEFT_EXPAND, previewTreeScrollPane)

        // Refresh button
        ULCButton refreshButton = new ULCButton("Refresh Preview")
        refreshButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                pivotModel.updatePreviewTree()
                previewTableTree.expandAll()
            }
        })
        col1.add (ULCBoxPane.BOX_CENTER_CENTER, refreshButton)

        content.add (ULCBoxPane.BOX_LEFT_EXPAND, col1)


        ULCBoxPane col2 = new ULCBoxPane (true)

        newTable = new ULCTable(pivotModel.newTableModel)
        newTable.setSelectionMode(ULCListSelectionModel.SINGLE_SELECTION)
        newTable.setRowSelectionAllowed(true)
        newTable.setPreferredScrollableViewportSize(new Dimension(300, 300))
        newTable.setTableHeader (null)

        newTable.setTransferHandler(new MyTransferHandler())

        ULCScrollPane newTableScrollPane = new ULCScrollPane(newTable)
        newTableScrollPane.setPreferredSize(new Dimension(300, 300))
        col2.add (ULCBoxPane.BOX_EXPAND_EXPAND, newTableScrollPane)
        
        content.add (ULCBoxPane.BOX_EXPAND_EXPAND, col2)
    }

    public class MyTransferHandler extends TransferHandler {
        @Override
        boolean importData(ULCComponent ulcComponent, Transferable transferable) {
            TreePath[] treePaths = ((DnDTableTreeData)transferable.getTransferData(DataFlavor.DRAG_FLAVOR)).getTreePaths()

            for (TreePath treePath in treePaths) {
                PreviewNode node = treePath.getLastPathComponent();

                pivotModel.newTableModel.addRow ([node.getValueAt(0), node.getValueAt(1)])
                pivotModel.newTableModel.fireTableRowsInserted(pivotModel.newTableModel.rowCount-1, pivotModel.newTableModel.rowCount-1)
            }
        }

        @Override
        void exportDone(ULCComponent ulcComponent, Transferable transferable, int i) {
        }
    }

    public ULCBoxPane createMoveButtons (ULCTable table) {
        ULCBoxPane pane = new ULCBoxPane(1, 4)

        UIUtils.ICON_DIRECTORY
        ULCButton dimensionTopButton = new ULCButton(UIUtils.getIcon ("pivot_top.png"))
        dimensionTopButton.setPreferredSize(new Dimension (16,16))
        dimensionTopButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (table.selectedRow < 0) return
                if ((table.getModel() as TreeStructureTableModel).moveToTop(table.selectedRow)) {
                    table.getSelectionModel().setSelectionInterval(0, 0)

                    table.scrollCellToVisible(dimensionTable.selectedRow, 0)
                }
            }
        })
        pane.add (0, 0, ULCBoxPane.BOX_CENTER_TOP, dimensionTopButton)

        ULCButton dimensionUpButton = new ULCButton(UIUtils.getIcon ("pivot_up.png"))
        dimensionUpButton.setPreferredSize(new Dimension (16,16))
        dimensionUpButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (table.selectedRow < 0) return
                if ((table.getModel() as TreeStructureTableModel).moveUp(table.selectedRow)) {
                    table.getSelectionModel().setSelectionInterval(table.selectedRow-1, table.selectedRow-1)

                    table.scrollCellToVisible(table.selectedRow, 0)
                }
            }
        })
        pane.add (0, 1, ULCBoxPane.BOX_CENTER_TOP, dimensionUpButton)

        ULCButton dimensionDownButton = new ULCButton(UIUtils.getIcon ("pivot_down.png"))
        dimensionDownButton.setPreferredSize(new Dimension (16,16))
        dimensionDownButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (table.selectedRow < 0) return
                if ((table.getModel() as TreeStructureTableModel).moveDown(table.selectedRow)) {
                    table.getSelectionModel().setSelectionInterval(table.selectedRow+1, table.selectedRow+1)

                    table.scrollCellToVisible(table.selectedRow, 0)
                }
            }
        })
        pane.add (0, 1, ULCBoxPane.BOX_CENTER_BOTTOM, dimensionDownButton)

        ULCButton dimensionBottomButton = new ULCButton(UIUtils.getIcon ("pivot_bottom.png"))
        dimensionBottomButton.setPreferredSize(new Dimension (16,16))
        dimensionBottomButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (table.selectedRow < 0) return
                if ((table.getModel() as TreeStructureTableModel).moveToBottom(table.selectedRow)) {
                    table.getSelectionModel().setSelectionInterval(table.rowCount-1, table.rowCount-1)

                    table.scrollCellToVisible(table.selectedRow, 0)
                }
            }
        })
        pane.add (0, 0, ULCBoxPane.BOX_CENTER_BOTTOM, dimensionBottomButton)

        return pane
    }

    class DimensionTableListSelectionListener implements IListSelectionListener {
        void valueChanged(ListSelectionEvent listSelectionEvent) {
            int dimension_id = pivotModel.dimensionTableModel.getID(dimensionTable.selectedRow);
            coordinateTable.setModel(pivotModel.coordinateTableModels[dimension_id])
            coordinateTable.getColumnModel().getColumn(0).setPreferredWidth(20)
            coordinateTable.getColumnModel().getColumn(1).setPreferredWidth(100)
        }
    }
}
