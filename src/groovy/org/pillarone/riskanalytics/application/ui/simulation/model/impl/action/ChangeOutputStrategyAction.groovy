package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import com.ulcjava.base.application.ULCTextField
import org.pillarone.riskanalytics.core.output.FileOutput

/**
 * Action which is used when the output strategy selection is changed.
 */
class ChangeOutputStrategyAction implements IActionListener {

    private SimulationSettingsPaneModel model
    private Closure action

    /**
     * @param action A closure which takes one argument (boolean whether result location fields are required for this strategy) which is executed when the strategy is changed.
     */
    public ChangeOutputStrategyAction(SimulationSettingsPaneModel model, Closure action) {
        this.model = model
        this.action = action
    }

    void actionPerformed(ActionEvent actionEvent) {
        ICollectorOutputStrategy strategy = model.outputStrategies.getStrategy()
        action.call(strategy instanceof FileOutput)
    }


}
