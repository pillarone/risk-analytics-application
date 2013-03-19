package org.pillarone.riskanalytics.application.ui.base

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.testframework.operator.ULCComponentOperator
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.base.view.FollowLinkPane

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class FollowLinkPaneTests extends AbstractP1RATTestCase {
    FollowLinkPane pane

    ULCComponent createContentPane() {
        pane = new FollowLinkPane()
        pane.name = "linkPane"
        pane.setText("<a href='http://java.sun.com'>http://java.sun.com/</a>")
        return pane
    }

    public void testShow() {
        ULCComponentOperator linkPaneOperator = getComponentOperatorByName("linkPane")
        assertNotNull linkPaneOperator
    }
}
