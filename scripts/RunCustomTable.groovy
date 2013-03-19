import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext

ant.property(environment: "env")

includeTargets << grailsScript("_GrailsPackage")
includeTargets << grailsScript("_GrailsBootstrap")

target('default': "Load the Grails interactive Swing console") {
    depends(checkVersion, configureProxy, packageApp, classpath)
    runCustomTable()
}

target(runCustomTable: "The application start target") {
    try {
        ant.copy(toDir: classesDir, file: "./web-app/WEB-INF/applicationContext.xml", verbose: true)
        ApplicationContext ctx = GrailsUtil.bootstrapGrailsFromClassPath();
        GrailsApplication app = (GrailsApplication) ctx.getBean(GrailsApplication.APPLICATION_ID);
        new GroovyShell(app.classLoader, new Binding(app: app, ctx: ctx)).evaluate '''
            import org.codehaus.groovy.grails.web.context.GrailsConfigUtils
            import org.pillarone.riskanalytics.application.ui.customtable.StandaloneCustomTableLauncher

            GrailsConfigUtils.executeGrailsBootstraps(app,ctx,null)
            StandaloneCustomTableLauncher.launch()
        '''
    } catch (Exception e) {
        event("StatusFinal", ["Error starting application: ${e.message} "])
        e.printStackTrace()
    }
}
