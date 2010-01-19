package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.base.model.VersionedItemNode
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class ParameterizationNode extends VersionedItemNode {

    public ParameterizationNode(Parameterization parametrization) {
        super(parametrization, false)
    }
}
