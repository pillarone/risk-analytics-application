package org.pillarone.riskanalytics.application.ui.batch.view

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.table.ULCTableColumn
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.batch.model.BatchRowInfo
import org.pillarone.riskanalytics.application.ui.batch.model.BatchViewModel
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.search.IModellingItemEventListener
import org.pillarone.riskanalytics.application.ui.search.ModellingItemCache
import org.pillarone.riskanalytics.application.ui.search.ModellingItemEvent
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SortableTable
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

import static org.pillarone.riskanalytics.application.ui.util.UIUtils.boxLayout
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.spaceAround

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
class BatchView implements IDetailView {

    private SortableTable batches
    private Batch batch
    private ULCButton runBatch
    private ULCBoxPane content
    private ULCComboBox simulationProfilesComboBox
    private ULCLabel parameterizationCount

    @Resource
    BatchViewModel batchViewModel

    @Resource
    ModellingItemCache modellingItemCache

    private MyBatchListener myBatchListener
    private final ValidationListener validationListener = new ValidationListener()
    private final IListSelectionListener updateSelectionCountListener = new UpdateSelectionCountListener()

    void setBatch(Batch batch) {
        if (this.batch) {
            this.batch.removeModellingItemChangeListener(validationListener)
        }
        this.batch = batch
        batch.addModellingItemChangeListener(validationListener)
        batchViewModel.batch = batch
        updateParameterizationCount()
        updateEnablingState()
        if (!batch || batch.executed) {
            lock()
        }
    }

    private void updateParameterizationCount() {
        String count = "Selected: ${selectedBatchRowInfos.size()}/${batchViewModel.simulationParameterizationTableModel.backedList.size()}"
        parameterizationCount.text = count
    }

    SortableTable getBatches() {
        return batches
    }

    private void lock() {
        batches.dragEnabled = false
        runBatch.enabled = false
        simulationProfilesComboBox.enabled = false
    }

    @PostConstruct
    void initialize() {
        myBatchListener = new MyBatchListener()
        modellingItemCache.addItemEventListener(myBatchListener)
        batches = new SortableTable(batchViewModel.simulationParameterizationTableModel)
        batches.selectionModel.addListSelectionListener(updateSelectionCountListener)
        BatchTableRenderer batchTableRenderer = new BatchTableRenderer(this)
        batches.columnModel.columns.each { ULCTableColumn column ->
            column.headerRenderer = new BatchTableHeaderRenderer()
            column.cellRenderer = batchTableRenderer
        }
        batches.showHorizontalLines = true
        content = new ULCBoxPane(1, 3, 5, 5)
        content.add(ULCBoxPane.BOX_LEFT_TOP, configurationPane)
        ULCScrollPane batchesPane = new ULCScrollPane(batches)
        batchesPane.preferredSize = new Dimension(600, 300)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, batchesPane)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, buttonsPane)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        attachListener()
    }

    @Override
    void close() {
        if (this.batch) {
            this.batch.removeModellingItemChangeListener(validationListener)
        }
        batches.selectionModel.removeListSelectionListener(updateSelectionCountListener)
        modellingItemCache.removeItemEventListener(myBatchListener)
        myBatchListener = null
        batchViewModel.destroy()
    }

    Batch getBatch() {
        return batch
    }

    List<BatchRowInfo> getSelectedBatchRowInfos() {
        batches.selectedRows.collect {
            batchViewModel.simulationParameterizationTableModel.batchRowInfos[it]
        }
    }

    private void attachListener() {
        runBatch.addActionListener([actionPerformed: { ActionEvent event ->
            batchViewModel.save()
            batchViewModel.run()
        }] as IActionListener)
        simulationProfilesComboBox.addActionListener([actionPerformed: { ActionEvent event ->
            String newSimulationProfileName = simulationProfilesComboBox.selectedItem as String
            if (batch) {
                batchViewModel.profileNameChanged(newSimulationProfileName)
            }
        }] as IActionListener)
    }

    private ULCBoxPane getButtonsPane() {
        final Dimension dimension = new Dimension(140, 20)
        runBatch = new ULCButton(UIUtils.getText(this.class, "RunBatch"))
        runBatch.preferredSize = dimension
        parameterizationCount = new ULCLabel()
        ULCBoxPane buttonPane = new ULCBoxPane(columns: 4, rows: 1)
        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(runBatch, 0, 8, 0, 8))
        buttonPane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        buttonPane.add(ULCBoxPane.BOX_RIGHT_TOP, spaceAround(parameterizationCount, 0, 8, 0, 8))
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

    private void updateEnablingState() {
        runBatch.enabled = batchViewModel.valid
    }

    ULCContainer getContent() {
        content
    }

    BatchViewModel getBatchViewModel() {
        return batchViewModel
    }

    void addParameterizations(List<Parameterization> parameterizations) {
        batchViewModel.addParameterizations(parameterizations)
        parameterizations.each { Parameterization parameterization ->
            BatchRowInfo batchRowInfo = batchViewModel.simulationParameterizationTableModel.batchRowInfos.find {
                it.parameterization == parameterization
            }
            int indexOf = batchViewModel.simulationParameterizationTableModel.batchRowInfos.indexOf(batchRowInfo)
            batches.selectionModel.addSelectionInterval(
                    indexOf, indexOf
            )
        }

    }

    void removeSelectedParameterizations() {
        batchViewModel.removeParameterizations(selectedBatchRowInfos.parameterization)
    }

    private class ValidationListener implements IModellingItemChangeListener {
        @Override
        void itemChanged(ModellingItem item) {
            updateEnablingState()
        }

        @Override
        void itemSaved(ModellingItem item) {
            updateEnablingState()
        }
    }

    private class UpdateSelectionCountListener implements IListSelectionListener {
        @Override
        void valueChanged(ListSelectionEvent event) {
            updateParameterizationCount()
        }
    }

    private class MyBatchListener implements IModellingItemEventListener {
        @Override
        void onEvent(ModellingItemEvent event) {
            if (event.modellingItem == getBatch()) {
                switch (event.eventType) {
                    case CacheItemEvent.EventType.ADDED:
                        break
                    case CacheItemEvent.EventType.REMOVED:
                        lock()
                        break
                    case CacheItemEvent.EventType.UPDATED:
                        batch.load()
                        setBatch(batch)
                        break
                }
            }
        }
    }
}
