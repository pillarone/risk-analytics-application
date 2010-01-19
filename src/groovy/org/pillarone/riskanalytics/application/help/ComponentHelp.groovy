package org.pillarone.riskanalytics.application.help

import org.apache.log4j.Logger
import org.pillarone.riskanalytics.core.components.Component

public class ComponentHelp {

    static String getHelpUrl(Component component, Locale locale) {
        String helpUrl = null
        try {
            helpUrl = ResourceBundle.getBundle("org/pillarone/riskanalytics/application/help/ComponentHelp", locale).getString(component.class.name)
        } catch (MissingResourceException e) {
            Logger.getLogger(ComponentHelp).error component, e
            throw new MissingHelpException()
        }
        return helpUrl
    }
}

class MissingHelpException extends Exception {

    public MissingHelpException() {
        super("No help available")
    }

    public MissingHelpException(String message) {
        super(message)
    }
}