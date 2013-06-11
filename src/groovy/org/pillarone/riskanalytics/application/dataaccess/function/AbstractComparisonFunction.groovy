package org.pillarone.riskanalytics.application.dataaccess.function

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode

@CompileStatic
abstract class AbstractComparisonFunction extends AbstractFunction {

    AbstractResultFunction underlyingFunction

    final def evaluate(SimulationRunHolder simulationRunHolder, int periodIndex, SimpleTableTreeNode node) {

        if (node instanceof ResultTableTreeNode && simulationRunHolder instanceof ComparisonSimulationRunHolder) {
            return evaluateComparison(underlyingFunction.evaluateResult(simulationRunHolder.referenceRun, periodIndex, node),
                    underlyingFunction.evaluateResult(simulationRunHolder.runToCompare, periodIndex, node))
        }

        return null
    }

    abstract protected double evaluateComparison(double referenceResult, double resultToCompare)

    boolean calculateForNonStochasticalValues() {
        return true
    }


}
