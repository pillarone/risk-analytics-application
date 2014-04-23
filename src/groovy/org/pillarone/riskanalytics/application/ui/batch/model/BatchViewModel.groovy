package org.pillarone.riskanalytics.application.ui.batch.model

import com.ulcjava.base.application.DefaultComboBoxModel
import com.ulcjava.base.application.IComboBoxModel
import org.pillarone.riskanalytics.core.SimulationProfileDAO
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
class BatchViewModel {

    @Resource
    BatchRunService batchRunService

    private boolean valid

    SimulationParameterizationTableModel simulationParameterizationTableModel
    IComboBoxModel simulationProfileNamesComboBoxModel
    private final Batch batch

    BatchViewModel(Batch batch) {
        this.batch = batch
    }

    @PostConstruct
    void initialize() {
        simulationProfileNamesComboBoxModel = new DefaultComboBoxModel(simulationProfileNames)
        simulationProfileNamesComboBoxModel.selectedItem = batch.simulationProfileName
        simulationParameterizationTableModel = new SimulationParameterizationTableModel(batch, batch.simulationProfileName)
        validate()
    }

    BatchViewModel() {
        this(null)
        throw new IllegalStateException("empty constructor is only for spring")
    }

    void save() {
        batch.simulationProfileName = simulationProfileNamesComboBoxModel.selectedItem
        batch.parameterizations = simulationParameterizationTableModel.batchRowInfos.parameterization
        batch.save()
        validate()
    }

    List<String> getSimulationProfileNames() {
        SimulationProfileDAO.createCriteria().list {
            projections {
                property('name')
            }
        }.unique()
    }

    void profileNameChanged(String profileName) {
        simulationParameterizationTableModel.simulationProfileNameChanged(profileName)
        validate()
    }

    private boolean validate() {
        valid = simulationParameterizationTableModel.batchRowInfos.every { it.valid }
    }

    boolean getValid() {
        return valid
    }

    void run() {
        batchRunService.runBatch(batch)
        //TODO lock ui, because batch is now executed
    }
}
