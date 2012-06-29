package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode

interface IFunction extends Cloneable {

    String getName()

    String getDisplayName()

    def evaluate(SimulationRunHolder simulationRunHolder, int periodIndex, SimpleTableTreeNode node)

    boolean calculateForNonStochasticalValues()

    FunctionDescriptor createDescriptor()

}