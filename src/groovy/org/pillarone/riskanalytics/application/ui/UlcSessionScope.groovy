package org.pillarone.riskanalytics.application.ui

import com.ulcjava.base.application.ApplicationContext
import com.ulcjava.base.server.ULCSession
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.config.Scope

class UlcSessionScope implements Scope {

    public static final String ULC_SESSION_SCOPE = 'ulcSessionScope'
    public static final String CALL_BACK_MAP = 'callBackMap'

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
}
