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
        //workaround for GRAILS-7367
        ant.copy(toDir: classesDir, file: "./web-app/WEB-INF/applicationContext.xml", verbose: true)
        ApplicationContext ctx = GrailsUtil.bootstrapGrailsFromClassPath();
        GrailsApplication app = (GrailsApplication) ctx.getBean(GrailsApplication.APPLICATION_ID);
        new GroovyShell(app.classLoader, new Binding([app: app, ctx: ctx])).evaluate '''
            import org.codehaus.groovy.grails.web.context.GrailsConfigUtils
            import org.pillarone.riskanalytics.application.ui.P1RATStandaloneLauncher

            GrailsConfigUtils.executeGrailsBootstraps(app, ctx, null)
            P1RATStandaloneLauncher.start()
        '''
    } catch (Exception e) {
        event("StatusFinal", ["Error starting application: ${e.message} "])
        e.printStackTrace()
    }
}
