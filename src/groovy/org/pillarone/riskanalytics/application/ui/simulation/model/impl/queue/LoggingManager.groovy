package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import groovy.transform.CompileStatic
import org.apache.log4j.Appender
import org.apache.log4j.PatternLayout
import org.apache.log4j.spi.Filter
import org.apache.log4j.spi.LoggingEvent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class LoggingManager {

    private List<Appender> appenders = []
    private UserNameFilter userNameFilter = new UserNameFilter()

    PatternLayout layout

    void appendLog(LoggingEvent event) {
        fireAppendEvent(event)
    }

    synchronized void fireAppendEvent(LoggingEvent loggingEvent) {
        for (Appender appender : appenders) {
            appender.doAppend(loggingEvent)
        }
    }

    synchronized void addAppender(Appender appender) {
        appender.addFilter(userNameFilter)
        appender.setLayout(layout)
        appenders.add(appender)
    }

    synchronized void removeAppender(Appender loggingListener) {
        appenders.remove(loggingListener)
    }

    void setLayout(String pattern) {
        layout = new PatternLayout()
        layout.setConversionPattern(pattern)
    }

    private static class UserNameFilter extends Filter {

        @Override
        int decide(LoggingEvent loggingEvent) {
            if (loggingEvent.getMDC('simulation')) {
                return ACCEPT
            } else {
                return DENY
            }
        }
    }
}