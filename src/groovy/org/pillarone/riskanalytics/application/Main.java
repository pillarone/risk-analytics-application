package org.pillarone.riskanalytics.application;

import grails.util.GrailsUtil;
import groovy.lang.Closure;
import groovy.lang.ExpandoMetaClass;
import org.apache.log4j.LogManager;
import org.codehaus.groovy.grails.commons.BootstrapArtefactHandler;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsBootstrapClass;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.codehaus.groovy.grails.plugins.logging.Log4jConfig;
import org.pillarone.riskanalytics.application.ui.P1RATStandaloneLauncher;
import org.springframework.context.ApplicationContext;

public class Main {

    public static void main(String args[]) {
        System.out.println("Starting RiskAnalytics");
        try {
            String environment = System.getProperty("grails.env");
            if (environment == null) {
                environment = "dev";
                System.setProperty("grails.env", environment);

            }
            System.out.println("Starting with environment " + environment);
            ExpandoMetaClass.enableGlobally();

            System.out.println("Loading grails..");

            ApplicationContext ctx = GrailsUtil.bootstrapGrailsFromClassPath();
            GrailsApplication app = (GrailsApplication) ctx.getBean(GrailsApplication.APPLICATION_ID);

            System.out.println("Executing bootstraps..");

            Object loggingConfig = ConfigurationHolder.getConfig().get("log4j");
            LogManager.resetConfiguration();
            if (loggingConfig instanceof Closure) {
                new Log4jConfig().configure((Closure) loggingConfig);
            } else {
                new Log4jConfig().configure();
            }

            GrailsClass[] bootstraps = app.getArtefacts(BootstrapArtefactHandler.TYPE);
            for (GrailsClass bootstrap : bootstraps) {
                final GrailsBootstrapClass bootstrapClass = (GrailsBootstrapClass) bootstrap;
                //Quartz bootstrap needs a servlet context
                if (!bootstrapClass.getClazz().getSimpleName().startsWith("Quartz")) {
                    bootstrapClass.callInit(null);
                }
            }

            System.out.println("Loading user interface..");

            P1RATStandaloneLauncher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
