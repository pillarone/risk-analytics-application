package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.*
import com.ulcjava.base.application.ULCSpinner.ULCDateEditor
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import grails.util.Holders
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.time.FastDateFormat
import org.pillarone.riskanalytics.application.ui.main.view.AbstractView
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.simulation.item.Batch

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NewBatchView extends AbstractView {

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

    BatchUIItem batchUIItem

    NewBatchView(BatchUIItem batchUIItem) {
        this.batchUIItem = batchUIItem
    }

    RiskAnalyticsMainModel getModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    void initComponents() {
        content = new ULCBoxPane(1, 3, 5, 5)

        batchNameLabel = new ULCLabel(UIUtils.getText(NewBatchView.class, "Name"))
        batchNameTextField = new ULCTextField()
        batchNameTextField.name = "batchNameTextField"
        batchNameTextField.preferredSize = new Dimension(145, 20)
        executionTimeLabel = new ULCLabel(UIUtils.getText(NewBatchView.class, "ExecutionTime"))
        ULCSpinnerDateModel dateSpinnerModel = new ULCSpinnerDateModel()
        executionTimeSpinner = new ULCSpinner(dateSpinnerModel)
        executionTimeSpinner.name = "executionTimeSpinner"
        executionTimeSpinner.preferredSize = new Dimension(145, 20)
        executionTimeSpinner.editor = new ULCDateEditor(executionTimeSpinner, FastDateFormat.getDateTimeInstance(FastDateFormat.SHORT, FastDateFormat.SHORT, UIUtils.getClientLocale()).pattern)

        commentLabel = new ULCLabel(UIUtils.getText(NewBatchView.class, "Comment"))
        comment = new ULCTextArea(4, 50)
        comment.name = "comment"
        comment.lineWrap = true
        comment.wrapStyleWord = true

    }

    void layoutComponents() {
        ULCBoxPane parameterSection = parameterSectionPane
        content.add(ULCBoxPane.BOX_LEFT_TOP, parameterSection)
        content.add(ULCBoxPane.BOX_LEFT_TOP, buttonsPane)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }

    void attachListeners() {
        addButton.addActionListener([actionPerformed: { ActionEvent evt ->
            batchUIItem.createNewBatch((ULCComponent) evt.source, createBatch())
            notifyItemSaved()
        }] as IActionListener)
    }

    protected boolean validate(String batchName) {
        return StringUtils.isNotEmpty(batchName) && StringUtils.isNotBlank(batchName) && BatchRun.findByName(batchName) == null
    }

    protected void notifyItemSaved() {
        batchUIItem.item.notifyItemChanged()
    }

    protected ULCBoxPane getParameterSectionPane() {
        ULCBoxPane parameterSection = boxLayout(UIUtils.getText(NewBatchView.class, "BatchConfig") + ":") { ULCBoxPane box ->
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
        addButton.name = "addButton"

        addButton.preferredSize = dimension

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

    protected Batch createBatch() {
        Batch batch = new Batch(batchNameTextField.value as String)
        batch.comment = comment.value as String
        return batch
    }


}