package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.core.output.SimulationRun

class ComparisonSimulationRunHolder extends SimulationRunHolder {

    SimulationRun referenceRun
    SimulationRun runToCompare

    AbstractResultFunction underlyingFunction

    ComparisonSimulationRunHolder(SimulationRun simulationRun) {
        super(simulationRun)
    }

    ComparisonSimulationRunHolder(List<SimulationRun> simulationRuns) {
        super(simulationRuns)
    }


}
