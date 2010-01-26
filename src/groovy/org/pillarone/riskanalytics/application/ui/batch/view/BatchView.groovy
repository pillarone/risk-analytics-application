package org.pillarone.riskanalytics.application.ui.batch.view

import org.pillarone.riskanalytics.core.output.batch.BatchRunner

import com.ulcjava.base.application.ULCSpinner.ULCDateEditor
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.util.Dimension
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.time.FastDateFormat
import org.pillarone.riskanalytics.application.ui.batch.model.BatchDataTableModel
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import com.ulcjava.base.application.*

/**
 * @author fouad jaada
 */

public class BatchView extends NewBatchView {

    ULCTable batches
    BatchRun batchRun
    BatchDataTableModel batchDataTableModel
    ULCButton runBatch
    ULCButton saveButton


    public BatchView(P1RATModel model, BatchRun batchRun) {
        this.model = model
        this.batchRun = batchRun
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void initComponents() {
        super.initComponents()
        batchNameTextField.setText(batchRun.name)
        comment.setText(batchRun.comment)
        executionTimeSpinner.setValue(batchRun.executionTime)
    }




    protected void layoutComponents() {
        ULCBoxPane parameterSection = getParameterSectionPane()
        content.add(ULCBoxPane.BOX_LEFT_TOP, parameterSection)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, spaceAround(createDomainList(), 5, 0, 0, 0))
        content.add(ULCBoxPane.BOX_LEFT_TOP, getButtonsPane())
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }


    ULCComponent createDomainList() {
        batchDataTableModel = new BatchDataTableModel(batchRun)
        batches = new ULCTable(batchDataTableModel)
        int columns = batches.getColumnCount()
        BatchTableRenderer batchTableRenderer = new BatchTableRenderer(batchRun: batchRun)
        columns.times {int columnIndex ->
            batches.columnModel.getColumn(columnIndex).setHeaderRenderer(new BatchTableHeaderRenderer())
            batches.columnModel.getColumn(columnIndex).setCellRenderer(batchTableRenderer)
        }
        batches.setSelectionMode(ULCListSelectionModel.SINGLE_SELECTION)
        batches.setShowHorizontalLines(true)

        batches.setAutoResizeMode(ULCTable.AUTO_RESIZE_ALL_COLUMNS);
        batches.selectionModel.addListSelectionListener([valueChanged: {ListSelectionEvent event ->
            ULCListSelectionModel source = (ULCListSelectionModel) event.getSource()
            int index = source.getMinSelectionIndex()
            batchDataTableModel.selectedRun = (index >= 0) ? batchRun.batchRunService.getSimulationRunAt(batchRun, index) : null
        }] as IListSelectionListener)
        return new ULCScrollPane(batches)
    }

    public ULCBoxPane getButtonsPane() {
        saveButton = new ULCButton(UIUtils.getText(this.class, "Save"))
        saveButton.setPreferredSize(dimension)

        runBatch = new ULCButton(UIUtils.getText(this.class, "RunBatch"))
        runBatch.setPreferredSize(dimension)


        ULCBoxPane buttonPane = new ULCBoxPane(columns: 2, rows: 1)

        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(runBatch, 0, 8, 0, 8))
        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(saveButton, 0, 8, 0, 8))
        buttonPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        return buttonPane
    }

    protected void attachListeners() {
        runBatch.addActionListener([actionPerformed: {ActionEvent evt ->
            BatchRun batchToRun = BatchRun.findByName(batchDataTableModel.batchRun.name)
            if (!batchToRun.executed) {
                BatchRunner.getService().runBatch(batchToRun)
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
                        batch.executionTime = executionTimeSpinner.getValue()
                        batch.comment = comment.getText()
                        batch.save()
                        if (!batch.name.equals(oldName))
                            model.refreshBatchNode()
                    }
                }
            } else {
                new I18NAlert("BatchNotValidName").show()
            }
        }] as IActionListener)
    }


}

class NewBatchView {

    ULCBoxPane content
    ULCLabel batchNameLabel
    ULCTextField batchNameTextField
    ULCLabel executionTimeLabel
    ULCSpinner executionTimeSpinner
    ULCLabel commentLabel
    ULCTextArea comment
    ULCButton addButton
    ULCButton cancelButton
    final Dimension dimension = new Dimension(140, 20)

    P1RATModel model

    public NewBatchView() {
    }

