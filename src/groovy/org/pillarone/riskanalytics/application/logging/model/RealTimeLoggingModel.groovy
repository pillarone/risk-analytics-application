package org.pillarone.riskanalytics.application.logging.model

import com.ulcjava.base.application.DefaultListModel
import com.ulcjava.base.application.ULCPollingTimer
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.spi.LoggingEvent
import org.pillarone.riskanalytics.application.UserContext

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class RealTimeLoggingModel {

    DefaultListModel listModel = new DefaultListModel()
    List<LoggingEvent> pendingLoggingEvents

    private MyAppender appender

    RealTimeLoggingModel() {
        pendingLoggingEvents = new ArrayList<LoggingEvent>()
        ULCPollingTimer timer = new ULCPollingTimer(1000, [actionPerformed: { ActionEvent event ->
            addPendingLoggingEvents()

        }] as IActionListener)
        timer.start()

        appender = new MyAppender()
    }

    private addPendingLoggingEvents() {
        synchronized (pendingLoggingEvents) {
            LoggingManager manager = LoggingAppender.getInstance().getLoggingManager()
            List<String> messages = []
            for (int i = 0; i < pendingLoggingEvents.size(); i++) {
                String userName = pendingLoggingEvents.get(i).getProperty("user")
                if (userName != null && (userName == "testUser" || userName.equals(UserContext.getCurrentUser()?.getUsername()))) {
                    messages << manager.layout.format(pendingLoggingEvents.get(i))
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
        for (int i = 0; i < listModel.getSize(); i++) {
            sb.append(listModel.getElementAt(i))
        }
        return sb.toString()
    }

    void start() {
        LoggingAppender.getInstance().getLoggingManager().addAppender(appender)
    }

    void stop() {
        LoggingAppender.getInstance().getLoggingManager().removeAppender(appender)
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