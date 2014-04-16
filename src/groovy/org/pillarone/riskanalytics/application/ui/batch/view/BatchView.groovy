package org.pillarone.riskanalytics.application.ui.batch.view

import com.canoo.ulc.community.table.server.ULCFixedTable
import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.table.ULCTableColumn
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel
import org.pillarone.riskanalytics.application.ui.main.model.IRiskAnalyticsModelListener
import org.pillarone.riskanalytics.application.ui.main.view.AbstractView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch

public class BatchView extends NewBatchView {

    ULCTable batches
    Batch batch
    BatchDataTableModel batchDataTableModel
    ULCButton runBatch
    ULCButton saveButton

    private BatchView(RiskAnalyticsMainModel model, Batch batch) {
        super(model)
        this.batch = batch
        this.batchDataTableModel = new BatchDataTableModel(batch)
    }

    BatchView(BatchUIItem batchUIItem) {
        this(batchUIItem.mainModel, batchUIItem.item)
        this.batchUIItem = batchUIItem
    }

    void init() {
        batchDataTableModel.init()
        super.init()

    }

    public void initComponents() {
        super.initComponents()
        batchNameTextField.text = batch.name
        batchNameTextField.enabled = false
        comment.text = batch.comment
        executionTimeSpinner.value = new Date()
    }


    public void layoutComponents() {
        ULCBoxPane parameterSection = parameterSectionPane
        content.add(ULCBoxPane.BOX_LEFT_TOP, parameterSection)
        if (batchDataTableModel.rowCount > 0) {
            content.add(ULCBoxPane.BOX_EXPAND_TOP, spaceAround(createDomainList(), 5, 0, 0, 0))
        }
        content.add(ULCBoxPane.BOX_LEFT_TOP, buttonsPane)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }


    ULCComponent createDomainList() {
        // Workarount for PMO-919: Headings Lost by Undocking Result Window (Tree View)
        //use FixedULCTable instead of ULCTable
        batches = new ULCFixedTable(batchDataTableModel)
        batches.name = "batchesTable"
        BatchTableRenderer batchTableRenderer = new BatchTableRenderer(mainModel: model)
        batches.columnModel.columns.each { ULCTableColumn column ->
            column.headerRenderer = new BatchTableHeaderRenderer()
            column.cellRenderer = batchTableRenderer
        }
        batches.selectionMode = ULCListSelectionModel.SINGLE_SELECTION
        batches.showHorizontalLines = true

        batches.autoResizeMode = ULCTable.AUTO_RESIZE_ALL_COLUMNS;
        batches.selectionModel.addListSelectionListener([valueChanged: { ListSelectionEvent event ->
            ULCListSelectionModel source = (ULCListSelectionModel) event.source
            int index = source.minSelectionIndex
            batchDataTableModel.selectedRun = (index >= 0) ? batchDataTableModel.getSimulationAt(index) : null
        }] as IListSelectionListener)
        ULCScrollPane scrollPane = new ULCScrollPane(batches)
        scrollPane.preferredSize = new Dimension(500, 350);
        scrollPane.minimumSize = new Dimension(500, 150);
        scrollPane.verticalScrollBarPolicy = ULCScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
        return scrollPane
    }

    public ULCBoxPane getButtonsPane() {
        saveButton = new ULCButton(UIUtils.getText(this.class, "Save"))
        saveButton.preferredSize = dimension

        runBatch = new ULCButton(UIUtils.getText(this.class, "RunBatch"))
        runBatch.preferredSize = dimension
        runBatch.enabled = batchDataTableModel.rowCount > 0


        ULCBoxPane buttonPane = new ULCBoxPane(columns: 2, rows: 1)

        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(runBatch, 0, 8, 0, 8))
        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(saveButton, 0, 8, 0, 8))
        buttonPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        return buttonPane
    }

    public void attachListeners() {
        runBatch.addActionListener([actionPerformed: { ActionEvent evt ->
            BatchRun batchToRun = BatchRun.findByName(batchDataTableModel.batch.name)
            if (batchToRun && !batchToRun.executed) {
                BatchRunService.service.runBatch(batchDataTableModel.batch)
            } else {
                new I18NAlert("BatchAlreadyExecuted").show()
            }
        }] as IActionListener)

        saveButton.addActionListener([actionPerformed: { ActionEvent evt ->
            String newName = batchNameTextField.value
            String oldName = batchDataTableModel.batch.name
            if (oldName.equals(newName) || validate(newName)) {
                BatchRun.withTransaction {
                    BatchRun batch = BatchRun.findByName(oldName)
                    if (batch) {
                        batch.name = newName
                        batch.comment = comment.text
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

    static AbstractView getView(BatchUIItem batchUIItem) {
        return (batchUIItem.item.name == BatchUIItem.NEWBATCH) ? new NewBatchView(batchUIItem) : new BatchView(batchUIItem)
    }
}


