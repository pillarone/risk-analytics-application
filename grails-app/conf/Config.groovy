import org.pillarone.riskanalytics.core.output.batch.results.MysqlBulkInsert
import org.pillarone.riskanalytics.core.output.batch.results.SQLServerBulkInsert
import org.pillarone.riskanalytics.core.output.batch.calculations.MysqlCalculationsBulkInsert
import grails.plugin.springsecurity.SecurityConfigType
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.LoggingAppender
import org.pillarone.riskanalytics.core.example.component.ExampleResource
import org.pillarone.riskanalytics.application.example.resource.ApplicationResource
import org.pillarone.riskanalytics.core.simulation.engine.grid.mapping.OneNodeStrategy
import org.apache.log4j.PatternLayout
import org.pillarone.riskanalytics.core.log.TraceAppender

grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text-plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
grails.doc.images = new File('src/docs/images')

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
grails.spring.bean.packages = ['org.pillarone']

maxIterations = 100000
keyFiguresToCalculate = null
resultBulkInsert = null
userLogin = false
// a cron for a batch, A cron expression is a string comprised of 6 or 7 fields separated by white space.
// Fields can contain any of the allowed values: Sec Min Hour dayOfMonth month dayOfWeek Year
// Fire every 60 minutes
batchCron = "0 0/10 * * * ?"
transactionServiceUrl = "rmi://localhost:1099/TransactionService"
resultServiceRegistryPort = 1099
transactionsEnabled = true

serverSessionPrefix = ";jsessionid="
grails.serverURL = "http://localhost:${System.getProperty("server.port", "8080")}/${appName}"

log4j = {
    appenders {

        String layoutPattern = "[%d{dd.MMM.yyyy HH:mm:ss,SSS}] - %t (%X{username}) - %-5p %c{1} %m%n"

        console name: 'stdout', layout: pattern(conversionPattern: layoutPattern)

        LoggingAppender loggingAppender = LoggingAppender.getInstance()
        loggingAppender.setName('application')
        loggingAppender.loggingManager.layout = "[%d{HH:mm:ss,SSS}] - %c{1} %m%n"
        appender loggingAppender

        TraceAppender traceAppender = new TraceAppender(name: "traceAppender")
        traceAppender.layout = new PatternLayout("[%d{dd.MMM.yyyy HH:mm:ss,SSS}] - %-5p %c{1} %m%n")
        appender traceAppender

    }
    root {
        error()
        additivity = false
    }

    def infoPackages = [
            'org.pillarone.riskanalytics',
    ]

    def debugPackages = [
            'org.pillarone.riskanalytics.core.fileimport',
    ]

    info(
            traceAppender: infoPackages,
            application: infoPackages,
            stdout: infoPackages,
            additivity: false
    )

    debug(
            application: debugPackages,
            stdout: debugPackages,
            additivity: false
    )
    debug(
            traceAppender: [
                    'org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem',
                    'org.pillarone.riskanalytics.application.ui',
            ],
            additivity: true
    )
}

environments {
    development {
//        models = ["CoreModel", "ResourceModel", 'ApplicationModel', 'DeterministicApplicationModel', 'MigratableCoreModel']
        models = ["CoreModel", 'ApplicationModel']
        includedResources = [ExampleResource, ApplicationResource]
        ExceptionSafeOut = System.out
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200,
                'percentileProfitFunction': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'varProfitFunction': [99, 99.5],
                'tvarProfitFunction': [99, 99.5]
        ]
    }
    mysql {
//        models = ["CoreModel", "ResourceModel", 'ApplicationModel', 'DeterministicApplicationModel', 'MigratableCoreModel']
        models = ["CoreModel", 'ApplicationModel']
        includedResources = [ExampleResource, ApplicationResource]
        ExceptionSafeOut = System.out
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200,
                'percentileProfitFunction': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'varProfitFunction': [99, 99.5],
                'tvarProfitFunction': [99, 99.5]
        ]
    }
    test {
        ExceptionSafeOut = System.out
        includedResources = [ExampleResource]
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200,
                'percentileProfitFunction': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'varProfitFunction': [99, 99.5],
                'tvarProfitFunction': [99, 99.5]
        ]
        nodeMappingStrategy = OneNodeStrategy
        log4j = {
            appenders {

                String layoutPattern = "[%d{dd.MMM.yyyy HH:mm:ss,SSS}] - %t (%X{username}) - %-5p %c{1} %m%n"

                console name: 'stdout', layout: pattern(conversionPattern: layoutPattern)

                LoggingAppender loggingAppender = LoggingAppender.getInstance()
                loggingAppender.setName('application')
                loggingAppender.loggingManager.layout = "[%d{HH:mm:ss,SSS}] - %c{1} %m%n"
                appender loggingAppender

            }
            root {
                error()
                additivity = false
            }

            def packages = [
                    'org.pillarone.riskanalytics',
            ]
            info(
                    application: packages,
                    additivity: false
            )
            error(
                    stdout: packages,
                    additivity: false
            )
        }
    }
    sqlserver {
        models = ["CoreModel", 'ApplicationModel']
        resultBulkInsert = SQLServerBulkInsert
        calculationBulkInsert = SQLServerCalculationBulkInsert
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200,
                'percentileProfitFunction': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'varProfitFunction': [99, 99.5],
                'tvarProfitFunction': [99, 99.5]
        ]
        log4j = {
            info 'org.pillarone.riskanalytics'
        }
    }
    production {
        userLogin = true
        maxIterations = 10000
        models = ["CoreModel", 'ApplicationModel']
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200,
                'percentileProfitFunction': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'varProfitFunction': [99, 99.5],
                'tvarProfitFunction': [99, 99.5]
        ]
    }
}

grails {
    plugin {
        springsecurity {
            logout.invalidateHttpSession = false
            userLookup {
                userDomainClassName = 'org.pillarone.riskanalytics.core.user.Person'
                authorityJoinClassName = 'org.pillarone.riskanalytics.core.user.PersonAuthority'
            }
            authority {
                className = 'org.pillarone.riskanalytics.core.user.Authority'
            }
            securityConfigType = SecurityConfigType.InterceptUrlMap
            interceptUrlMap = [
                    '/login/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/**/css/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/**/js/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/**/images/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/**/*.jar': ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/ulcserverendpoint/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/css/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/person/**': ['ROLE_ADMIN'],
                    '/authority/**': ['ROLE_ADMIN'],
                    '/**': ['IS_AUTHENTICATED_REMEMBERED'],
            ]

        }
    }
}
// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
