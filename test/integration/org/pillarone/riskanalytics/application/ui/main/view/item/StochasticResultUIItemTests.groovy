package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCFrameOperator

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class StochasticResultUIItemTests extends AbstractResultUIItemTests {

    public void testView() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        assertNotNull frameOperator
        Thread.sleep 5000
    }



}
