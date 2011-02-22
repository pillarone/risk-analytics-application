package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.workflow.Status


class WorkflowParameterizationNode extends ParameterizationNode {

    public WorkflowParameterizationNode(Parameterization parametrization) {
        super(parametrization);
    }

    Status getStatus() {
        item.status
    }
}
