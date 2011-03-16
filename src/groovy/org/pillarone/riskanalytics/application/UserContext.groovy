package org.pillarone.riskanalytics.application

import com.ulcjava.base.application.ApplicationContext
import com.ulcjava.base.application.ClientContext
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement

class UserContext {

    private static Map fallbackContext = [:]
    static Log LOG = LogFactory.getLog(UserContext)

    public static final String USER_TIME_ZONE = "time_zone"

    public static void setAttribute(String key, Object value) {
        try {
            ApplicationContext.setAttribute key, value
        } catch (Exception e) {
            LOG.debug('ULC Application context not available', e)
            fallbackContext.put(key, value)
        }
    }

    public static Object getAttribute(String key) {
        try {
            return ApplicationContext.getAttribute(key)
        } catch (Exception e) {
            LOG.debug('ULC Application context not available', e)
            return fallbackContext.get(key)
        }
    }

    public static Person getCurrentUser() {
        return UserManagement.getCurrentUser()
    }

    public static void removeAttribute(String key) {
        ApplicationContext.removeAttribute(key)
        fallbackContext.remove(key)
    }

    public static boolean isStandAlone() {
        int type = ClientContext.getClientEnvironmentType()
        return type != ClientContext.APPLET && type != ClientContext.JNLP
    }

    public static void setUserTimeZone(TimeZone timeZone) {
        setAttribute(USER_TIME_ZONE, timeZone)
    }

    public static TimeZone getUserTimeZone() {
        return (TimeZone) getAttribute(USER_TIME_ZONE)
    }

}