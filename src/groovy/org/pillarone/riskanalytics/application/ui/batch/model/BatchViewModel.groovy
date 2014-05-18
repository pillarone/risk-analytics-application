package org.pillarone.riskanalytics.application.ui.batch.model

import com.ulcjava.base.application.DefaultComboBoxModel
import com.ulcjava.base.application.IComboBoxModel
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@CompileStatic
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
            boolean changed = batch.simulationProfileName != profileName
            if (changed) {
                batch.simulationProfileName = profileName
            }
            simulationParameterizationTableModel.simulationProfileNameChanged()
            if (changed) {
                batch.changed = true
            }
        }
    }

    boolean getValid() {
        return simulationParameterizationTableModel.batchRowInfos.every { BatchRowInfo info -> info.valid }
    }

    void run() {
        if (batch) {
            batchRunService.runBatch(batch)
        }
    }

    void addParameterizations(List<Parameterization> parameterizations) {
        List<Parameterization> copy = new ArrayList<>(parameterizations)
        copy.removeAll(batch.parameterizations)
        if (copy) {
            batch.parameterizations += copy
            simulationParameterizationTableModel.batch = batch
            batch.changed = true
        }
    }

    void removeParameterizations(List<Parameterization> parameterizations) {
        if (parameterizations) {
            batch.parameterizations.removeAll(parameterizations)
            simulationParameterizationTableModel.batch = batch
            batch.changed = true
        }
    }
}
