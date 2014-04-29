package org.pillarone.riskanalytics.application.ui.batch.model

import com.ulcjava.base.application.DefaultComboBoxModel
import com.ulcjava.base.application.IComboBoxModel
import org.codehaus.groovy.grails.commons.GrailsApplication
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
    @Resource
    GrailsApplication grailsApplication

    @Resource
    SimulationParameterizationTableModel simulationParameterizationTableModel

    IComboBoxModel simulationProfileNamesComboBoxModel
    private Batch batch

    @PostConstruct
    void initialize() {
        simulationProfileNamesComboBoxModel = new DefaultComboBoxModel(batchRunService.simulationProfileNames)
    }

    void destroy() {
        simulationParameterizationTableModel.destroy()
    }

    void setBatch(Batch batch) {
        this.batch = batch
        simulationParameterizationTableModel.batch = batch
        simulationProfileNamesComboBoxModel.selectedItem = batch.simulationProfileName
    }

    void save() {
        if (batch) {
            batch.save()
        }
    }

    void profileNameChanged(String profileName) {
        if (batch) {
            simulationParameterizationTableModel.simulationProfileNameChanged()
            if (batch.simulationProfileName != profileName) {
                batch.simulationProfileName = profileName
                batch.changed = true
            }
        }
    }

    boolean getValid() {
        return simulationParameterizationTableModel.batchRowInfos.every { it.valid }
    }

    void run() {
        if (batch) {
            batchRunService.runBatch(batch)
        }
    }
}
