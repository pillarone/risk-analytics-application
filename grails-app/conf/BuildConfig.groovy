//Use a custom plugins dir, because different branches use different plugin versions
grails.project.plugins.dir = "../local-plugins/RiskAnalyticsApplication-1.9.x"
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
            updatePolicy System.getProperty('snapshotUpdatePolicy') ?: 'daily'
        }
        mavenRepo "http://repo.spring.io/milestone/" //needed for spring-security-core 2.0-rc2 plugin
        mavenRepo "https://ci.canoo.com/nexus/content/repositories/public-releases"
    }

    String ulcVersion = "7.2.0.6"

    plugins {
        runtime ":background-thread:1.3"
        runtime ":hibernate:3.6.10.3"
        runtime ":release:3.0.1", {
            excludes "groovy"
        }
        runtime ":quartz:1.0.1"
        runtime ":spring-security-core:2.0-RC2"

        compile "com.canoo:ulc:${ulcVersion}"
        runtime("org.pillarone:pillar-one-ulc-extensions:1.7") { transitive = false }

        test ":code-coverage:1.2.7"
        test ":codenarc:0.20"

        if (appName == 'RiskAnalyticsApplication') {
            runtime "org.pillarone:risk-analytics-core:1.9.x-SNAPSHOT"
        }

    }

    dependencies {
        runtime "mysql:mysql-connector-java:5.1.20"
        compile(group: 'com.canoo.ulc.ext.ULCMigLayout', name: 'ULCMigLayout-client', version: "1.0") {
            transitive = false
        }
        compile group: 'com.miglayout', name: 'miglayout', version: "3.7.3.1"

        //required for ulc tests
        test 'org.mortbay.jetty:jetty:6.1.21', 'org.mortbay.jetty:jetty-plus:6.1.21'
        test 'org.mortbay.jetty:jetty-util:6.1.21', 'org.mortbay.jetty:jetty-naming:6.1.21'
        test 'hsqldb:hsqldb:1.8.0.10'

        test("org.grails:grails-plugin-testing:2.2.3.FIXED")
        test("org.springframework:spring-test:3.2.4.RELEASE")

        //see http://jira.grails.org/browse/GRAILS-10671
        build "com.lowagie:itext:2.1.7"
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

//grails.plugin.location.'risk-analytics-core' = "../risk-analytics-core"

codenarc.maxPriority1Violations = 0
codenarc.maxPriority2Violations = 0
codenarc.maxPriority3Violations = 0

codenarc.properties = {
    MisorderedStaticImports.enabled = false
    FactoryMethodName.enabled = false
    CatchException.enabled = false
    SerializableClassMustDefineSerialVersionUID.enabled = false
    ClassJavadoc.enabled = false

    //domain
    GrailsDomainHasEquals.enabled = false
    GrailsDomainHasToString.enabled = false

    //formatting
    SpaceAroundMapEntryColon.enabled = false
    SpaceBeforeOpeningBrace.enabled = false
    SpaceAfterIf.enabled = false
    //TODO discuss about rules together
//    GrailsPublicControllerMethod.enabled = false
//    SimpleDateFormatMissingLocale.enabled = false
//    ThrowRuntimeException.enabled = false
//    CatchThrowable.enabled = false
//    CatchRuntimeException.enabled = false
//    ThrowException.enabled = false
//    ReturnNullFromCatchBlock.enabled = false


    LineLength.length = 200

    def allTestClasses = testClasses().join(', ')

    MethodName.doNotApplyToClassNames = allTestClasses
    AbcComplexity.doNotApplyToClassNames = allTestClasses
}

private List testClasses() {
    List result = []
    new File('./test/').eachFileRecurse { File file ->
        String name = file.name
        if (name.endsWith('Tests.groovy')) {
            result << name.replaceAll('.groovy', '')
        }
    }
    result
}

codenarc.reports = {
    Jenkins('xml') {
        outputFile = 'target/code-analysis/CodeNarcReport.xml'
        title = 'Code Narc Code Report'
    }
    LocalReport('html') {
        outputFile = 'target/code-analysis/CodeNarcReport.html'
        title = 'Code Narc Code Report'
    }
}

codenarc.ruleSetFiles = [
        'rulesets/basic.xml',
        'rulesets/braces.xml',
        'rulesets/concurrency.xml',
        'rulesets/design.xml',
        'rulesets/exceptions.xml',
        'rulesets/formatting.xml',
        'rulesets/grails.xml',
        'rulesets/imports.xml',
        'rulesets/jdbc.xml',
        'rulesets/junit.xml',
        'rulesets/logging.xml',
        'rulesets/naming.xml',
        'rulesets/security.xml',
        'rulesets/serialization.xml',
        'rulesets/size.xml',
        'rulesets/unnecessary.xml',
        'rulesets/unused.xml'].join(',').toString()

