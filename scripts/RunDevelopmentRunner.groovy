includeTargets << grailsScript("_GrailsRun")

target('default': "Runs a Grails application") {
    depends(checkVersion, configureProxy, packageApp, classpath)
    runUlcApp()
}

target(runUlcApp: "The application start target") {
    try {
        runApp()
        configureClassLoader(grailsSettings, rootLoader)
        ClassLoader loader = grailsApp.classLoader
        def runner = loader.loadClass("org.pillarone.riskanalytics.runner.P1RATDevelopmentRunner")
        runner.run()
        watchContext()
    } catch (Exception e) {
        event("StatusFinal", ["Error starting application: ${e.message} "])
    }
}

private void configureClassLoader(grailsSettings, rootLoader) {
    URL classesDir = grailsSettings.classesDir.toURI().toURL()
    URL pluginClassesDir = grailsSettings.pluginClassesDir.toURI().toURL()
    URL resourcesDir = grailsSettings.resourcesDir.toURI().toURL()

    def urls = [classesDir, pluginClassesDir, resourcesDir] as URL[]

    Thread.currentThread().setContextClassLoader(new URLClassLoader(urls, rootLoader))
}