    public NewBatchView(P1RATModel model) {
        this.model = model
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void initComponents() {
        content = new ULCBoxPane(1, 3, 5, 5)

        batchNameLabel = new ULCLabel(UIUtils.getText(NewBatchView.class, "Name"))
        batchNameTextField = new ULCTextField()
        batchNameTextField.setPreferredSize(new Dimension(145, 20))
        executionTimeLabel = new ULCLabel(UIUtils.getText(NewBatchView.class, "ExecutionTime"))
        ULCSpinnerDateModel dateSpinnerModel = new ULCSpinnerDateModel()
        executionTimeSpinner = new ULCSpinner(dateSpinnerModel)
        executionTimeSpinner.setPreferredSize(new Dimension(145, 20))
        executionTimeSpinner.setEditor(new ULCDateEditor(executionTimeSpinner, FastDateFormat.getDateTimeInstance(FastDateFormat.SHORT, FastDateFormat.SHORT, UIUtils.getClientLocale()).pattern))

        commentLabel = new ULCLabel(UIUtils.getText(NewBatchView.class, "Comment"))
        comment = new ULCTextArea(4, 50)
        comment.lineWrap = true
        comment.wrapStyleWord = true

    }

    protected void layoutComponents() {
        ULCBoxPane parameterSection = getParameterSectionPane()
        content.add(ULCBoxPane.BOX_LEFT_TOP, parameterSection)
        content.add(ULCBoxPane.BOX_LEFT_TOP, getButtonsPane())
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }

    protected void attachListeners() {
        addButton.addActionListener([actionPerformed: {ActionEvent evt ->
            String batchName = batchNameTextField.getValue()
            if (validate(batchName)) {
                BatchRun.withTransaction {
                    mapToDao().save()
                }
                BatchRun item = BatchRun.findByName(batchName)
                if (item)
                    model.addItem(item)
            } else {
                new I18NAlert("BatchNotValidName").show()
            }
        }] as IActionListener)
    }

    protected boolean validate(String batchName) {
        return StringUtils.isNotEmpty(batchName) && StringUtils.isNotBlank(batchName) && BatchRun.findByName(batchName) == null
    }


    protected ULCBoxPane getParameterSectionPane() {
        ULCBoxPane parameterSection = boxLayout(UIUtils.getText(NewBatchView.class, "BatchConfig") + ":") {ULCBoxPane box ->
            ULCBoxPane content = new ULCBoxPane(3, 3)

            content.add(ULCBoxPane.BOX_LEFT_CENTER, batchNameLabel)
            content.add(2, ULCBoxPane.BOX_LEFT_TOP, spaceAround(batchNameTextField, 2, 10, 0, 0))

            content.add(ULCBoxPane.BOX_LEFT_CENTER, executionTimeLabel)
            content.add(2, ULCBoxPane.BOX_LEFT_TOP, spaceAround(executionTimeSpinner, 2, 10, 0, 0))

            content.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel(UIUtils.getText(NewBatchView.class, "Comment") + ":"))
            content.add(2, ULCBoxPane.BOX_LEFT_TOP, spaceAround(comment, 2, 10, 0, 0))

            box.add ULCBoxPane.BOX_LEFT_TOP, content
        }
        return parameterSection
    }

    ULCBoxPane getButtonsPane() {
        addButton = new ULCButton(UIUtils.getText(NewBatchView.class, "Add"))

        addButton.setPreferredSize(dimension)

        ULCBoxPane buttonPane = new ULCBoxPane(columns: 2, rows: 1)

        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(addButton, 0, 8, 0, 8))
        buttonPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        return buttonPane
    }


    protected ULCBoxPane boxLayout(String title, Closure body) {
        ULCBoxPane result = new ULCBoxPane()
        result.border = BorderFactory.createTitledBorder(" $title ")
        ULCBoxPane inner = new ULCBoxPane()
        body(inner)
        result.add ULCBoxPane.BOX_EXPAND_EXPAND, spaceAround(inner, 0, 5, 5, 5)
        return result
    }


    protected ULCBoxPane spaceAround(ULCComponent comp, int top, int left, int bottom, int right) {
        ULCBoxPane deco = new ULCBoxPane()
        deco.border = BorderFactory.createEmptyBorder(top, left, bottom, right)
        deco.add(ULCBoxPane.BOX_EXPAND_EXPAND, comp)
        return deco
    }

    protected BatchRun mapToDao() {
        BatchRun newBatchRun = new BatchRun(name: batchNameTextField.getValue(), comment: comment.getValue())
        newBatchRun.setExecutionTime(executionTimeSpinner.getValue())
        return newBatchRun
    }


}
