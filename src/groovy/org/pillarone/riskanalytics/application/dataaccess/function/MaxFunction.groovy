package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation

class MaxFunction extends AbstractResultFunction {

    public static final String MAX = "Max"

    @Override
    double evaluateResult(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        return ResultAccessor.getMax(simulationRun, periodIndex, node.path, node.collector, node.field)

    }

    String getName() {
        return MAX
    }

    @Override
    String getKeyFigureName() {
        return "max" //TODO: reference constant in PSC
    }


}
