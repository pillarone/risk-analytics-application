package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.RuntimeParameterCollector.RuntimeParameterDescriptor
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory


class RuntimeParameterPaneModel {

    Model model
    List<RuntimeParameterDescriptor> runtimeParameters

    RuntimeParameterPaneModel(Model model) {
        this.model = model
        model.init()
        model.injectComponentNames()
        initRuntimeParameters()
    }

    protected initRuntimeParameters() {
        RuntimeParameterCollector parameterCollector = new RuntimeParameterCollector()
        model.accept(parameterCollector)

        runtimeParameters = parameterCollector.runtimeParameters.sort { it.propertyName }.toList()
    }

    boolean hasRuntimeParameters() {
        return !runtimeParameters.empty
    }

    List<ParameterHolder> getParameters() {
        return runtimeParameters.collect { ParameterHolderFactory.getHolder(it.propertyName, 0, it.value)}
    }
}
