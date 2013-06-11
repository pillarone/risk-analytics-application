package org.pillarone.riskanalytics.application.dataaccess.function

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode


@CompileStatic
class NodeNameFunction extends AbstractFunction {

    final String name = 'Name'

    def evaluate(SimulationRunHolder simulationRunHolder, int periodIndex, SimpleTableTreeNode node) {
        return node.displayName
    }

    boolean calculateForNonStochasticalValues() {
        return true
    }

}
