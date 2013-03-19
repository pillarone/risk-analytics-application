package org.pillarone.riskanalytics.application.ui.batch.view

import com.canoo.ulc.community.table.server.ULCFixedTable
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.table.ULCTableColumn
import com.ulcjava.base.application.util.Dimension
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel
import org.pillarone.riskanalytics.application.ui.main.model.IRiskAnalyticsModelListener
import org.pillarone.riskanalytics.application.ui.main.view.AbstractView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.batch.BatchRunService
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.main.view.MarkItemAsUnsavedListener

public class BatchView extends NewBatchView {

    ULCTable batches
    BatchRun batchRun
    BatchDataTableModel batchDataTableModel
    ULCButton runBatch
    ULCButton saveButton


    public BatchView(RiskAnalyticsMainModel model, BatchRun batchRun) {
        this.model = model
        this.batchRun = batchRun
        this.batchDataTableModel = new BatchDataTableModel(batchRun)
    }

    public BatchView(BatchUIItem batchUIItem) {
        this(batchUIItem.mainModel, batchUIItem.batchRun)
        this.batchUIItem = batchUIItem

    }

    public void init() {
        batchDataTableModel.init()
        super.init()

    }

    public void initComponents() {
        super.initComponents()
        batchNameTextField.setText(batchRun.name)
        comment.setText(batchRun.comment)
        executionTimeSpinner.setValue(batchRun.executionTime.toDate())
    }




    public void layoutComponents() {
        ULCBoxPane parameterSection = getParameterSectionPane()
        content.add(ULCBoxPane.BOX_LEFT_TOP, parameterSection)
        if (batchDataTableModel.getRowCount() > 0)
            content.add(ULCBoxPane.BOX_EXPAND_TOP, spaceAround(createDomainList(), 5, 0, 0, 0))
        content.add(ULCBoxPane.BOX_LEFT_TOP, getButtonsPane())
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }


    ULCComponent createDomainList() {
        // Workarount for PMO-919: Headings Lost by Undocking Result Window (Tree View)
        //use FixedULCTable instead of ULCTable
        batches = new ULCFixedTable(batchDataTableModel)
        batches.name = "batchesTable"
        BatchTableRenderer batchTableRenderer = new BatchTableRenderer(batchRun: batchRun, mainModel: model)
        batches.getColumnModel().getColumns().each {ULCTableColumn column ->
            column.setHeaderRenderer(new BatchTableHeaderRenderer())
            column.setCellRenderer(batchTableRenderer)
        }
        batches.setSelectionMode(ULCListSelectionModel.SINGLE_SELECTION)
        batches.setShowHorizontalLines(true)

        batches.setAutoResizeMode(ULCTable.AUTO_RESIZE_ALL_COLUMNS);
        batches.selectionModel.addListSelectionListener([valueChanged: {ListSelectionEvent event ->
            ULCListSelectionModel source = (ULCListSelectionModel) event.getSource()
            int index = source.getMinSelectionIndex()
            batchDataTableModel.selectedRun = (index >= 0) ? batchDataTableModel.getSimulationRunAt(index) : null
        }] as IListSelectionListener)
        ULCScrollPane scrollPane = new ULCScrollPane(batches)
        scrollPane.setPreferredSize(new Dimension(500, 350));
        scrollPane.setMinimumSize(new Dimension(500, 150));
        scrollPane.setVerticalScrollBarPolicy(ULCScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane
    }

    public ULCBoxPane getButtonsPane() {
        saveButton = new ULCButton(UIUtils.getText(this.class, "Save"))
        saveButton.setPreferredSize(dimension)

        runBatch = new ULCButton(UIUtils.getText(this.class, "RunBatch"))
        runBatch.setPreferredSize(dimension)
        runBatch.setEnabled(batchDataTableModel.getRowCount() > 0)


        ULCBoxPane buttonPane = new ULCBoxPane(columns: 2, rows: 1)

        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(runBatch, 0, 8, 0, 8))
        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(saveButton, 0, 8, 0, 8))
        buttonPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        return buttonPane
    }

    public void attachListeners() {
        runBatch.addActionListener([actionPerformed: {ActionEvent evt ->
            BatchRun batchToRun = BatchRun.findByName(batchDataTableModel.batchRun.name)
            if (batchToRun && !batchToRun.executed) {
                BatchRunService.getService().runBatch(batchToRun)
            } else {
                new I18NAlert("BatchAlreadyExecuted").show()
            }
        }] as IActionListener)

        saveButton.addActionListener([actionPerformed: {ActionEvent evt ->
            String newName = batchNameTextField.getValue()
            String oldName = batchDataTableModel.batchRun.name
            if (oldName.equals(newName) || validate(newName)) {
                BatchRun.withTransaction {
                    BatchRun batch = BatchRun.findByName(oldName)
                    if (batch) {
                        batch.name = newName
                        batch.executionTime = new DateTime(executionTimeSpinner.getValue().getTime())
                        batch.comment = comment.getText()
                        batch.save()
                        if (!batch.name.equals(oldName)) {
                            notifyItemSaved()
                        }
                    }
                }
            } else {
                new I18NAlert("BatchNotValidName").show()
            }
        }] as IActionListener)

        model.addBatchTableListener batchDataTableModel
    }

    public void addRiskAnalyticsModelListener(IRiskAnalyticsModelListener riskAnalyticsModelListener) {
        batchDataTableModel.addRiskAnalyticsModelListener riskAnalyticsModelListener
    }

    static AbstractView getView(RiskAnalyticsMainModel model, BatchRun batchRun) {
        return batchRun ? new BatchView(model, batchRun) : new NewBatchView(model)
    }

    static AbstractView getView(BatchUIItem batchUIItem) {
        return batchUIItem.batchRun ? new BatchView(batchUIItem) : new NewBatchView(batchUIItem)
    }


}


