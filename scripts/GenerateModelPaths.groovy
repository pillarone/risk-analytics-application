import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsUtil
import org.springframework.context.ApplicationContext

includeTargets << grailsScript("Init")
includeTargets << grailsScript("Package")
includeTargets << grailsScript("Bootstrap")

target(main: "Lists all possible paths of a model") {
    depends(parseArguments, checkVersion, configureProxy, packageApp, classpath)

    ApplicationContext ctx = GrailsUtil.bootstrapGrailsFromClassPath();
    GrailsApplication app = (GrailsApplication) ctx.getBean(GrailsApplication.APPLICATION_ID);
    String modelClass = argsMap.modelClass
    new GroovyShell(app.classLoader, new Binding([modelClass: argsMap.modelClass])).evaluate '''
            import org.pillarone.riskanalytics.application.output.structure.DefaultResultStructureBuilder
            import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure

            Class clazz = Thread.currentThread().getContextClassLoader().loadClass(modelClass)
            ResultStructure resultStructure = DefaultResultStructureBuilder.create("default", clazz)
            println "### Begin Path Map ###"

            println "["
            for (Map.Entry entry in resultStructure.mappings) {
                println '"' + entry.key + '": "' + entry.value + '",'
            }
            println "]"

            println "### End Path Map ###"
        '''


}

setDefaultTarget(main)
