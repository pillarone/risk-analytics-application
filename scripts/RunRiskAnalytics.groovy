import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.context.ApplicationContext

Ant.property(environment: "env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"

System.setProperty(GrailsApplication.WORK_DIR, ".")

includeTargets << new File("${grailsHome}/scripts/Package.groovy")
includeTargets << new File("${grailsHome}/scripts/Bootstrap.groovy")

target('default': "Load the Grails interactive Swing console") {
    depends(checkVersion, configureProxy, packageApp, classpath)
    pirat()
}

target(pirat: "The application start target") {

    classLoader = new URLClassLoader([classesDir.toURL()] as URL[], rootLoader)
    Thread.currentThread().setContextClassLoader(classLoader)
    try {
        ApplicationContext ctx = GrailsUtil.bootstrapGrailsFromClassPath();
        GrailsApplication app = (GrailsApplication) ctx.getBean(GrailsApplication.APPLICATION_ID);
        new GroovyShell(app.classLoader).evaluate '''
            import org.pillarone.riskanalytics.application.ui.P1RATStandaloneLauncher
            new BootStrap().init(null)
            P1RATStandaloneLauncher.start()
        '''
    } catch (Exception e) {
        event("StatusFinal", ["Error starting application: ${e.message} "])
        e.printStackTrace()
    }
}
