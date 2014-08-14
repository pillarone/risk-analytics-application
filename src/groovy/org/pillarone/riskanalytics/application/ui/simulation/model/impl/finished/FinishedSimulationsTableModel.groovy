package org.pillarone.riskanalytics.application.ui.simulation.model.impl.finished

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationQueueTableModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationRowModel
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedSimulationsTableModel extends SimulationQueueTableModel {

    @Override
    protected void sortColumnModels() {}

    void removeAt(int[] selected) {
        List<SimulationRowModel> toRemove = selected.collect {
            columnModels[it]
        }
        columnModels.removeAll(toRemove)
        assignRowsToColumnModels()
        List<Integer> selectedAsList = selected.toList()
        fireTableRowsDeleted(selectedAsList.min(), selectedAsList.max())
    }

    void simulationDeleted(Simulation simulation) {
        SimulationRowModel rowModel = columnModels.find { SimulationRowModel simulationRowModel ->
            simulationRowModel.object.simulation == simulation
        }
        if (rowModel) {
            rowModel?.object?.deleted = true
            fireTableRowsUpdated(rowModel.row, rowModel.row)
        }
    }
}
