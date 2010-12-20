package org.pillarone.riskanalytics.application.logging.model

import org.apache.log4j.Appender
import org.apache.log4j.spi.LoggingEvent

//import org.pillarone.riskanalytics.core.user.Person

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class LoggingManager {

    private List<LoggingEvent> loggingEvents = new ArrayList<LoggingEvent>()
    private List<Appender> appenders = []

    public void appendLog(LoggingEvent event) {
//        Person user = UserContext.getCurrentUser()
//        event.setProperty("user", user.getUsername())
        event.setProperty("user", "testUser")
        loggingEvents << event
        fireAppendEvent(event)
    }

    void fireAppendEvent(LoggingEvent loggingEvent) {
        for (Appender appender: appenders) {
            appender.doAppend(loggingEvent)
        }
    }

    public Collection<LoggingEvent> getLogs() {
        return loggingEvents
    }

    public void addAppender(Appender loggingListener) {
        appenders.add(loggingListener)
    }

    public void removeAppender(Appender loggingListener) {
        appenders.remove(loggingListener)
    }
}
