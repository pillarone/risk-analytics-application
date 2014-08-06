package org.pillarone.riskanalytics.application.ui.upload.model
import com.ulcjava.base.application.DefaultComboBoxModel
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulationprofile.SimulationProfileService
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@CompileStatic
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
class UploadBatchViewModel {

    @Resource
    UploadSimulationTableModel uploadSimulationTableModel

    @Resource
    SimulationProfileService simulationProfileService

    @Resource
    IDestinationService destinationService

    DefaultComboBoxModel simulationProfileNamesComboBoxModel
    DefaultComboBoxModel destinationNamesComboBoxModel
    boolean allowOverwrite = false

    private List<Simulation> simulations = []

    private String simulationProfileName

    @PostConstruct
    void initialize() {
        simulationProfileNamesComboBoxModel = new DefaultComboBoxModel(simulationProfileService.simulationProfileNames)
        String activeProfileName = simulationProfileService.activeProfileName
        simulationProfileNamesComboBoxModel.selectedItem = activeProfileName
        profileNameChanged(activeProfileName)
        destinationNamesComboBoxModel = new DefaultComboBoxModel(destinationService.destinations.toList())
    }

    void updateProfiles() {
        String selected = simulationProfileNamesComboBoxModel.selectedItem
        simulationProfileNamesComboBoxModel.removeAllElements()
        simulationProfileService.simulationProfileNames.each {
            simulationProfileNamesComboBoxModel.addElement(it)
        }
        simulationProfileNamesComboBoxModel.selectedItem = selected
    }

    void close() {
        uploadSimulationTableModel.close()
    }

    void profileNameChanged(String profileName) {
        if (simulationProfileName != profileName) {
            this.simulationProfileName = profileName
            uploadSimulationTableModel.simulationProfileNameChanged(profileName)
        }
    }

    boolean getValid() {
        return uploadSimulationTableModel.simulationRowInfos.every { SimulationRowInfo info -> info.valid }
    }

    void upload() {
        //TODO put sims into upload queue and remove it from this view
        removeSimulations(simulations)
        println("send to queue $simulations, destination: ${destinationNamesComboBoxModel.selectedItem}, allow overwrite: ${allowOverwrite}")
    }

    void addSimulations(List<Simulation> simulations) {
        List<Simulation> copy = new ArrayList<>(simulations)
        copy.removeAll(this.simulations)
        if (copy) {
            this.simulations += copy
        }
        uploadSimulationTableModel.setSimulations(this.simulations, simulationProfileName)
    }

    void removeSimulations(List<Simulation> simulations) {
        if (simulations) {
            this.simulations.removeAll(simulations)
            uploadSimulationTableModel.setSimulations(this.simulations, simulationProfileName)
        }
    }

    SimulationRowInfo getSimulationRowInfo(int row) {
        uploadSimulationTableModel.simulationRowInfos[row]
    }
}
