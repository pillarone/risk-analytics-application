package org.pillarone.riskanalytics.application.logging.model

import groovy.transform.CompileStatic
import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.spi.LoggingEvent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class LoggingAppender extends AppenderSkeleton {

    private LoggingManager loggingManager = new LoggingManager()

    private static LoggingAppender instance;

    public synchronized static LoggingAppender getInstance() {
        if (instance == null) {
            instance = new LoggingAppender();
        }
        return instance;
    }

    private LoggingAppender() {
        loggingManager = new LoggingManager()
    }

    protected void append(LoggingEvent loggingEvent) {
        loggingManager.appendLog(loggingEvent)
    }

    void close() {
    }

    boolean requiresLayout() {
        return false;
    }

    public LoggingManager getLoggingManager() {
        return loggingManager
    }
}
