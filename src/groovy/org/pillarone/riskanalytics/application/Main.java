package org.pillarone.riskanalytics.application;

import grails.util.GrailsUtil;
import groovy.lang.ExpandoMetaClass;
import org.codehaus.groovy.grails.commons.BootstrapArtefactHandler;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsBootstrapClass;
import org.codehaus.groovy.grails.commons.GrailsClass;
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
