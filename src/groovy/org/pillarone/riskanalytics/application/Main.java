package org.pillarone.riskanalytics.application;

import grails.util.GrailsUtil;
import groovy.lang.ExpandoMetaClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.commons.BootstrapArtefactHandler;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsBootstrapClass;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.pillarone.riskanalytics.application.initialization.StandaloneConfigLoader;
import org.pillarone.riskanalytics.application.ui.P1RATStandaloneLauncher;
import org.springframework.context.ApplicationContext;

public class Main {

    private static Log LOG = LogFactory.getLog(Main.class);

    public static void main(String args[]) {
        try {
            String environment = System.getProperty("grails.env");
            if (environment == null) {
                environment = "development";
                System.setProperty("grails.env", environment);

            }
            StandaloneConfigLoader.loadLog4JConfig(environment);

            LOG.info("Starting RiskAnalytics with environment " + environment);
            ExpandoMetaClass.enableGlobally();

            LOG.info("Loading grails..");

            ApplicationContext ctx = GrailsUtil.bootstrapGrailsFromClassPath();
            GrailsApplication app = (GrailsApplication) ctx.getBean(GrailsApplication.APPLICATION_ID);

            LOG.info("Executing bootstraps..");

            GrailsClass[] bootstraps = app.getArtefacts(BootstrapArtefactHandler.TYPE);
            for (GrailsClass bootstrap : bootstraps) {
                final GrailsBootstrapClass bootstrapClass = (GrailsBootstrapClass) bootstrap;
                //Quartz bootstrap needs a servlet context
                if (!bootstrapClass.getClazz().getSimpleName().startsWith("Quartz")) {
                    bootstrapClass.callInit(null);
                }
            }

            LOG.info("Loading user interface..");

            P1RATStandaloneLauncher.start();
        } catch (Exception e) {
            LOG.fatal("Startup failed", e);
        }
    }
}
