package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun

class SigmaFunction extends AbstractResultFunction {

    public static final String SIGMA = "Sigma"

    @Override
    Double evaluateResult(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        return ResultAccessor.getStdDev(simulationRun, periodIndex, node.path, node.collector, node.field)

    }

    String getName() {
        return SIGMA
    }

    @Override
    String getKeyFigureName() {
        return PostSimulationCalculation.STDEV
    }


}
