package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NavigationBarTopPaneTests extends AbstractP1RATTestCase {

    public void testView() {
        Thread.sleep 10000
    }

    ULCComponent createContentPane() {
        NavigationBarTopPane navigationBarTopPane = new NavigationBarTopPane()
        navigationBarTopPane.init()
        return navigationBarTopPane.content;
    }


}
