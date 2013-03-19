package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation

class MeanFunction extends AbstractResultFunction {

    public static final String MEAN = "Mean"

    @Override
    Double evaluateResult(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        return ResultAccessor.getMean(simulationRun, periodIndex, node.path, node.collector, node.field)
    }

    String getName() {
        return MEAN
    }

    @Override
    String getKeyFigureName() {
        return PostSimulationCalculation.MEAN
    }

    boolean calculateForNonStochasticalValues() {
        return true
    }

    //TODO: remove equals/hashCode after proper ResultView refactoring
    @Override
    boolean equals(Object obj) {
        return obj instanceof MeanFunction
    }

    @Override
    int hashCode() {
        return new HashCodeBuilder().append(MeanFunction.name).toHashCode()
    }


}
