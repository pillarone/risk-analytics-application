package org.pillarone.riskanalytics.application.ui.main.view
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCToolBar
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.P1UnitTestMixin
import org.pillarone.riskanalytics.application.util.LocaleResources

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@TestMixin(GrailsUnitTestMixin)
@Mixin(P1UnitTestMixin)
class NavigationBarTopPaneTests extends AbstractSimpleStandaloneTestCase {

    public void testView() {
        ULCTextFieldOperator textField = getTextFieldOperator("searchText")
        assertNotNull textField
        textField.clearText()
        textField.typeText("test")
        assertTrue textField.text == "test"

        ULCButtonOperator clearButton = getButtonOperator("clearButton")
        assertNotNull clearButton
        clearButton.getFocus()
        clearButton.clickMouse()
        assertTrue textField.text != "test"
    }

    @Override
    void start() {
        inTestFrame(createContentPane())
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp()
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
    }

    ULCComponent createContentPane() {
        LocaleResources.testMode = true
        ULCToolBar toolbar = new ULCToolBar()
        NavigationBarTopPane topPane = new NavigationBarTopPane(toolbar, new NavigationTableTreeModel())
        topPane.metaClass.isStandAlone = { -> false }
        topPane.metaClass.getLoggedUser = { -> null }
        topPane.init()
        return toolbar
    }
}
