package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.ULCToolBar
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.ResultNavigator
import org.pillarone.riskanalytics.application.ui.customtable.model.CustomTableModel
import com.ulcjava.base.shared.IWindowConstants
import com.ulcjava.base.application.UlcUtilities
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * The View which contains the CustomTable, and its other elements (cellEditTextField)
 *
 * @author ivo.nussbaumer
 */
public class CustomTableView {
    public ULCFrame parent
    private ULCBoxPane content
    public CustomTableModel customTableModel

    public CustomTable customTable
    private ResultNavigator resultNavigator

    public CellEditTextField cellEditTextField
    public DataCellEditPane dataCellEditPane

    private SimulationRun simulationRun

    /**
     * Constructor
     */
    public CustomTableView(SimulationRun simulationRun) {
        this.simulationRun = simulationRun
        this.customTableModel = new CustomTableModel([[""]])
        this.customTableModel.setNumberCols(10)
        this.customTableModel.setNumberRows(10)
        initComponents()
    }

    public CustomTableView(List<List<Object>> data, SimulationRun simulationRun) {
        this.simulationRun = simulationRun
        this.customTableModel = new CustomTableModel(data)
        initComponents()
    }

    /**
     * Initialize the Components
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

        //           col  row  hSpan  vSpan
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
                frame.setIconImage(UIUtils.getIcon("application.png"))
                frame.locationRelativeTo = UlcUtilities.getWindowAncestor(parent)

                if (CustomTableView.this.simulationRun != null) {
                    resultNavigator = new ResultNavigator(CustomTableView.this.simulationRun)
                } else {
                    resultNavigator = new ResultNavigator()
                }
                frame.contentPane = resultNavigator.contentView

                frame.visible = true
            }
        })


        toolBar.add new ULCButton(new PrecisionAction(this.customTable, -1, "reducePrecision"))
        toolBar.add new ULCButton(new PrecisionAction(this.customTable, +1, "increasePrecision"))

        return toolBar
    }

    ULCBoxPane getContent() {
        return content
    }

     private class PrecisionAction extends ResourceBasedAction {
        CustomTable table
        CustomTableModel tableModel
        int adjustment

        public PrecisionAction(CustomTable table, int adjustment, String label) {
            super(label)
            this.table = table
            this.tableModel = (CustomTableModel)table.model
            this.adjustment = adjustment
        }

        public void doActionPerformed(ActionEvent event) {
            for (int row = table.getSelectedRow(); row <= table.getSelectionModel().getMaxSelectionIndex(); row++) {
                for (int col = table.getSelectedColumn(); col <= table.getColumnModel().getSelectionModel().getMaxSelectionIndex(); col++) {
                    tableModel.adjustPrecision(row, col, adjustment)
                }
            }
        }
    }
}
