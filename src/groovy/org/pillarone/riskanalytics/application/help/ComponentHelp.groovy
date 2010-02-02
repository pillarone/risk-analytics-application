package org.pillarone.riskanalytics.application.help

import org.apache.log4j.Logger
import org.pillarone.riskanalytics.core.components.Component

public class ComponentHelp {

    static String getHelpUrl(Component component, Locale locale) {
        String helpUrl = null
        String propertyName = "org/pillarone/riskanalytics/application/help/ComponentHelp"
        try {
            helpUrl = ResourceBundle.getBundle(propertyName, locale).getString(component.class.name)
        } catch (MissingResourceException e) {
            Logger.getLogger(ComponentHelp).error component, e
            throw new MissingHelpException("No help available for ${component.class.name} in the property ${propertyName}")
        }
        return helpUrl
    }
}

class MissingHelpException extends Exception {

    public MissingHelpException(String message) {
        super(message)
    }
}