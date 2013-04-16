package org.pillarone.riskanalytics.application.dataaccess.function

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.SimulationRun

@CompileStatic
class SigmaFunction extends AbstractResultFunction {

    final String name = 'Sigma'

    @Override
    Double evaluateResult(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        return ResultAccessor.getStdDev(simulationRun, periodIndex, node.path, node.collector, node.field)

    }

    @Override
    String getKeyFigureName() {
        return PostSimulationCalculation.STDEV
    }


}
