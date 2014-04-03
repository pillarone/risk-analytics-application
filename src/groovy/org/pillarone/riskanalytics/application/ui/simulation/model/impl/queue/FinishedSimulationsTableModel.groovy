package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class FinishedSimulationsTableModel extends SimulationQueueTableModel {

    @Override
    void itemAdded(SimulationRuntimeInfo item) {
        int index = queueItems.size()
        queueItems.add(item)
        fireTableRowsInserted(index, index)
    }
}
