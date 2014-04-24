package org.pillarone.riskanalytics.application.ui
import com.ulcjava.applicationframework.application.Application
import com.ulcjava.applicationframework.application.ApplicationContext
import com.ulcjava.base.application.IApplication
import com.ulcjava.base.server.ULCSession
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.config.Scope
import org.springframework.stereotype.Component

@Component(UlcSessionScope.ULC_SESSION_SCOPE)
class UlcSessionScope implements Scope {

    private static final Log LOG = LogFactory.getLog(UlcSessionScope)
    public static final String ULC_SESSION_SCOPE = 'ulcSession'
    private static final String CALL_BACK_MAP = 'callBackMap'
    private static final String ULC_APPLICATION_CONTEXT = 'ulcApplicationContext'

    @Override
    Object get(String name, ObjectFactory<?> objectFactory) {
        def object = scopeMap[name]
        if (object == null) {
            object = objectFactory.object
            scopeMap[name] = object
        }
        object
    }

    @Override
    Object remove(String name) {
        scopeMap.remove(name)
        destructionCallbackMap.remove(name)
    }

    @Override
    void registerDestructionCallback(String name, Runnable callback) {
        destructionCallbackMap[name] = callback
    }

    @Override
    Object resolveContextualObject(String key) {
        if ('ulcSession' == key) {
            return ULCSession.currentSession()
        }
        null
        //TODO maybe resolve also the http session
    }

    @Override
    String getConversationId() {
        null
    }

    static void destroy() {
        destructionCallbackMap.values().each { it.run() }
    }

    private static Map<String, Object> getScopeMap() {
        Map<String, Object> scopeMap = ApplicationContext.getAttribute(ULC_SESSION_SCOPE) as Map
        if (scopeMap == null) {
            scopeMap = [:]
            scopeMap[ULC_APPLICATION_CONTEXT] = currentApplicationContext
            ApplicationContext.setAttribute(ULC_SESSION_SCOPE, scopeMap)
        }
        scopeMap
    }

    private static Map<String, Runnable> getDestructionCallbackMap() {
        Map<String, Runnable> callBackMap = ApplicationContext.getAttribute(CALL_BACK_MAP) as Map
        if (callBackMap == null) {
            callBackMap = [:]
            ApplicationContext.setAttribute(CALL_BACK_MAP, callBackMap)
        }
        callBackMap
    }

    private static ApplicationContext getCurrentApplicationContext() {
        IApplication application = ULCSession.currentSession().application
        if (application instanceof Application) {
            return application.context
        }
        LOG.warn('cannot get current application context')
        null
    }
}
