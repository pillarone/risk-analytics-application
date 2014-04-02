package org.pillarone.riskanalytics.application.logging.model

import com.ulcjava.base.application.DefaultListModel
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import grails.util.Holders
import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.spi.LoggingEvent
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.PollingSupport

import static org.pillarone.riskanalytics.application.logging.model.LoggingManager.NO_USER
import static org.pillarone.riskanalytics.application.logging.model.LoggingManager.USER_PROPERTY

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class RealTimeLoggingModel {

    DefaultListModel listModel = new DefaultListModel()
    final List<LoggingEvent> pendingLoggingEvents

    private MyAppender appender
    private PollingSupport pollingSupport
    private final IActionListener addLoggingEventAction

    RealTimeLoggingModel() {
        pendingLoggingEvents = new ArrayList<LoggingEvent>()
        appender = new MyAppender()
        addLoggingEventAction = [actionPerformed: { ActionEvent event ->
            addPendingLoggingEvents()
        }] as IActionListener
    }

    private void addPendingLoggingEvents() {
        synchronized (pendingLoggingEvents) {
            LoggingManager manager = LoggingAppender.instance.loggingManager
            List<String> messages = []
            for (int i = 0; i < pendingLoggingEvents.size(); i++) {
                String userName = pendingLoggingEvents[i].getProperty(USER_PROPERTY)
                if (userName != null && (userName == NO_USER || userName.equals(UserContext.currentUser?.username))) {
                    messages << manager.layout.format(pendingLoggingEvents[i])
                }
            }
            pendingLoggingEvents.clear()
            listModel.addAll(messages as String[])
        }
    }

    void append(LoggingEvent loggingEvent) {
        synchronized (pendingLoggingEvents) {
            pendingLoggingEvents.add(loggingEvent)
        }
    }

    void clear() {
        listModel.clear()
    }

    public String getContent() {
        StringBuilder sb = new StringBuilder()
        for (int i = 0; i < listModel.size; i++) {
            sb.append(listModel.getElementAt(i))
        }
        return sb.toString()
    }

    void start() {
        if (!pollingSupport) {
            pollingSupport = Holders.grailsApplication.mainContext.getBean('pollingSupport1000', PollingSupport)
        }
        LoggingAppender.instance.loggingManager.addAppender(appender)
        pollingSupport.addActionListener(addLoggingEventAction)
    }

    void stop() {
        LoggingAppender.instance.loggingManager.removeAppender(appender)
        pollingSupport.removeActionListener(addLoggingEventAction)
        addPendingLoggingEvents()
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
}
