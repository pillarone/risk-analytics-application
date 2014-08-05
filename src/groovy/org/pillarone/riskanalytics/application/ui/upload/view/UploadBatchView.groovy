package org.pillarone.riskanalytics.application.ui.upload.view

import com.google.common.eventbus.Subscribe
import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IListSelectionListener
import com.ulcjava.base.application.event.ListSelectionEvent
import com.ulcjava.base.application.table.ULCTableColumn
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.main.eventbus.RiskAnalyticsEventBus
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.ModellingItemEvent
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.sortable.view.SortableTable
import org.pillarone.riskanalytics.application.ui.upload.model.SimulationRowInfo
import org.pillarone.riskanalytics.application.ui.upload.model.UploadBatchViewModel
import org.pillarone.riskanalytics.application.ui.util.IResourceBundleResolver
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulationprofile.SimulationProfileService
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

import static org.pillarone.riskanalytics.application.ui.util.UIUtils.boxLayout
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.spaceAround

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
class UploadBatchView implements IDetailView {

    private SortableTable simulations
    private ULCButton upload
    private ULCBoxPane content
    private ULCComboBox simulationProfilesComboBox
    private ULCLabel simulationCount
    private ULCLabel warningLabel

    @Lazy
    private ULCContainer container = new ULCScrollPane(content)

    @Resource
    UploadBatchViewModel uploadBatchViewModel

    @Resource
    RiskAnalyticsEventBus riskAnalyticsEventBus

    @Resource
    IResourceBundleResolver resourceBundleResolver

    @Resource
    SimulationProfileService simulationProfileService

    private final IListSelectionListener updateSelectionCountListener = new UpdateSelectionCountListener()

    private void updateSimulationCount() {
        String count = "Selected: ${selectedSimulationRowInfos.size()}/${uploadBatchViewModel.uploadSimulationTableModel.backedList.size()}"
        simulationCount.text = count
    }

    SortableTable getSimulations() {
        return simulations
    }

    @PostConstruct
    void initialize() {
        simulations = new SortableTable(uploadBatchViewModel.uploadSimulationTableModel)
        simulations.selectionModel.addListSelectionListener(updateSelectionCountListener)
        UploadBatchTableRenderer batchTableRenderer = new UploadBatchTableRenderer(this)
        simulations.columnModel.columns.each { ULCTableColumn column ->
            column.cellRenderer = batchTableRenderer
        }
        simulations.showHorizontalLines = true
        content = new ULCBoxPane(1, 3, 5, 5)
        content.add(ULCBoxPane.BOX_LEFT_TOP, configurationPane)
        ULCScrollPane batchesPane = new ULCScrollPane(simulations)
        batchesPane.preferredSize = new Dimension(600, 300)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, batchesPane)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, buttonsPane)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        attachListener()
    }

    @Override
    void close() {
        riskAnalyticsEventBus.unregister(this)
        simulations.selectionModel.removeListSelectionListener(updateSelectionCountListener)
        uploadBatchViewModel.close()
    }

    @Subscribe
    void onEvent(ModellingItemEvent event) {
        if (!(event.modellingItem instanceof Simulation)) {
            return
        }
        Simulation simulation = event.modellingItem as Simulation
        switch (event.eventType) {
            case CacheItemEvent.EventType.ADDED:
                break
            case CacheItemEvent.EventType.REMOVED:
                uploadBatchViewModel.removeSimulations([simulation])
                break
            case CacheItemEvent.EventType.UPDATED:
//                TODO check if we need to update something
//                uploadBatchViewModel.updateSimulation(simulation)
                break
        }
    }

    List<SimulationRowInfo> getSelectedSimulationRowInfos() {
        simulations.selectedRows.collect { int index ->
            uploadBatchViewModel.uploadSimulationTableModel.simulationRowInfos[index]
        }
    }

    private void attachListener() {
        riskAnalyticsEventBus.register(this)
        upload.addActionListener([actionPerformed: { ActionEvent event ->
            uploadBatchViewModel.upload()
        }] as IActionListener)
        simulationProfilesComboBox.addActionListener([actionPerformed: { ActionEvent event ->
            String newSimulationProfileName = simulationProfilesComboBox.selectedItem as String
            if (simulationProfileService.activeProfileName != newSimulationProfileName) {
                warningLabel.text = 'This is not the current profile!'
            } else {
                warningLabel.text = ''
            }
            uploadBatchViewModel.profileNameChanged(newSimulationProfileName)
            updateEnablingState()
        }] as IActionListener)
    }

    private ULCBoxPane getButtonsPane() {
        final Dimension dimension = new Dimension(140, 20)
        upload = new ULCButton(resourceBundleResolver.getText(this.class, "UploadBatch"))
        upload.preferredSize = dimension
        simulationCount = new ULCLabel()
        ULCBoxPane buttonPane = new ULCBoxPane(columns: 4, rows: 1)
        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(upload, 0, 8, 0, 8))
        buttonPane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        buttonPane.add(ULCBoxPane.BOX_RIGHT_TOP, spaceAround(simulationCount, 0, 8, 0, 8))
        return buttonPane
    }

    private ULCBoxPane getConfigurationPane() {
        simulationProfilesComboBox = new ULCComboBox(uploadBatchViewModel.simulationProfileNamesComboBoxModel)
        warningLabel = new ULCLabel()
        warningLabel.foreground = Color.red
        String configText = resourceBundleResolver.getText(this.class, "UploadBatchConfig")
        String profileText = resourceBundleResolver.getText(this.class, "SimulationProfile")
        ULCBoxPane parameterSection = boxLayout("$configText:") { ULCBoxPane box ->
            ULCBoxPane content = new ULCBoxPane(3, 3)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(profileText))
            content.add(2, ULCBoxPane.BOX_LEFT_TOP, spaceAround(simulationProfilesComboBox, 2, 10, 0, 0))
            content.add(3, ULCBoxPane.BOX_LEFT_TOP, warningLabel)
            box.add ULCBoxPane.BOX_LEFT_TOP, content
        }
        return parameterSection
    }

    private void updateEnablingState() {
        upload.enabled = uploadBatchViewModel.valid
    }

    ULCContainer getContent() {
        container
    }

    void addSimulations(List<Simulation> simulations) {
        uploadBatchViewModel.addSimulations(simulations)
        selectAddedSimulations(simulations)
        updateEnablingState()
    }

    private List<Simulation> selectAddedSimulations(List<Simulation> simulations) {
        simulations.each { Simulation simulation ->
            SimulationRowInfo batchRowInfo = uploadBatchViewModel.uploadSimulationTableModel.simulationRowInfos.find { SimulationRowInfo info ->
                info.parameterization == simulation
            }
            int indexOf = uploadBatchViewModel.uploadSimulationTableModel.simulationRowInfos.indexOf(batchRowInfo)
            this.simulations.selectionModel.addSelectionInterval(
                    indexOf, indexOf
            )
        }
    }

    void removeSelectedSimulations() {
        uploadBatchViewModel.removeSimulations(selectedSimulationRowInfos.simulation)
        updateEnablingState()
    }

    private class UpdateSelectionCountListener implements IListSelectionListener {
        @Override
        void valueChanged(ListSelectionEvent event) {
            updateSimulationCount()
        }
    }
}
