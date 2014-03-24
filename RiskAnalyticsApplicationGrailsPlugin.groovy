import org.codehaus.groovy.grails.commons.spring.BeanConfiguration
import org.pillarone.riskanalytics.application.ui.ULCScope
import org.pillarone.riskanalytics.application.ui.main.view.*
import org.springframework.beans.factory.config.CustomScopeConfigurer

import static ULCScope.ULC_SCOPE

class RiskAnalyticsApplicationGrailsPlugin {
    // the plugin version
    def version = "1.9-SNAPSHOT"
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
        ulcScope(ULCScope)
        customScopeConfigurer(CustomScopeConfigurer) {
            Map<String, Object> scopeMap = new HashMap<String, Object>()
            scopeMap[ULC_SCOPE] = ref('ulcScope')
            scopes = scopeMap
        }
        Closure ulcScopeWired = { BeanConfiguration conf ->
            conf.scope = ULC_SCOPE
            conf.autowire = 'byName'
        }
        riskAnalyticsMainModel(RiskAnalyticsMainModel) { ulcScopeWired(it) }
        riskAnalyticsMainView(RiskAnalyticsMainView) { ulcScopeWired(it) }
        modelIndependentDetailView(ModelIndependentDetailView) { ulcScopeWired(it) }
        selectionTreeView(SelectionTreeView) { ulcScopeWired(it) }
        headerView(HeaderView) { ulcScopeWired(it) }
        cardPaneManager(CardPaneManager) { ulcScopeWired(it) }
    }

    def doWithDynamicMethods = { ctx -> }

    def doWithApplicationContext = { applicationContext -> }

    def onChange = { event -> }

    def onConfigChange = { event -> }
}

