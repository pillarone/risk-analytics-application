package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import groovy.time.TimeCategory
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfoAdapter
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.user.Person
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class SimulationInfoPaneModel {
    private DateTimeFormatter dateFormat = DateFormatUtils.getDateFormat("HH:mm")

    private SimulationRuntimeInfo running
    private Simulation latestFinishedSimulation

    @Resource
    UlcSimulationRuntimeService ulcSimulationRuntimeService

    @Delegate
    private SimulationStateEventSupport support = new SimulationStateEventSupport()
    private MyListener listener = new MyListener()

    @PostConstruct
    void register() {
        ulcSimulationRuntimeService.addSimulationRuntimeInfoListener(listener)
    }

    @PreDestroy
    void unregister() {
        ulcSimulationRuntimeService.removeSimulationRuntimeInfoListener(listener)
    }

    String getEstimatedEndTime() {
        DateTime estimatedSimulationEnd = running?.estimatedSimulationEnd
        if (estimatedSimulationEnd != null) {
            return dateFormat.print(estimatedSimulationEnd)
        }
        return "-"
    }

    String getSimulationStartTime() {
        DateTime simulationStartTime = running?.simulation?.start
        if (simulationStartTime != null) {
            return dateFormat.print(simulationStartTime)
        }
        return "-"
    }

    String getSimulationEndTime() {
        DateTime simulationEndTime = running?.simulation?.end
        if (simulationEndTime != null) {
            return dateFormat.print(simulationEndTime)
        }
        return "-"
    }

    String getRemainingTime() {
        DateTime end = running?.estimatedSimulationEnd
        if (end != null) {
            use(TimeCategory) {
                def duration = end.toDate() - new Date()
                "$duration.hours h $duration.minutes m $duration.seconds s"
            }
        } else {
            "-"
        }
    }

    int getProgress() {
        running?.progress ?: 0
    }

    String getErrorMessage() {
        List<Throwable> errors = running?.simulationErrors
        if (!errors) {
            return ''
        }
        HashSet<String> messages = new HashSet<String>();
        for (Throwable simulationException : errors) {
            String exceptionMessage = simulationException.message
            if (exceptionMessage == null) {
                exceptionMessage = simulationException.class.name
            }
            messages.add(exceptionMessage);
        }
        StringBuffer text = new StringBuffer();
        for (String exceptionMessage : messages) {
            List words = exceptionMessage.split(' ') as List
            int lineLength = 0
            for (String s in words) {
                if (lineLength + s.length() > 70) {
                    text << '\n'
                    lineLength = 0
                }
                text << s + ' '
                lineLength += (s.length() + 1)
            }
            text << '\n';
        }
        text.toString()
    }

    Person getSimulationOwner() {
        running?.simulation?.creator
    }

    boolean isBatchSimulation() {
        running?.simulation?.batch
    }

    Simulation getLatestFinishedSimulation() {
        latestFinishedSimulation
    }

    private class MyListener extends SimulationRuntimeInfoAdapter {
        @Override
        void starting(SimulationRuntimeInfo info) {
            running = info
            notifySimulationStateChanged(info.simulationState)
        }

        @Override
        void finished(SimulationRuntimeInfo info) {
            running = info
            if (info.simulationState == SimulationState.FINISHED) {
                latestFinishedSimulation = info.simulation
            } else {
                latestFinishedSimulation = null
            }
            notifySimulationStateChanged(info.simulationState)
        }

        @Override
        void changed(SimulationRuntimeInfo info) {
            running = info
            notifySimulationStateChanged(info.simulationState)
        }

    }
}
