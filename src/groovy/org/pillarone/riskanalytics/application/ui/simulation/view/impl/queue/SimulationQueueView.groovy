package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCPollingTimer
import com.ulcjava.base.application.ULCScrollPane
import com.ulcjava.base.application.ULCTable
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationQueueViewModel
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope('ulcSessionScope')
@Component
class SimulationQueueView {

    @Resource
    SimulationQueueViewModel simulationQueueViewModel
    private ULCScrollPane content
    private ULCPollingTimer pollingTimer

    @PostConstruct
    void initialize() {
        content = new ULCScrollPane()
        ULCTable queueTable = new ULCTable(simulationQueueViewModel.simulationQueueTableModel)
        content.add(queueTable)
        pollingTimer = new ULCPollingTimer(2000, new IActionListener() {
            @Override
            void actionPerformed(ActionEvent event) {
                simulationQueueViewModel.update()
            }
        })
        pollingTimer.start()
    }

    @PreDestroy
    void stopPollingTimer() {
        pollingTimer.stop()
        pollingTimer = null
    }

    ULCComponent getContent() {
        content
    }
}
