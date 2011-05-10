import org.pillarone.riskanalytics.core.output.batch.results.MysqlBulkInsert
import org.pillarone.riskanalytics.core.output.batch.results.SQLServerBulkInsert
import org.pillarone.riskanalytics.core.output.batch.calculations.MysqlCalculationsBulkInsert
import grails.plugins.springsecurity.SecurityConfigType
import org.pillarone.riskanalytics.application.logging.model.LoggingAppender

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

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

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

environments {
    development {
        models = ["CoreModel", 'ApplicationModel', 'DeterministicApplicationModel', 'MigratableCoreModel']

        ExceptionSafeOut = System.out
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

            def infoPackages = [
                    'org.pillarone.riskanalytics',
            ]

            def debugPackages = [
                    'org.pillarone.riskanalytics.core.fileimport'
            ]

            info(
                    application: infoPackages,
                    stdout: infoPackages,
                    additivity: false
            )

            debug(
                    application: debugPackages,
                    stdout: debugPackages,
                    additivity: false
            )

        }
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
    sqlserver {
        models = ["CoreModel", 'ApplicationModel']
        resultBulkInsert = SQLServerBulkInsert
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
    mysql {
        resultBulkInsert = MysqlBulkInsert
        calculationBulkInsert = MysqlCalculationsBulkInsert
        ExceptionSafeOut = System.out
        models = ["CoreModel", 'ApplicationModel']
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

            def infoPackages = [
                    'org.pillarone.riskanalytics',
            ]

            def debugPackages = [
                    'org.pillarone.riskanalytics.core.fileimport'
            ]

            info(
                    application: infoPackages,
                    stdout: infoPackages,
                    additivity: false
            )

            debug(
                    application: debugPackages,
                    stdout: debugPackages,
                    additivity: false
            )

        }
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
    plugins {
        springsecurity {
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

log4j = {
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '[%d] %-5p %c{1} %m%n')
        file name: 'file', file: 'RiskAnalytics.log', layout: pattern(conversionPattern: '[%d] %-5p %c{1} %m%n')
    }
    root {
        error 'stdout', 'file'
        additivity = false
    }
    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
//        'org.springframework',
//        'org.hibernate',
            'org.pillarone.modelling.fileimport',
            'org.pillarone.modelling.ui.util.ExceptionSafe',
            'org.pillarone.riskanalytics.core.wiring',
            'org.pillarone.modelling.domain',
            'org.pillarone.modelling.util'
    info()
    debug()
    warn()
}

//log4j.logger.org.springframework.security='off,stdout'