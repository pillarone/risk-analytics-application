package org.pillarone.riskanalytics.application

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginManager

class ApplicationController {
    private static final String RISK_ANALYTICS_APPLICATION = "riskAnalyticsApplication"
    private static final String WEBSTART_JSP = "webstart.jsp"
    private static final String ULC_SERVER_ENDPOINT = '/ulcServerEndpoint/gate'
    private static final String DEFAULT_SESSION_PREFIX = ";jsessionid="
    private static final String SERVER_SESSION_PREFIX_KEY = "serverSessionPrefix"

    GrailsPluginManager pluginManager
    GrailsApplication grailsApplication

    def webstart() {
        redirect(url: getWebstartUrl())
    }

    def applet() {
        return [urlString: getUlcServerEnpointUrl(), applicationContextPath: getApplicationPluginPath()]
    }

    private String getWebstartUrl() {
        resource(dir: getApplicationPluginPath(), file: WEBSTART_JSP, absolute: true)
    }

    private String getApplicationPluginPath() {
        pluginManager.getPluginPath(RISK_ANALYTICS_APPLICATION)
    }

    private String getUlcServerEnpointUrl() {
        StringBuilder builder = new StringBuilder()
        builder.append(request.getScheme())
        builder.append('://')
        builder.append(request.getServerName())
        builder.append(':')
        builder.append(request.getServerPort())
        builder.append(request.getContextPath())
        builder.append(ULC_SERVER_ENDPOINT)
        builder.append(getSessionIdPostFix())
        builder.toString()
    }

    private String getSessionIdPostFix() {
        Map grailsConfig = grailsApplication.config.flatten()
        String sessionPrefix = grailsConfig.containsKey(SERVER_SESSION_PREFIX_KEY) ? (String) grailsConfig.get(SERVER_SESSION_PREFIX_KEY) : DEFAULT_SESSION_PREFIX;
        sessionPrefix + session.getId()
    }
}
