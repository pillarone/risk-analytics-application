import org.apache.ivy.plugins.resolver.FileSystemResolver

//Use a custom plugins dir, because different branches use different plugin versions
grails.project.plugins.dir = "../local-plugins/RiskAnalyticsApplication-master"

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn"

    repositories {
        grailsHome()
        grailsCentral()
    }

    def ulcClientJarResolver = new FileSystemResolver()
    String absolutePluginDir = grailsSettings.projectPluginsDir.absolutePath

    ulcClientJarResolver.addArtifactPattern "${absolutePluginDir}/ulc-[revision]/web-app/lib/[artifact].[ext]"
    ulcClientJarResolver.addArtifactPattern "${basedir}/web-app/lib/[artifact]-[revision].[ext]"
    ulcClientJarResolver.name = "ulc"

    resolver ulcClientJarResolver

    mavenRepo "https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public/"
    mavenRepo "https://ci.canoo.com/nexus/content/repositories/public-releases"

    String ulcVersion = "ria-suite-u2-P1"

    plugins {
        runtime ":background-thread:1.3"
        runtime ":hibernate:1.3.7"
        runtime ":joda-time:0.5"
        runtime ":maven-publisher:0.7.5"
        runtime ":quartz:0.4.2"
        runtime ":spring-security-core:1.1.2"
        runtime ":jetty:1.2-SNAPSHOT"

        compile "com.canoo:ulc:${ulcVersion}"
        runtime ("org.pillarone:pillar-one-ulc-extensions:0.2")  {  transitive = false }

        test ":code-coverage:1.2.4"

        if (appName == 'RiskAnalyticsApplication') {
            runtime "org.pillarone:risk-analytics-core:1.5-ALPHA-3.8"
        }

    }

    dependencies {
        compile group: 'canoo', name: 'ulc-applet-client', version: ulcVersion
        compile group: 'canoo', name: 'ulc-base-client', version: ulcVersion
        compile group: 'canoo', name: 'ulc-base-trusted', version: ulcVersion
        compile group: 'canoo', name: 'ulc-jnlp-client', version: ulcVersion
        compile group: 'canoo', name: 'ulc-servlet-client', version: ulcVersion
        compile group: 'canoo', name: 'ulc-standalone-client', version: ulcVersion
        compile group: 'canoo', name: 'ULCMigLayout-client', version: "1.0"
        compile group: 'canoo', name: 'miglayout', version: "3.7.3.1"
    }

}

grails.project.dependency.distribution = {
    String password = ""
    String user = ""
    String scpUrl = ""
    try {
        Properties properties = new Properties()
        properties.load(new File("${userHome}/deployInfo.properties").newInputStream())

        user = properties.get("user")
        password = properties.get("password")
        scpUrl = properties.get("url")
    } catch (Throwable t) {
    }
    remoteRepository(id: "pillarone", url: scpUrl) {
        authentication username: user, password: password
    }
}

coverage {
    exclusions = [
            'models/**',
            '**/*Test*',
            '**/com/energizedwork/grails/plugins/jodatime/**',
            '**/grails/util/**',
            '**/org/codehaus/**',
            '**/org/grails/**',
            '**GrailsPlugin**',
            '**TagLib**'
    ]

}

reportFolders = [new File("./src/java/reports")]
