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

    private static final Log LOG = LogFactory.getLog(UserContext)
    private static final Map FALLBACK_CONTEXT = [:]
    public static final String USER_TIME_ZONE = "time_zone"

    public static void setAttribute(String key, Object value) {
        try {
            ApplicationContext.setAttribute key, value
        } catch (Exception e) {
            LOG.warn('Using FALLBACK_CONTEXT as ULC ApplicationContext threw:' + e.message)
            FALLBACK_CONTEXT[key] = value
        }
    }

    public static Object getAttribute(String key) {
        try {
            return ApplicationContext.getAttribute(key)
        } catch (Exception e) {
            LOG.warn('Using FALLBACK_CONTEXT as ULC ApplicationContext threw:' + e.message)
            return FALLBACK_CONTEXT[key]
        }
    }

    public static boolean hasCurrentUser() {
        try {
            return currentUser != null
        } catch (Exception e) {
            LOG.warn("getCurrentUser() threw:" + e.message)
            return false
        }
    }

    public static Person getCurrentUser() {
        return UserManagement.currentUser
    }

    public static String getBaseUrl() {
        GrailsWebRequest grailsWebRequest = WebUtils.retrieveGrailsWebRequest()
        String contextPath = grailsWebRequest.currentRequest.contextPath
        String requestURL = grailsWebRequest.currentRequest.requestURL
        String uri = grailsWebRequest.currentRequest.requestURI
        return requestURL.substring(0, requestURL.indexOf(uri)) + contextPath
    }

    public static boolean isStandAlone() {
        int type = ClientContext.clientEnvironmentType
        return type != ClientContext.APPLET && type != ClientContext.JNLP
    }

    public static boolean isApplet() {
        int type = ClientContext.clientEnvironmentType
        return type == ClientContext.APPLET
    }

    public static void setUserTimeZone(TimeZone timeZone) {
        setAttribute(USER_TIME_ZONE, timeZone)
    }

    public static TimeZone getUserTimeZone() {
        return (TimeZone) getAttribute(USER_TIME_ZONE)
    }

}