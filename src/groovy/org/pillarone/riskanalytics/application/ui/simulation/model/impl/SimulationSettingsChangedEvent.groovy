package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

import static com.google.common.base.Preconditions.checkNotNull

class SimulationSettingsChangedEvent {

    final Class modelClass
    final ResultConfiguration template
    final Parameterization parameterization

    SimulationSettingsChangedEvent(ResultConfiguration template, Parameterization parameterization, Class modelClass) {
        this.modelClass = checkNotNull(modelClass)
        this.template = template
        this.parameterization = parameterization
        if (template && template.modelClass != modelClass) {
            throw new IllegalStateException("template $template has wrong modelClass")
        }

        if (parameterization && parameterization.modelClass != modelClass) {
            throw new IllegalStateException("parameterization $parameterization has wrong modelClass")
        }
    }
}
