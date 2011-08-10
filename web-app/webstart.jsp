<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>
<%@ page contentType="application/x-java-jnlp-file" language="java" %>
<%
    //Idea from http://lopica.sourceforge.net/faq.html#relcodebase
    StringBuffer codebaseBuffer = new StringBuffer();
    codebaseBuffer.append(!request.isSecure() ? "http://" : "https://");
    codebaseBuffer.append(request.getServerName());
    if (request.getServerPort() != (!request.isSecure() ? 80 : 443)) {
        codebaseBuffer.append(':');
        codebaseBuffer.append(request.getServerPort());
    }
    codebaseBuffer.append(request.getContextPath());

%>

<%
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

    String sessionPrefix = ConfigurationHolder.getConfig().containsKey("serverSessionPrefix") ? (String) ConfigurationHolder.getConfig().get("serverSessionPrefix") : ";jsessionid=";
    String sessionId = sessionPrefix + session.getId();
%>
<?xml version="1.0" encoding="ISO-8859-1"?>
<jnlp spec="1.0+" codebase="<%= codebaseBuffer.toString() %>">

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
        <jar href="./plugins/ulc-ria-suite-u2/lib/ulc-jnlp-client.jar"/>
        <jar href="./plugins/ulc-ria-suite-u2/lib/ulc-base-client.jar"/>
        <jar href="./plugins/ulc-ria-suite-u2/lib/ulc-servlet-client.jar"/>
        <jar href="./plugins/ulc-ria-suite-u2/lib/ulc-base-trusted.jar"/>
        <jar href="<%= appPluginDir %>/lib/RiskAnalyticsApplication-jnlp-client.jar" main="true"/>
        <jar href="<%= appPluginDir %>/lib/RiskAnalyticsApplication-extensions-client.jar"/>
        <jar href="<%= appPluginDir %>/lib/RiskAnalyticsApplication-client.jar"/>
    </resources>

    <resources os="Windows">
        <j2se version="1.5+"/>
    </resources>

    <application-desc main-class="org.pillarone.riskanalytics.application.environment.jnlp.P1RATJNLPLauncher">
        <argument>url-string=<%= codebaseBuffer.toString() %>/ulcServerEndpoint/gate<%= sessionId %></argument>
        <argument>keep-alive-interval=60</argument>
        <argument>log-level=WARNING</argument>
        <argument>ViewFactory=org.pillarone.riskanalytics.application.environment.jnlp.P1RATFrameViewFactory</argument>
    </application-desc>

</jnlp>
