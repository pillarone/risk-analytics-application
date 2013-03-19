package org.pillarone.riskanalytics.application.ui.result.action

import org.pillarone.riskanalytics.application.ui.result.model.ResultIterationDataViewModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ShowIterationSingleValueAction extends ShowSingleValueCollectorAction {
    List nodes

    public ShowIterationSingleValueAction(ResultIterationDataViewModel iterationDataViewModel, int iteration) {
        super("ShowSingleValueCollector");
        this.tabbedPane = iterationDataViewModel.resultView.tabbedPane
        this.simulationRun = iterationDataViewModel.simulationRun
        this.nodes = iterationDataViewModel.nodes
        this.iteration = iteration
    }

    List getNodes() {
        return this.nodes
    }
}
