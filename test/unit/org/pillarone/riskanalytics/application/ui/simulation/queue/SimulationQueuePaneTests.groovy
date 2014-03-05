package org.pillarone.riskanalytics.application.ui.simulation.queue

import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.SimulationQueuePaneModel
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue.SimulationQueuePane

/**
 * Created by matthiasa on 04.03.14.
 */
class SimulationQueuePaneTests extends AbstractP1RATTestCase {
    @Override
    ULCComponent createContentPane() {
        def model = new SimulationQueuePaneModel()
        new SimulationQueuePane(model).content
    }


    void testView() {
        sleep(1000000)
    }
}
