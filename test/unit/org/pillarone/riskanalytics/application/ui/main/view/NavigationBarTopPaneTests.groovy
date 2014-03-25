package org.pillarone.riskanalytics.application.ui.main.view
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCToolBar
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import grails.util.Holders
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.support.MockApplicationContext
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.core.modellingitem.CacheItemHibernateListener
import org.pillarone.riskanalytics.core.search.CacheItemEventQueueService
import org.pillarone.riskanalytics.core.search.CacheItemSearchService
import org.springframework.context.ApplicationContext

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NavigationBarTopPaneTests extends AbstractP1RATTestCase {

    public void testView() {
        ULCTextFieldOperator textField = getTextFieldOperator("searchText")
        assertNotNull textField
        textField.clearText()
        textField.typeText("test")
        assertTrue textField.getText() == "test"

        ULCButtonOperator clearButton = getButtonOperator("clearButton")
        assertNotNull clearButton
        clearButton.getFocus()
        clearButton.clickMouse()

        assertTrue textField.getText() != "test"
    }

    @Override
    ULCComponent createContentPane() {
        mockApplicationContext()
        NavigationBarTopPane topPane = new NavigationBarTopPane(new ULCToolBar(), getMockTreeModel(mockRiskAnalyticsMainModel))
        topPane.metaClass.isStandAlone = {-> false}
        topPane.metaClass.getLoggedUser = {-> null}
        topPane.init()
        return topPane.toolBar
    }


    private void mockApplicationContext() {
        ApplicationContext mainContext = new MockApplicationContext()
        def service = new CacheItemSearchService()
        mainContext.registerMockBean("cacheItemSearchService", service)
        def cacheItemEventQueueService = new CacheItemEventQueueService(cacheItemListener: new CacheItemHibernateListener())
        cacheItemEventQueueService.init()
        mainContext.registerMockBean("cacheItemEventQueueService", cacheItemEventQueueService)
        GrailsApplication application = new DefaultGrailsApplication(mainContext: mainContext)
        Holders.grailsApplication = application
    }
}
