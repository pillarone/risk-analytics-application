package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.output.SimulationRun

abstract class AbstractResultFunction extends AbstractFunction {

    final def evaluate(SimulationRunHolder simulationRunHolder, int periodIndex, SimpleTableTreeNode node) {
        if (node instanceof ResultTableTreeNode) {
            return evaluateResult(simulationRunHolder.simulationRun, periodIndex, node)
        }
        return null
    }

    abstract Double evaluateResult(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node)

    abstract String getKeyFigureName()

    boolean calculateForNonStochasticalValues() {
        return false
    }

}
