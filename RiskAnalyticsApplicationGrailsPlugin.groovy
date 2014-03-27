import com.ulcjava.applicationframework.application.ApplicationContext
import org.codehaus.groovy.grails.commons.spring.BeanConfiguration
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.model.UserDependentNavigationTreeModelFactory
import org.pillarone.riskanalytics.application.ui.main.view.*
import org.pillarone.riskanalytics.application.ui.search.CacheItemEventQueue
import org.pillarone.riskanalytics.application.ui.search.CacheItemEventQueue
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationQueueEventService
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationQueueViewModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SimulationQueueView
import org.springframework.beans.factory.config.CustomScopeConfigurer

import static UlcSessionScope.ULC_SESSION_SCOPE

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
        ulcSessionScope(UlcSessionScope)
        customScopeConfigurer(CustomScopeConfigurer) {
            Map<String, Object> scopeMap = new HashMap<String, Object>()
            scopeMap[ULC_SESSION_SCOPE] = ref('ulcSessionScope')
            scopes = scopeMap
        }
        Closure ulcScopeWired = { BeanConfiguration conf ->
            conf.scope = ULC_SESSION_SCOPE
            conf.autowire = 'byName'
        }
        simulationQueueEventService(SimulationQueueEventService) { ulcScopeWired(it) }
        riskAnalyticsMainModel(RiskAnalyticsMainModel) { ulcScopeWired(it) }
        riskAnalyticsMainView(RiskAnalyticsMainView) { ulcScopeWired(it) }
        modelIndependentDetailView(ModelIndependentDetailView) { ulcScopeWired(it) }
        selectionTreeView(SelectionTreeView) { ulcScopeWired(it) }
        headerView(HeaderView) { ulcScopeWired(it) }
        cardPaneManager(CardPaneManager) { ulcScopeWired(it) }
        simulationQueueViewModel(SimulationQueueViewModel) { ulcScopeWired(it) }
        simulationQueueView(SimulationQueueView) { ulcScopeWired(it) }
        userDependentNavigationTreeModelFactory(UserDependentNavigationTreeModelFactory)
        ulcApplicationContext(ApplicationContext) { ulcScopeWired(it) }
        navigationTableTreeModel(userDependentNavigationTreeModelFactory: 'createModel') { ulcScopeWired(it) }
        navigationTableTreeModelQueue(CacheItemEventQueue) { ulcScopeWired(it) }
    }

    def doWithDynamicMethods = { ctx -> }

    def doWithApplicationContext = { applicationContext -> }

    def onChange = { event -> }

    def onConfigChange = { event -> }
}

