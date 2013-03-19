package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode


class NodeNameFunction extends AbstractFunction {


    def evaluate(SimulationRunHolder simulationRunHolder, int periodIndex, SimpleTableTreeNode node) {
        return node.displayName
    }
    String getName() {
        return "Name"
    }

    boolean calculateForNonStochasticalValues() {
        return true
    }

}
