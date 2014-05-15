package org.pillarone.riskanalytics.application

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginManager

class ApplicationController {
    private static final String ULC_SERVER_ENDPOINT = '/ulcServerEndpoint/gate'
    private static final String DEFAULT_SESSION_PREFIX = ";jsessionid="
    private static final String SERVER_SESSION_PREFIX_KEY = "serverSessionPrefix"

    GrailsPluginManager pluginManager
    GrailsApplication grailsApplication

    def webstart() {
        List<String> clientFiles = applicationPluginPaths.collect {getClientFilesForPluginPath(it)}.flatten()
        render(view: 'webstart.jnlp', model: [codebase: codeBase, ulcEndpoint: ulcServerEnpointUrl, clientFiles: clientFiles], contentType: "application/x-java-jnlp-file")
    }

    def applet() {
        return [urlString: ulcServerEnpointUrl, applicationContextPaths: applicationPluginPaths]
    }

    private List<String> getApplicationPluginPaths() {
        List<String> plugins = grailsApplication.config.clientLibPlugins
        plugins.collect {
            pluginManager.getPluginPath(it)
        }
    }

    private List getClientFilesForPluginPath(String pluginDir) {
        List<String> archive = []
        File appLibDir = new File(servletContext.getRealPath("${pluginDir}/lib"))
        for (fileName in appLibDir.list()) {
            archive << "${codeBase}${pluginDir}/lib/${fileName}"
        }
        archive
    }

    private String getCodeBase() {
        grailsApplication.config.grails.serverURL
    }

    private String getUlcServerEnpointUrl() {
        StringBuilder alternative = new StringBuilder(codeBase)
        alternative.append(ULC_SERVER_ENDPOINT)
        alternative.append(getSessionIdPostFix())
        alternative.toString()
    }

    private String getSessionIdPostFix() {
        Map grailsConfig = grailsApplication.config.flatten()
        String sessionPrefix = grailsConfig.containsKey(SERVER_SESSION_PREFIX_KEY) ? (String) grailsConfig[SERVER_SESSION_PREFIX_KEY] : DEFAULT_SESSION_PREFIX;
        sessionPrefix + session.id
    }
}
