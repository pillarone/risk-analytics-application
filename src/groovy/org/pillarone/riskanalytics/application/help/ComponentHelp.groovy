package org.pillarone.riskanalytics.application.help

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.components.Component

import org.apache.log4j.Logger
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

@CompileStatic
public class ComponentHelp {

    public static final String DEFAULT_HELP_URL_CONFIG_KEY = "defaultHelpURL"

    static String getHelpUrl(Component component, Locale locale) {
        //Check for component specific help..
        String url = I18NUtils.getHelpText("['" + component.class.name + "']")
        if (!url) {
            //Nope, check if any default help set in the config..
            if (ConfigurationHolder.getConfig() != null && ConfigurationHolder.getConfig().containsKey("defaultHelpURL")) {
                url = ConfigurationHolder.getConfig().getProperty("defaultHelpURL")
            }
        }
        if (!url) {
            Logger.getLogger(ComponentHelp).error "No help available for ${component.class.name}"
            throw new MissingHelpException("No help available for ${component.class.name}")
        }
        return url
    }
}
