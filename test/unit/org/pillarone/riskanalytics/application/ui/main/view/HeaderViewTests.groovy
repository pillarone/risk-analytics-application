package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class HeaderViewTests extends AbstractP1RATTestCase {

    public void testView() {
//        Thread.sleep 10000
    }

    @Override
    ULCComponent createContentPane() {
        HeaderView headerView = new HeaderView(null, new RiskAnalyticsMainModel(null))
        return headerView.content
    }


}
