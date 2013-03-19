package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.SimulationRun

class SingleIterationFunction extends AbstractResultFunction implements IParametrizedFunction<Integer> {

    public static final String KEY_FIGURE_NAME = 'singleIteration'
    public static final String ITERATION = "Iteration"

    protected int iteration

    SingleIterationFunction(int iteration) {
        this.iteration = iteration
    }

    @Override
    String getDisplayName() {
        return "$iteration. ${super.getDisplayName()}"
    }

    String getName() {
        return ITERATION
    }

    Double evaluateResult(SimulationRun simulationRun, int periodIndex, ResultTableTreeNode node) {
        return ResultAccessor.getUltimatesForOneIteration(simulationRun, periodIndex, node.path, node.collector, node.field, iteration)
    }

    String getKeyFigureName() {
        KEY_FIGURE_NAME
    }

    boolean calculateForNonStochasticalValues() {
        return true
    }

    Integer getParameter() {
        return iteration
    }

    @Override
    FunctionDescriptor createDescriptor() {
        return new ParametrizedFunctionDescriptor(getClass(), getParameter())
    }
}
