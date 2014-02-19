package org.pillarone.riskanalytics.application

import com.ulcjava.base.application.ApplicationContext
import com.ulcjava.base.application.ClientContext
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement
import org.codehaus.groovy.grails.web.util.WebUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest


class UserContext {
    private static final String SYSTEM_PROPERTY_STANDALONE_USERNAME = "standaloneUsername"

    private static Map fallbackContext = [:]
    static Log LOG = LogFactory.getLog(UserContext)

    public static final String USER_TIME_ZONE = "time_zone"

    public static void setAttribute(String key, Object value) {
        try {
            ApplicationContext.setAttribute key, value
        } catch (Exception e) {
            LOG.warn('Using fallbackContext as ULC ApplicationContext threw:' + e.getMessage())
            fallbackContext.put(key, value)
        }
    }

    public static Object getAttribute(String key) {
        try {
            return ApplicationContext.getAttribute(key)
        } catch (Exception e) {
            LOG.warn('Using fallbackContext as ULC ApplicationContext threw:' + e.getMessage())
            return fallbackContext.get(key)
        }
    }

    public static boolean hasCurrentUser() {
        try {
            return getCurrentUser() != null
        } catch (Exception e) {
            LOG.warn("getCurrentUser() threw:" + e.getMessage())
            return false
        }
    }

    public static Person getCurrentUser() {
        return UserManagement.getCurrentUser()
    }

    public static String getBaseUrl() {
        GrailsWebRequest grailsWebRequest = WebUtils.retrieveGrailsWebRequest()
        String contextPath = grailsWebRequest.getCurrentRequest().contextPath
        String requestURL = grailsWebRequest.getCurrentRequest().getRequestURL()
        String uri = grailsWebRequest.getCurrentRequest().getRequestURI()
        return requestURL.substring(0, requestURL.indexOf(uri)) + contextPath
    }

    public static void removeAttribute(String key) {
        ApplicationContext.removeAttribute(key)
        fallbackContext.remove(key)
    }

    public static boolean isStandAlone() {
        int type = ClientContext.getClientEnvironmentType()
        return type != ClientContext.APPLET && type != ClientContext.JNLP
    }

    public static boolean isApplet() {
        int type = ClientContext.getClientEnvironmentType()
        return type == ClientContext.APPLET
    }

    public static void setUserTimeZone(TimeZone timeZone) {
        setAttribute(USER_TIME_ZONE, timeZone)
    }

    public static TimeZone getUserTimeZone() {
        return (TimeZone) getAttribute(USER_TIME_ZONE)
    }

}