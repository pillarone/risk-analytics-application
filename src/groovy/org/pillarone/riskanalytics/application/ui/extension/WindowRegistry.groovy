package org.pillarone.riskanalytics.application.ui.extension

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

class WindowRegistry {

    private static Log LOG = LogFactory.getLog(WindowRegistry)
    private static Map<String, ComponentCreator> windowMap = [:]

    public static void registerWindow(String name, ComponentCreator componentCreator) {
        ComponentCreator existing = windowMap.get(name)
        if (existing != null) {
            throw new IllegalStateException("Already a window registered for ${name}.")
        }

        windowMap.put(name, componentCreator)
    }

    public static Map<String, ComponentCreator> getAllWindows() {
        return windowMap
    }
}
