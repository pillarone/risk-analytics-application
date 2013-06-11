package org.pillarone.riskanalytics.application.dataaccess.function

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation

@CompileStatic
class MaxFunction extends AbstractResultFunction {

    final String name = 'Max'

    @Override
    Double evaluateResult(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        return ResultAccessor.getMax(simulationRun, periodIndex, node.path, node.collector, node.field)

    }

    @Override
    String getKeyFigureName() {
        PostSimulationCalculation.MAX
    }


}
