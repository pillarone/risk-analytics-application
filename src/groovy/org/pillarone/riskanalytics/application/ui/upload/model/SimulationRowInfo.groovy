package org.pillarone.riskanalytics.application.ui.upload.model

import com.google.common.base.Preconditions
import grails.util.Holders
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile
import org.pillarone.riskanalytics.core.upload.UploadValidationError
import org.pillarone.riskanalytics.core.upload.UploadValidationService

@CompileStatic
class SimulationRowInfo {
    private SimulationProfile simulationProfile
    private final Simulation simulation
    private List<UploadValidationError> errors = []

    SimulationRowInfo(Simulation simulation, SimulationProfile simulationProfile = null) {
        Preconditions.checkNotNull(simulation)
        this.simulation = simulation
        setSimulationProfile(simulationProfile)
    }

    List<UploadValidationError> getErrors() {
        new ArrayList(errors)
    }

    String getName() {
        simulation.nameAndVersion
    }

    String getModelName() {
        modelClass?.simpleName
    }

    Class getModelClass() {
        parameterization?.modelClass
    }

    String getTemplateName() {
        simulation.template?.nameAndVersion
    }

    String getIterationAsString() {
        simulation.numberOfIterations
    }

    String getRandomSeed() {
        simulation.randomSeed
    }

    Parameterization getParameterization() {
        simulation.parameterization
    }

    String getParameterizationVersion() {
        parameterization?.versionNumber?.toString()
    }

    boolean isValid() {
        errors.empty
    }

    Simulation getSimulation() {
        return simulation
    }

    ResultConfiguration getTemplate() {
        simulation.template
    }

    void setSimulationProfile(SimulationProfile simulationProfile) {
        this.simulationProfile = simulationProfile
        errors = uploadValidationService.validate(simulation, simulationProfile)
    }

    UploadValidationService getUploadValidationService() {
        Holders.grailsApplication.mainContext.getBean('uploadValidationService', UploadValidationService)
    }
}
