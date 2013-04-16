package org.pillarone.riskanalytics.application.dataaccess.function

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.output.SimulationRun

@CompileStatic
class SimulationRunHolder {

    private final List<SimulationRun> simulationRuns = []

    SimulationRunHolder(SimulationRun simulationRun) {
        simulationRuns << simulationRun
    }

    SimulationRunHolder(List<SimulationRun> simulationRuns) {
        this.simulationRuns.addAll(simulationRuns)
    }

    SimulationRun getSimulationRun() {
        return getSimulationRun(0)
    }

    SimulationRun getSimulationRun(int index) {
        return simulationRuns[index]
    }

}
