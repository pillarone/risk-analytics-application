import org.pillarone.riskanalytics.application.example.constraint.CopyPasteConstraint
import org.pillarone.riskanalytics.application.example.constraint.LinePercentage
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.listener.ModellingItemHibernateListener
import org.codehaus.groovy.grails.orm.hibernate.HibernateEventListeners
import org.pillarone.riskanalytics.application.ULCAwareHibernateListener

class RiskAnalyticsApplicationGrailsPlugin {
    // the plugin version
    def version = "1.6-ALPHA-3.2-kti"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def loadAfter = ['riskAnalyticsCore']

    def author = "Intuitive Collaboration AG"
    def authorEmail = "info@pillarone.org"
    def title = "RiskAnalytics application"
    def description = '''\\
ULC view
'''

    def documentation = "http://www.pillarone.org"

    def groupId = "org.pillarone"

    def doWithWebDescriptor = {xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        modellingItemListener(ULCAwareHibernateListener)

        hibernateEventListeners(HibernateEventListeners) {
            listenerMap = ['post-insert': modellingItemListener,
                    'post-update': modellingItemListener,
                    'post-delete': modellingItemListener]
        }
    }

    def doWithDynamicMethods = {ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = {applicationContext ->
        ConstraintsFactory.registerConstraint(new LinePercentage())
        ConstraintsFactory.registerConstraint(new CopyPasteConstraint())
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
