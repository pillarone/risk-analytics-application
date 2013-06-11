package org.pillarone.riskanalytics.application.dataaccess.function

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode

@CompileStatic
interface IFunction extends Cloneable {

    String getName()

    String getDisplayName()

    def evaluate(SimulationRunHolder simulationRunHolder, int periodIndex, SimpleTableTreeNode node)

    boolean calculateForNonStochasticalValues()

    FunctionDescriptor createDescriptor()

}