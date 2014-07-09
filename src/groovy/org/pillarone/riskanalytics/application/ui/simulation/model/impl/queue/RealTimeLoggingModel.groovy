package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.DefaultListModel
import groovy.util.logging.Log
import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.spi.LoggingEvent
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.core.simulation.engine.ISimulationRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfoAdapter
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

@Log
@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
public class RealTimeLoggingModel {

    final DefaultListModel listModel = new DefaultListModel()
    private final List<String> pendingMessages = []
    private final MyAppender appender = new MyAppender()
    private ISimulationRuntimeInfoListener addPendingEventListener = new AddPendingEventsListener()
    private ISimulationRuntimeInfoListener addAppenderListener = new AddAppenderListener()

    @Resource
    UlcSimulationRuntimeService ulcSimulationRuntimeService

    @Autowired
    SimulationRuntimeService simulationRuntimeService

    @PostConstruct
    private void initialize() {
        //ulcSimulationRuntimeService posts events in ulc thread but the events are queued. The appender has to be added immediately, so we add it to the 'real' simulationRuntimeService
        ulcSimulationRuntimeService.addSimulationRuntimeInfoListener(addPendingEventListener)
        simulationRuntimeService.addSimulationRuntimeInfoListener(addAppenderListener)
    }

    @PreDestroy
    private void unregister() {
        simulationRuntimeService.removeSimulationRuntimeInfoListener(addAppenderListener)
        ulcSimulationRuntimeService.removeSimulationRuntimeInfoListener(addPendingEventListener)
    }

    void clear() {
        listModel.clear()
    }

    String getContent() {
        StringBuilder sb = new StringBuilder()
        for (int i = 0; i < listModel.size; i++) {
            sb.append(listModel.getElementAt(i))
        }
        return sb.toString()
    }


    private void addPendingLoggingEvents() {
        synchronized (pendingMessages) {
            listModel.addAll(pendingMessages as String[])
            pendingMessages.clear()
        }
    }

    private void append(LoggingEvent loggingEvent) {
        synchronized (pendingMessages) {
            LoggingManager manager = LoggingAppender.instance.loggingManager
            pendingMessages << manager.layout.format(loggingEvent)
        }
    }

    private class MyAppender extends AppenderSkeleton {

        protected void append(LoggingEvent loggingEvent) {
            RealTimeLoggingModel.this.append(loggingEvent)
        }

        void close() {
        }

        boolean requiresLayout() {
            return true
        }
    }

    private class AddPendingEventsListener extends SimulationRuntimeInfoAdapter {
        @Override
        void starting(SimulationRuntimeInfo info) {
            addPendingLoggingEvents()
        }

        @Override
        void changed(SimulationRuntimeInfo info) {
            addPendingLoggingEvents()
        }

        @Override
        void finished(SimulationRuntimeInfo info) {
            addPendingLoggingEvents()
        }
    }

    // Reduce technical noise in logs - demote to debug
    //
    private class AddAppenderListener extends SimulationRuntimeInfoAdapter {
        @Override
        void starting(SimulationRuntimeInfo info) {
            log.debug('added appender')
            LoggingAppender.instance.loggingManager.addAppender(appender)
        }


        @Override
        void finished(SimulationRuntimeInfo info) {
            LoggingAppender.instance.loggingManager.removeAppender(appender)
            log.debug('removed appender')
        }
    }
}
