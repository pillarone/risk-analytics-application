import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext

ant.property(environment: "env")

includeTargets << grailsScript("_GrailsPackage")
includeTargets << grailsScript("_GrailsBootstrap")

target('default': "Load the Grails interactive Swing console") {
    depends(checkVersion, configureProxy, packageApp, classpath)
    runRiskAnalytics()
}

target(runRiskAnalytics: "The application start target") {
    try {
        ApplicationContext ctx = GrailsUtil.bootstrapGrailsFromClassPath();
        GrailsApplication app = (GrailsApplication) ctx.getBean(GrailsApplication.APPLICATION_ID);
        new GroovyShell(app.classLoader).evaluate '''
            import org.pillarone.riskanalytics.application.ui.P1RATStandaloneLauncher
            import org.codehaus.groovy.grails.commons.GrailsClass
            import org.codehaus.groovy.grails.commons.GrailsBootstrapClass
            import org.codehaus.groovy.grails.commons.BootstrapArtefactHandler
            import org.codehaus.groovy.grails.commons.ApplicationHolder

            GrailsClass[] bootstraps =  ApplicationHolder.application.getArtefacts(BootstrapArtefactHandler.TYPE);
            for (GrailsClass bootstrap : bootstraps) {
                final GrailsBootstrapClass bootstrapClass = (GrailsBootstrapClass) bootstrap;
                //Quartz bootstrap needs a servlet context
                if(!bootstrapClass.clazz.simpleName.startsWith("Quartz")) {
                    bootstrapClass.callInit(null);
                }
            }

            P1RATStandaloneLauncher.start()
        '''
    } catch (Exception e) {
        event("StatusFinal", ["Error starting application: ${e.message} "])
        e.printStackTrace()
    }
}
