includeTargets << grailsScript("_GrailsWar")
includeTargets << grailsScript("_GrailsPackage")

stagingDir = null

target(jarMain: '''
Compiles a grails application and moves the output to the specified directory and optionally creates
a JAR file.
The directory is structured in a way that a grails application can locally be run without
the need for a grails executable. Web Content (i.e. views) is not completely included.
''') {
    depends(parseArguments, packageApp)

    stagingDir = argsMap.destination
    if (!stagingDir) {
        throw new RuntimeException("no destination dir set")
    }

    ant.mkdir(dir: stagingDir)

    ant.copy(todir: stagingDir, overwrite: true) {
        fileset(dir: "${basedir}/web-app/WEB-INF", includes: "applicationContext.xml")
    }

    ant.copy(todir: "${stagingDir}") {
        fileset(dir: classesDirPath) {
            exclude(name: "hibernate")
            exclude(name: "spring")
            exclude(name: "hibernate/*")
            exclude(name: "spring/*")
        }
        fileset(dir: resourcesDirPath, includes: "**/*")
    }

    ant.mkdir(dir: "${stagingDir}/WEB-INF")
    createDescriptor()

    if (argsMap.buildJar) {

        String mainClass = argsMap.mainClass
        if (!mainClass) return

        String jarTarget = "${projectTargetDir}/jar"

        def externalLibsTarget = "${jarTarget}/lib"
        ant.mkdir(dir: externalLibsTarget)
        ant.copy(todir: externalLibsTarget, flatten: true) {
            fileset(dir: './lib', includes: '*.jar')
            fileset(dir: pluginsDirPath, includes: '**/lib/*.jar')
            fileset(dir: grailsHome, includes: 'lib/*.jar dist/*.jar')
        }
        StringBuilder classPath = new StringBuilder()
        new File(externalLibsTarget).eachFileRecurse {File file ->
            classPath << "lib/${file.name} "
        }
        String manifestTarget = "${jarTarget}/MANIFEST.MF"
        ant.manifest(file: manifestTarget) {
            attribute(name: "Main-Class", value: mainClass)
            attribute(name: "Class-Path", value: classPath.toString())
            section(name: "Grails Application") {
                attribute(name: "Implementation-Title", value: "${grailsAppName}")
                attribute(name: "Implementation-Version", value: "${metadata.getApplicationVersion()}")
                attribute(name: "Grails-Version", value: "${metadata.getGrailsVersion()}")
            }
        }
        ant.jar(destfile: "${jarTarget}/${grailsAppName}.jar", basedir: stagingDir, manifest: manifestTarget)
        ant.delete(file: manifestTarget)
    }

}

setDefaultTarget('jarMain')