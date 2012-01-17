package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.ULCToolBar
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.ResultNavigator
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableModel
import com.ulcjava.base.shared.IWindowConstants
import com.ulcjava.base.application.UlcUtilities

/**
 * The View which contains the CustomTable, and its other elements (cellEditTextField)
 *
 * @author ivo.nussbaumer
 */
public class CustomTableView {
    public ULCFrame parent
    private ULCBoxPane content
    private CustomTableModel customTableModel

    public CustomTable customTable
    private ResultNavigator resultNavigator

    public CellEditTextField cellEditTextField
    public DataCellEditPane dataCellEditPane

    /**
     * Constructor
     */
    public CustomTableView() {
        this([[""]])
    }

    public CustomTableView(List<List<Object>> data) {
        this.customTableModel = new CustomTableModel(data)
        this.customTableModel.setNumberCols(10)
        this.customTableModel.setNumberRows(10)
        initComponents()
    }

    /**
     * Initalize the Compontents
     */
    private void initComponents() {
        content = new ULCBoxPane(2, 3)
        content.setPreferredSize(new Dimension (400,400))

        // Create Custom Table
        customTable = new CustomTable(customTableModel, this)
        // Create CustomTablePane (which is a ScrollPane with a row header)
        CustomTablePane customTablePane = new CustomTablePane(customTable)

        ULCToolBar toolbar = createToolbar()
        cellEditTextField = new CellEditTextField(customTable)
        dataCellEditPane = new DataCellEditPane(this)

        //           col  row  hspan  vspan
        content.set (0,   0,   2,     1,    ULCBoxPane.BOX_EXPAND_TOP, toolbar)
        content.set (0,   1,   2,     1,    ULCBoxPane.BOX_EXPAND_TOP, cellEditTextField)
        content.set (0,   2,   1,     1,    ULCBoxPane.BOX_EXPAND_EXPAND, customTablePane)
        content.set (1,   2,   1,     1,    ULCBoxPane.BOX_RIGHT_TOP, dataCellEditPane)
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
                ULCFrame frame = new ULCFrame()
                frame.defaultCloseOperation = IWindowConstants.DISPOSE_ON_CLOSE
                frame.setSize(1000, 750)
                frame.setExtendedState(ULCFrame.NORMAL)
                frame.toFront()
                frame.locationRelativeTo = UlcUtilities.getWindowAncestor(parent)

                resultNavigator = new ResultNavigator()
                frame.contentPane = resultNavigator.contentView

                frame.visible = true
            }
        })

        return toolBar
    }

    ULCBoxPane getContent() {
        return content
    }
}
