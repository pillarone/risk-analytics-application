package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.ULCToolBar
import org.pillarone.riskanalytics.application.ui.resultnavigator.StandaloneResultNavigator
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.ResultNavigator
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableModel

/**
 * The View which contains the CustomTable, and its other elements (cellEditTextField)
 *
 * @author ivo.nussbaumer
 */
public class CustomTableView {
    private ULCFrame parent
    private ULCBoxPane content
    private CustomTableModel customTableModel

    private CustomTable customTable
    private ResultNavigator resultNavigator

    private CellEditTextField cellEditTextField
    private DataCellEditPane dataCellEditPane

    /**
     * Constructor
     */
    public CustomTableView() {
        this.customTableModel = new CustomTableModel(new LinkedList<List<Object>>())
        initComponents()
    }

    /**
     * Initalize the Compontents
     */
    private void initComponents() {
        content = new ULCBoxPane(true)
        content.setPreferredSize(new Dimension (400,400))

        ULCToolBar toolbar = createToolbar()
        ULCBoxPane customTablePane = createCustomTable()
        cellEditTextField = new CellEditTextField(customTable)

        dataCellEditPane = new DataCellEditPane(customTable)

        content.add(ULCBoxPane.BOX_EXPAND_TOP, toolbar)
        content.add (ULCBoxPane.BOX_EXPAND_TOP, cellEditTextField)
        content.add (ULCBoxPane.BOX_EXPAND_TOP, dataCellEditPane)
        content.add (ULCBoxPane.BOX_EXPAND_EXPAND, customTablePane)
    }

    /**
     * Creates the toolbar
     * @return the toolbar
     */
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
    private ULCBoxPane createCustomTable() {
        ULCBoxPane pane = new ULCBoxPane(true)

        // Create Custom Table
        customTable = new CustomTable(customTableModel, this)

        // Create CustomTablePane (which is a ScrollPane with a row header)
        CustomTablePane customTablePane = new CustomTablePane(customTable)

        // add CustomTablePane to the window
        pane.add (ULCBoxPane.BOX_EXPAND_EXPAND, customTablePane)

        // EditMode checkBox
        ULCBoxPane customTableButtonPane = new ULCBoxPane (false)

        ULCCheckBox editModeButton = new ULCCheckBox("Edit Mode")
        editModeButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                customTableModel.editMode = editModeButton.isSelected()
                customTableModel.fireTableDataChanged()
            }
        })
        customTableButtonPane.add (ULCBoxPane.BOX_CENTER_CENTER, editModeButton)

        ULCButton newRowButton = new ULCButton("Insert Row")
        newRowButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                customTableModel.addRow([])
            }
        })
        customTableButtonPane.add (ULCBoxPane.BOX_CENTER_CENTER, newRowButton)

        ULCButton newColButton = new ULCButton("Insert Column")
        newColButton.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                customTableModel.addCol ()
            }
        })
        customTableButtonPane.add (ULCBoxPane.BOX_CENTER_CENTER, newColButton)
        pane.add (ULCBoxPane.BOX_LEFT_CENTER, customTableButtonPane)

        return pane
    }
}
