package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class AbstractPresentationModel {

    Set<IModelChangedListener> listeners = new HashSet()

    void addModelChangedListener(IModelChangedListener listener) {
        listeners << listener
    }

    void removeModelChangedListener(IModelChangedListener listener) {
        listeners.remove(listener)
    }

    void fireModelChanged() {
        listeners.each { it.modelChanged() }
    }

    static List loadPeriodLabels(SimulationRun simulationRun, boolean showPeriodLabels) {
        List periodLabels = []
        if (showPeriodLabels) {
            SimulationRun.withTransaction { status ->
                SimulationRun run = SimulationRun.get(simulationRun.id) as SimulationRun
                Parameterization parameterization = ModellingItemFactory.getParameterization(run?.parameterization)
                parameterization.load(false)
                simulationRun.periodCount.times { int index ->
                    periodLabels << parameterization.getPeriodLabel(index)
                }
            }
        } else {
            simulationRun?.periodCount?.times { int index ->
                periodLabels << "P" + index
            }
        }
        return periodLabels
    }

}

interface IModelChangedListener {
    void modelChanged()
}