package org.pillarone.riskanalytics.application.logging.model

import org.apache.log4j.Appender
import org.apache.log4j.Layout
import org.apache.log4j.PatternLayout
import org.apache.log4j.spi.Filter
import org.apache.log4j.spi.LoggingEvent
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.core.user.Person

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class LoggingManager {

    private List<LoggingEvent> loggingEvents = new ArrayList<LoggingEvent>()
    private List<Appender> appenders = []
    List<Filter> categoryNameFilters = []
    Layout layout

    public void appendLog(LoggingEvent event) {
        Person user = null
        try {
            user = UserContext.getCurrentUser()
            event.setProperty("user", user.getUsername())
        } catch (Exception ex) {
            event.setProperty("user", "testUser")
        }

// event.setProperty("user", "testUser")
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

    public void addAppender(Appender appender) {
        for (Filter filter: categoryNameFilters) {
            appender.addFilter(filter)
        }
        appender.setLayout(layout)
        appenders.add(appender)
    }

    public void removeAppender(Appender loggingListener) {
        appenders.remove(loggingListener)
    }

    public void addFilters(List<String> categoryNames) {
        for (String categoryName: categoryNames) {
            Filter categoryNameFilter = [decide: {LoggingEvent event ->
                if (event.categoryName.indexOf(categoryName) == 0) {
                    String userName = event.getProperty("user")
                    if (userName != null && (userName == "testUser" || userName.equals(UserContext.getCurrentUser()?.getUsername()))) {
                        return Filter.ACCEPT
                    }
                }
                return Filter.DENY
            }] as Filter
            categoryNameFilters << categoryNameFilter
        }
    }

    public void setLayout(String pattern) {
        layout = new PatternLayout()
        layout.setConversionPattern(pattern)
    }
}