package org.pillarone.riskanalytics.application.help

import org.pillarone.riskanalytics.core.components.Component

import org.pillarone.riskanalytics.core.util.ResourceBundleRegistry
import org.apache.log4j.Logger
import org.pillarone.riskanalytics.application.ui.util.I18NUtils

public class ComponentHelp {

    static String getHelpUrl(Component component, Locale locale) {
        String url = I18NUtils.getHelpText("['" + component.class.name + "']")
        if (!url) {
            Logger.getLogger(ComponentHelp).error "No help available for ${component.class.name}"
            throw new MissingHelpException("No help available for ${component.class.name}")
        }
        return url
    }
}

class MissingHelpException extends Exception {

    public MissingHelpException(String message) {
        super(message)
    }
}