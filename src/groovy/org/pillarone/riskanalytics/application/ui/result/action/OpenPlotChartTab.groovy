package org.pillarone.riskanalytics.application.ui.result.action

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.result.action.OpenChartTab
import org.pillarone.riskanalytics.application.ui.result.view.ChartType
import org.pillarone.riskanalytics.application.ui.util.I18NAlert

class OpenPlotChartTab extends OpenChartTab {
    final static int MAX_ITERATION = 10000

    public OpenPlotChartTab(ULCCloseableTabbedPane tabbedPane, String key, ChartType chartType, SimulationRun simulationRun, def rowHeaderTableTree) {
        super(tabbedPane, key, chartType, simulationRun, rowHeaderTableTree)
    }

    public void doActionPerformed(ActionEvent event) {
        if (simulationRun.getIterations() > MAX_ITERATION) {
            I18NAlert alert = new I18NAlert(tabbedPane.rootPane, "MaxIterationInPlot")
            alert.show()
        } else {
            super.doActionPerformed(event)
        }
    }
}
