import org.pillarone.riskanalytics.application.example.constraint.LinePercentage
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory

class RiskAnalyticsApplicationGrailsPlugin {
    // the plugin version
    def version = "1.0-RC-2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.2.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [
            "ulc": "2008 > *",
            "jasper": "0.9.5-riskanalytics"
    ]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Intuitive Collaboration GmbH"
    def authorEmail = "info@pillarone.org"
    def title = "RiskAnalytics application"
    def description = '''\\
ULC view
'''

    def documentation = "http://www.pillarone.org"

    def doWithWebDescriptor = {xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = {ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = {applicationContext ->
        ConstraintsFactory.registerConstraint(new LinePercentage())
    }

    def onChange = {event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = {event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
