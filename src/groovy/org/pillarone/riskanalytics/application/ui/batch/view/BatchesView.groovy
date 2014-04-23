package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.table.ULCTableColumn
import com.ulcjava.base.application.util.Dimension
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.pillarone.riskanalytics.application.ui.batch.model.BatchViewModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SortableTable
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

import static org.pillarone.riskanalytics.application.ui.util.UIUtils.boxLayout
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.spaceAround

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
class BatchesView {

    private SortableTable batches
    private final Batch batch
    private ULCButton runBatch
    private ULCButton saveButton
    private ULCBoxPane content
    private ULCComboBox simulationProfilesComboBox

    @Resource
    GrailsApplication grailsApplication

    private BatchViewModel batchViewModel

    BatchesView(Batch batch) {
        this.batch = batch
    }

    BatchesView() {
        this(null)
        throw new IllegalStateException("empty constructor for spring only")
    }

    @PostConstruct
    void initialize() {
        batchViewModel = grailsApplication.mainContext.getBean('batchViewModel', batch) as BatchViewModel
        batches = new SortableTable(batchViewModel.simulationParameterizationTableModel)
        BatchTableRenderer batchTableRenderer = new BatchTableRenderer()
        batches.columnModel.columns.each { ULCTableColumn column ->
            column.headerRenderer = new BatchTableHeaderRenderer()
            column.cellRenderer = batchTableRenderer
        }
        batches.showHorizontalLines = true
        content = new ULCBoxPane(1, 3, 5, 5)
        content.add(ULCBoxPane.BOX_LEFT_TOP, configurationPane)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCScrollPane(batches))
        content.add(ULCBoxPane.BOX_LEFT_TOP, buttonsPane)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        attachListener()
    }

    private void attachListener() {
        saveButton.addActionListener([actionPerformed: { ActionEvent event ->
            batchViewModel.save()
            runBatch.enabled = batchViewModel.valid
        }] as IActionListener)
        runBatch.addActionListener([actionPerformed: { ActionEvent event ->
            batchViewModel.save()
            batchViewModel.run()
        }] as IActionListener)
        simulationProfilesComboBox.addActionListener([actionPerformed: { ActionEvent event ->
            String newSimulationProfileName = simulationProfilesComboBox.selectedItem as String
            if (batch.simulationProfileName != newSimulationProfileName) {
                batchViewModel.profileNameChanged(newSimulationProfileName)
                runBatch.enabled = batchViewModel.valid
                batch.changed = true
            }
        }] as IActionListener)
    }

    private ULCBoxPane getButtonsPane() {
        final Dimension dimension = new Dimension(140, 20)
        saveButton = new ULCButton(UIUtils.getText(this.class, "Save"))
        saveButton.preferredSize = dimension
        runBatch = new ULCButton(UIUtils.getText(this.class, "RunBatch"))
        runBatch.preferredSize = dimension
        runBatch.enabled = batchViewModel.valid
        ULCBoxPane buttonPane = new ULCBoxPane(columns: 2, rows: 1)
        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(runBatch, 0, 8, 0, 8))
        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(saveButton, 0, 8, 0, 8))
        buttonPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        return buttonPane
    }

    private ULCBoxPane getConfigurationPane() {
        simulationProfilesComboBox = new ULCComboBox(batchViewModel.simulationProfileNamesComboBoxModel)
        ULCBoxPane parameterSection = boxLayout(UIUtils.getText(this.class, "BatchConfig") + ":") { ULCBoxPane box ->
            ULCBoxPane content = new ULCBoxPane(3, 3)

            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel('Simulation Profile'))
            content.add(2, ULCBoxPane.BOX_LEFT_TOP, spaceAround(simulationProfilesComboBox, 2, 10, 0, 0))
//            content.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel(UIUtils.getText(NewBatchView.class, "Comment") + ":"))
//            content.add(2, ULCBoxPane.BOX_LEFT_TOP, spaceAround(comment, 2, 10, 0, 0))

            box.add ULCBoxPane.BOX_LEFT_TOP, content
        }
        return parameterSection
    }

    ULCContainer getContent() {
        content
    }

    BatchViewModel getBatchViewModel() {
        return batchViewModel
    }
}
