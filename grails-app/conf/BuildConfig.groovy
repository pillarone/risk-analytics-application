import org.apache.ivy.plugins.resolver.FileSystemResolver

//Use a custom plugins dir, because different branches use different plugin versions
grails.project.plugins.dir = "../local-plugins/RiskAnalyticsApplication-master"
grails.project.target.level = 1.6
grails.project.source.level = 1.6

grails.project.dependency.resolver = "maven"
grails.project.fork = [test: false, run: false]

grails.project.dependency.resolution = {
    inherits("global") { // inherit Grails' default dependencies
        excludes "grails-plugin-testing"
    }
    log "warn"

    repositories {
        grailsHome()
        grailsCentral()
        mavenCentral()

        mavenRepo "https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public/"
        mavenRepo("https://repository.intuitive-collaboration.com/nexus/content/repositories/pillarone-public-snapshot/") {
            interval: System.hasProperty('snapshotUpdatePolicy') ? System.getProperty('snapshotUpdatePolicy') : '1d'
        }
        mavenRepo "http://repo.spring.io/milestone/" //needed for spring-security-core 2.0-rc2 plugin
        mavenRepo "https://ci.canoo.com/nexus/content/repositories/public-releases"
    }

    String ulcVersion = "ria-suite-2013-2"

    plugins {
        runtime ":background-thread:1.3"
        runtime ":hibernate:3.6.10.3"
        runtime ":joda-time:0.5"
        runtime ":release:3.0.1", {
            excludes "groovy"
        }
        runtime ":quartz:0.4.2"
        runtime ":spring-security-core:2.0-RC2"

        compile "com.canoo:ulc:${ulcVersion}"
        runtime("org.pillarone:pillar-one-ulc-extensions:1.3") { transitive = false }

        test ":code-coverage:1.2.7"

        if (appName == 'RiskAnalyticsApplication') {
            runtime "org.pillarone:risk-analytics-core:1.9-SNAPSHOT"
        }

    }

    dependencies {
        runtime "mysql:mysql-connector-java:5.1.20"
        compile(group: 'com.canoo.ulc.ext.ULCMigLayout', name: 'ULCMigLayout-client', version: "1.0") { transitive = false }
        compile group: 'com.miglayout', name: 'miglayout', version: "3.7.3.1"

        //required for ulc tests
        test 'org.mortbay.jetty:jetty:6.1.21', 'org.mortbay.jetty:jetty-plus:6.1.21'
        test 'org.mortbay.jetty:jetty-util:6.1.21', 'org.mortbay.jetty:jetty-naming:6.1.21'
        test 'hsqldb:hsqldb:1.8.0.10'

        test("org.grails:grails-plugin-testing:2.2.3.FIXED")
        test("org.springframework:spring-test:3.2.4.RELEASE")
    }

}

grails.project.dependency.distribution = {
    String password = ""
    String user = ""
    String scpUrl = ""
    try {
        Properties properties = new Properties()
        String version = new GroovyClassLoader().loadClass('RiskAnalyticsApplicationGrailsPlugin').newInstance().version
        properties.load(new File("${userHome}/deployInfo.properties").newInputStream())
        user = properties.get("user")
        password = properties.get("password")

        if (version?.endsWith('-SNAPSHOT')) {
            scpUrl = properties.get("urlSnapshot")
        } else {
            scpUrl = properties.get("url")
        }
        remoteRepository(id: "pillarone", url: scpUrl) {
            authentication username: user, password: password
        }
    } catch (Throwable t) {
        println "deployInfo.properties not found. $t.message"
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
