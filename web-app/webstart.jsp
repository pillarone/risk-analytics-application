<%@ page import="grails.util.Holders" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="application/x-java-jnlp-file" language="java" %>
<%
    Map grailsConfig = Holders.getConfig().flatten();

    String codebase = grailsConfig.get("grails.serverURL").toString();

    String requestURI = request.getRequestURI();
    String contextPath = request.getContextPath();
    //strip file / action name, results in the virtual location of the application or plugin
    requestURI = requestURI.substring(0, requestURI.lastIndexOf('/'));

    if (requestURI.startsWith(contextPath)) {
        requestURI = requestURI.substring(contextPath.length());
    }
    StringBuffer appPluginDir = new StringBuffer(".");
    //If the grails app which uses the ulc plugin is itself a plugin..
    if (requestURI.contains("plugins")) {
        appPluginDir.append("/plugins/").append(requestURI.substring(requestURI.lastIndexOf('/')));
    }
    String sessionPrefix = grailsConfig.containsKey("serverSessionPrefix") ? (String) grailsConfig.get("serverSessionPrefix") : ";jsessionid=";
    String sessionId = sessionPrefix + session.getId();
%>
<?xml version="1.0" encoding="ISO-8859-1"?>
<jnlp spec="1.0+" codebase="<%= codebase %>">

    <information>
        <title>RiskAnalytics</title>
        <vendor>Canoo AG</vendor>
        <description>ULC Application RiskAnalytics.</description>
        <homepage href="http://www.pillarone.org"/>
        <icon href="/images/PillarOneLogoSmall.png" width="48" height="48"/>
    </information>

    <security>
        <all-permissions/>
    </security>

    <resources>
        <j2se version="1.5+"/>
        <jar href="./plugins/ulc-ria-suite-2012-u1-2/lib/ulc-core-client.jar"/>
        <jar href="<%= appPluginDir %>/lib/RiskAnalyticsApplication-jnlp-client.jar" main="true"/>
        <jar href="<%= appPluginDir %>/lib/RiskAnalyticsApplication-extensions-client.jar"/>
        <jar href="<%= appPluginDir %>/lib/RiskAnalyticsApplication-client.jar"/>
        <jar href="<%= appPluginDir %>/lib/ULCMigLayout-client-1.0.jar"/>
        <jar href="<%= appPluginDir %>/lib/miglayout-3.7.3.1.jar"/>
    </resources>

    <resources os="Windows">
        <j2se version="1.5+"/>
    </resources>

    <application-desc main-class="org.pillarone.riskanalytics.application.environment.jnlp.P1RATJNLPLauncher">
        <argument>url-string=<%= codebase %>/ulcServerEndpoint/gate<%= sessionId %></argument>
        <argument>keep-alive-interval=60</argument>
        <argument>log-level=WARNING</argument>
        <argument>ViewFactory=org.pillarone.riskanalytics.application.environment.jnlp.P1RATFrameViewFactory</argument>
    </application-desc>

</jnlp>
