package org.pillarone.riskanalytics.application.logging.model

import com.ulcjava.base.application.DefaultListModel
import com.ulcjava.base.application.ULCPollingTimer
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.spi.Filter
import org.apache.log4j.spi.LoggingEvent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class RealTimeLoggingModel {

    DefaultListModel listModel = new DefaultListModel()
    List<LoggingEvent> pendingLoggingEvents

    def RealTimeLoggingModel() {
        pendingLoggingEvents = new ArrayList<LoggingEvent>()
        ULCPollingTimer timer = new ULCPollingTimer(1000, [actionPerformed: { ActionEvent event ->
            addPendingLoggingEvents()

        }] as IActionListener)
        timer.start()
        Filter userNameFilter = [decide: {LoggingEvent event ->
            String userName = event.getProperty("user")
//            if (userName != null && userName.equals(UserContext.getCurrentUser()?.getUsername())) {
//                return Filter.ACCEPT
//            }
            return Filter.ACCEPT
        }] as Filter


        MyAppender appender = new MyAppender()
        appender.addFilter(userNameFilter)
        LoggingAppender.getInstance().getLoggingManager().addAppender(appender)
    }

    private addPendingLoggingEvents() {
        synchronized (pendingLoggingEvents) {
            String[] messages = new String[pendingLoggingEvents.size()]
            for (int i = 0; i < pendingLoggingEvents.size(); i++) {
                messages[i] = pendingLoggingEvents.get(i).getMessage().toString()
            }
            pendingLoggingEvents.clear()
            listModel.addAll(messages)
        }
    }

    def void append(LoggingEvent loggingEvent) {
        synchronized (pendingLoggingEvents) {
            pendingLoggingEvents.add(loggingEvent)
        }
    }

    public void clear() {
        listModel.clear()
    }

    private class MyAppender extends AppenderSkeleton {

        protected void append(LoggingEvent loggingEvent) {
            RealTimeLoggingModel.this.append(loggingEvent)
        }

        void close() {
        }

        boolean requiresLayout() {
            return false
        }

    }
}
