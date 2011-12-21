package org.pillarone.riskanalytics.application.ui.pivot.view

import org.pillarone.riskanalytics.application.ui.pivot.model.PivotModel
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.table.ULCTableColumn
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCDialog
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.ULCList
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CustomTableRowHeaderRenderer
import com.ulcjava.base.application.ULCToolBar
import org.pillarone.riskanalytics.application.ui.resultnavigator.StandaloneResultNavigator
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.ResultNavigator
import org.pillarone.riskanalytics.application.ui.pivot.model.CustomTable.CellEditTextField


class PivotView {
    ULCFrame parent
    ULCBoxPane content
    PivotModel pivotModel

    CustomTable customTable
    ResultNavigator resultNavigator

    ULCList rowHeader

    CellEditTextField cellEditTextField

    PivotView() {
        this.pivotModel = new PivotModel()
        initComponents()
    }

    protected void initComponents() {
        content = new ULCBoxPane(true)
        content.setPreferredSize(new Dimension (400,400))

        content.add(ULCBoxPane.BOX_EXPAND_TOP, createToolbar())

        cellEditTextField = new CellEditTextField(pivotModel.customTableModel, customTable)
        content.add (ULCBoxPane.BOX_EXPAND_TOP, cellEditTextField)

        content.add (ULCBoxPane.BOX_EXPAND_EXPAND, createCustomTable())
    }


    private ULCToolBar createToolbar() {
        ULCToolBar toolBar = new ULCToolBar("Select Data", ULCToolBar.HORIZONTAL)

        ULCButton startResultNavigator = new ULCButton("Result Navigator")
        toolBar.add(startResultNavigator)
        startResultNavigator.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                StandaloneResultNavigator res = new StandaloneResultNavigator()
                res.start()
                resultNavigator = res.contents
            }
        })

        return toolBar
    }

    /**
     * Creates the Custom Table and the corresponding Buttons
     */
    ULCTableColumn lastLeftClickedColumn
    public ULCBoxPane createCustomTable() {
        ULCBoxPane pane = new ULCBoxPane(true)

        // Custom Table
        customTable = new CustomTable(pivotModel, this)

        // Column Header Context Menu
        ULCPopupMenu colHeaderPopupMenu = new ULCPopupMenu()
        ULCMenuItem renameColMenuItem = new ULCMenuItem("Rename Column")
        renameColMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                def getName = { return pivotModel.customTableModel.getColumnName(lastLeftClickedColumn.getModelIndex()) }
                def setName = { name -> pivotModel.customTableModel.setColumnName (lastLeftClickedColumn.getModelIndex(), name) }
                createRenameDialog (getName, setName)
            }
        })
        colHeaderPopupMenu.add(renameColMenuItem)
        ULCMenuItem deleteColMenuItem = new ULCMenuItem("Delete Column")
        deleteColMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                pivotModel.customTableModel.deleteCol(lastLeftClickedColumn.getModelIndex())
            }
        })
        colHeaderPopupMenu.add(deleteColMenuItem)
        customTable.getTableHeader().setComponentPopupMenu(colHeaderPopupMenu)

        customTable.getTableHeader().addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (actionEvent.modifiers == ActionEvent.META_MASK) {
                    lastLeftClickedColumn = (ULCTableColumn)actionEvent.source
                }
            }
        })

        // Row Header
        rowHeader = new ULCList(pivotModel.customTableModel.rowHeaderModel)
        rowHeader.setFixedCellWidth(50)
        rowHeader.setFixedCellHeight(customTable.getRowHeight())
        rowHeader.setCellRenderer(new CustomTableRowHeaderRenderer(customTable))

        // Row Header Context Menu
        ULCPopupMenu rowHeaderPopupMenu = new ULCPopupMenu()
        ULCMenuItem renameRowMenuItem = new ULCMenuItem("Rename Row")
        renameRowMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                def getName = { return pivotModel.customTableModel.rowHeaderModel.getElementAt (rowHeader.getSelectedIndex()).toString() }
                def setName = { name -> pivotModel.customTableModel.rowHeaderModel.set (rowHeader.getSelectedIndex(), name) }
                createRenameDialog (getName, setName)
            }
        })
        rowHeaderPopupMenu.add(renameRowMenuItem)
        ULCMenuItem deleteRowMenuItem = new ULCMenuItem("Delete Row")
        deleteRowMenuItem.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                pivotModel.customTableModel.deleteRow(rowHeader.getSelectedIndex())
            }
        })
        rowHeaderPopupMenu.add(deleteRowMenuItem)
        rowHeader.setComponentPopupMenu(rowHeaderPopupMenu)

        // add table to the window
        ULCScrollPane customTableScrollPane = new ULCScrollPane(customTable)
        customTableScrollPane.setPreferredSize(new Dimension(300, 300))
        customTableScrollPane.setRowHeaderView(rowHeader)
        pane.add (ULCBoxPane.BOX_EXPAND_EXPAND, customTableScrollPane)

        // EditMode checkBox
        ULCBoxPane customTableButtonPane = new ULCBoxPane (false)

        ULCCheckBox editModeButton = new ULCCheckBox("Edit Mode")
        editModeButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                pivotModel.customTableModel.editMode = editModeButton.isSelected()
                pivotModel.customTableModel.fireTableDataChanged()
            }
        })
        customTableButtonPane.add (ULCBoxPane.BOX_CENTER_CENTER, editModeButton)

        ULCButton newRowButton = new ULCButton("Insert Row")
        newRowButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                pivotModel.customTableModel.addRow([], "")
            }
        })
        customTableButtonPane.add (ULCBoxPane.BOX_CENTER_CENTER, newRowButton)

        ULCButton newColButton = new ULCButton("Insert Column")
        newColButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                pivotModel.customTableModel.addCol ("")
            }
        })
        customTableButtonPane.add (ULCBoxPane.BOX_CENTER_CENTER, newColButton)
        pane.add (ULCBoxPane.BOX_LEFT_CENTER, customTableButtonPane)

        return pane
    }

    /**
     * creates a Dialog for renaming a column or a row
     */
    public ULCDialog createRenameDialog (Closure getName, Closure setName) {
        ULCDialog renameDialog = new ULCDialog(parent, "Rename...", true)
        ULCBoxPane dialogPane = new ULCBoxPane (true)
        ULCTextField nameTextField = new ULCTextField (getName())
        dialogPane.add(ULCBoxPane.BOX_EXPAND_TOP, nameTextField)

        ULCButton okButton = new ULCButton ("OK")
        okButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                setName (nameTextField.getText())
                renameDialog.setVisible(false);
            }
        })

        dialogPane.add(ULCBoxPane.BOX_EXPAND_TOP, okButton)
        renameDialog.add (dialogPane)
        renameDialog.setVisible(true)

        return renameDialog
    }
}
