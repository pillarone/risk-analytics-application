package org.pillarone.riskanalytics.application.help

import grails.util.Holders
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.util.I18NUtilities
import org.pillarone.riskanalytics.core.components.Component

import org.apache.log4j.Logger

@CompileStatic
public class ComponentHelp {

    public static final String DEFAULT_HELP_URL_CONFIG_KEY = "defaultHelpURL"

    static String getHelpUrl(Component component, Locale locale) {
        //Check for component specific help..
        String url = I18NUtilities.getHelpText("['" + component.class.name + "']")
        if (!url) {
            //Nope, check if any default help set in the config..
            if (Holders.config.containsKey(DEFAULT_HELP_URL_CONFIG_KEY)) {
                url = Holders.config.getProperty(DEFAULT_HELP_URL_CONFIG_KEY)
            }
        }
        if (!url) {
            Logger.getLogger(ComponentHelp).error "No help available for ${component.class.name}"
            throw new MissingHelpException("No help available for ${component.class.name}")
        }
        return url
    }
}
