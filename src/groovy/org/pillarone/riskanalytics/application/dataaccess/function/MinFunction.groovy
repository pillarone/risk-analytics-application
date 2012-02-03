package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation


class MinFunction extends AbstractResultFunction {

    public static final String MIN = "Min"

    @Override
    Double evaluateResult(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        return ResultAccessor.getMin(simulationRun, periodIndex, node.path, node.collector, node.field)

    }

    String getName() {
        return MIN
    }

    @Override
    String getKeyFigureName() {
        return 'min'
    }


}
