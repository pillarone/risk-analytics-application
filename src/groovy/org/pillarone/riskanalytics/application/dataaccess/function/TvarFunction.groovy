package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.core.output.SimulationRun

class TvarFunction extends AbstractQuantilePerspectiveBasedFunction<Double> {

    public static final String TVAR = "TVaR"
    private double tvarValue

    TvarFunction(QuantilePerspective quantilePerspective, double tvarValue) {
        super(quantilePerspective)
        this.tvarValue = tvarValue
    }

    Double getParameter() {
        return tvarValue
    }

    @Override
    double evaluateResult(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        return ResultAccessor.getTvar(simulationRun, periodIndex, node.path, node.collector, node.field, tvarValue, quantilePerspective)
    }

    String getName() {
        return TVAR
    }

    @Override
    String getKeyFigureName() {
        return quantilePerspective == QuantilePerspective.LOSS ? PostSimulationCalculation.PERCENTILE : PostSimulationCalculation.PERCENTILE_PROFIT
    }

}
