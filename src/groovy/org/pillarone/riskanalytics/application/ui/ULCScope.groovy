package org.pillarone.riskanalytics.application.ui

import org.pillarone.riskanalytics.application.UserContext
import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.config.Scope

class ULCScope implements Scope {

    public static final String ULC_SCOPE = 'ulcScope'

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
    }

    @Override
    void registerDestructionCallback(String name, Runnable callback) {

    }

    @Override
    Object resolveContextualObject(String key) {
        null
    }

    @Override
    String getConversationId() {
        null
    }

    private Map<String, Object> getScopeMap() {
        Map<String, Object> scopeMap = UserContext.getAttribute(ULC_SCOPE) as Map
        if (scopeMap == null) {
            scopeMap = [:]
            UserContext.setAttribute(ULC_SCOPE, scopeMap)
        }
        scopeMap
    }
}
