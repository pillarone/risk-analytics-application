package org.pillarone.riskanalytics.application.logging.model

import org.apache.log4j.Appender
import org.apache.log4j.Layout
import org.apache.log4j.PatternLayout
import org.apache.log4j.spi.Filter
import org.apache.log4j.spi.LoggingEvent
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class LoggingManager {

    public static final String USER_PROPERTY = "user"
    public static final String NO_USER = "no-user"

    private List<Appender> appenders = []
    private UserNameFilter userNameFilter

    Layout layout

    public void appendLog(LoggingEvent event) {
        Person user = null
        try {
            user = UserManagement.getCurrentUser()
            event.setProperty(USER_PROPERTY, user.getUsername())
        } catch (Exception ex) {
            event.setProperty(USER_PROPERTY, NO_USER)
        }

        fireAppendEvent(event)

    }

    synchronized void fireAppendEvent(LoggingEvent loggingEvent) {
        for (Appender appender: appenders) {
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

    public void setLayout(String pattern) {
        layout = new PatternLayout()
        layout.setConversionPattern(pattern)
    }

    //TODO: getCurrentUser does not work in simulation thread
    private static class UserNameFilter extends Filter {

        @Override
        int decide(LoggingEvent loggingEvent) {
            String userName = loggingEvent.getProperty(USER_PROPERTY)
            return (userName != null && (userName == NO_USER || userName.equals(UserContext.getCurrentUser()?.getUsername()))) ?
                Filter.ACCEPT : Filter.DENY;
        }

    }
}