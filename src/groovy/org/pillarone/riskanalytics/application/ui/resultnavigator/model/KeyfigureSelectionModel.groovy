package org.pillarone.riskanalytics.application.ui.resultnavigator.model

import com.ulcjava.base.application.DefaultComboBoxModel
import com.ulcjava.base.application.IComboBoxModel
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ResultAccess
import org.pillarone.riskanalytics.core.output.SimulationRun

/**
 * Data model underlying the panel for selecting the period and the statistics key figure.
 *
 * @author martin.melchior
 */
class KeyfigureSelectionModel {

    IComboBoxModel keyfigureModel
    Number keyfigureParameter
    DefaultComboBoxModel periodSelectionModel

    private int numOfIterations
    private int numOfPeriods
    private DateTime startPeriod
    private DateTime endPeriod
    private List<String> periodLabels

    KeyfigureSelectionModel(SimulationRun run) {
        numOfIterations = run.iterations
        numOfPeriods = run.periodCount
        startPeriod = run.startTime
        endPeriod = run.endTime

        keyfigureModel = new DefaultComboBoxModel(StatisticsKeyfigure.getNames())
        
        periodLabels = ResultAccess.getPeriodLabels(run)
        periodSelectionModel = new DefaultComboBoxModel(periodLabels)
    }

    StatisticsKeyfigure getKeyfigure() {
        return StatisticsKeyfigure.getEnumValue((String) keyfigureModel.selectedItem)
    }

    int getPeriod() {
        return (int) periodSelectionModel.getIndexOf(periodSelectionModel.getSelectedItem())
    }

    int getPeriodIndexForLabel(String label) {
        return periodLabels.indexOf(label)
    }

    String getPeriodLabelForIndex(int periodIndex) {
        return periodLabels[periodIndex]
    }
}
