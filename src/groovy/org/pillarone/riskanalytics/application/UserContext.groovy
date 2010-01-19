package org.pillarone.riskanalytics.application

import com.ulcjava.base.application.ApplicationContext
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.user.ApplicationUser
import org.pillarone.riskanalytics.application.user.UserManagement

class UserContext {

    private static Map fallbackContext = [:]
    static Log LOG = LogFactory.getLog(UserContext)

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

    public static ApplicationUser getCurrentUser() {
        return UserManagement.getCurrentUser()
    }

      public static void removeAttribute(String key){
        ApplicationContext.removeAttribute(key)
        fallbackContext.remove(key)
    }

}