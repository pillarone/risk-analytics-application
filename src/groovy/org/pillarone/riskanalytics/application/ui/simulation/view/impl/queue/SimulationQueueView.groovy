package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.ULCTable
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationQueueViewModel
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class SimulationQueueView {

    @Resource
    SimulationQueueViewModel simulationQueueViewModel
    private ULCScrollPane content

    @PostConstruct
    void initialize() {
        content = new ULCScrollPane()
        ULCTable queueTable = new ULCTable(simulationQueueViewModel.simulationQueueTableModel)
        content.add(queueTable)
    }

    ULCComponent getContent() {
        content
    }
}
