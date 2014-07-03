import com.ulcjava.applicationframework.application.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.BeanConfiguration
import org.pillarone.riskanalytics.application.ui.PollingSupport
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.model.UserDependentNavigationTreeModelFactory
import org.pillarone.riskanalytics.application.ui.util.DefaultResourceBundleResolver
import org.springframework.beans.factory.config.CustomScopeConfigurer

import static UlcSessionScope.ULC_SESSION_SCOPE

class RiskAnalyticsApplicationGrailsPlugin {
    // the plugin version
    def version = "1.9.8"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3.2 > *"
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

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        customScopeConfigurer(CustomScopeConfigurer) {
            Map<String, Object> scopeMap = new HashMap<String, Object>()
            scopeMap[ULC_SESSION_SCOPE] = ref(ULC_SESSION_SCOPE)
            scopes = scopeMap
        }
        Closure ulcScopeWired = { BeanConfiguration conf ->
            conf.scope = ULC_SESSION_SCOPE
            conf.autowire = 'byName'
        }
        userDependentNavigationTreeModelFactory(UserDependentNavigationTreeModelFactory)
        navigationTableTreeModel(userDependentNavigationTreeModelFactory: 'createModel') { ulcScopeWired(it) }
        ulcApplicationContext(ApplicationContext) { ulcScopeWired(it) }
        pollingSupport2000(PollingSupport) {
            delay = 2000
            ulcScopeWired(it)
        }
        pollingSupport1000(PollingSupport) {
            delay = 1000
            ulcScopeWired(it)
        }
        resourceBundleResolver(DefaultResourceBundleResolver)
    }

    def doWithDynamicMethods = { ctx -> }

    def doWithApplicationContext = { applicationContext -> }

    def onChange = { event -> }

    def onConfigChange = { event -> }
}

