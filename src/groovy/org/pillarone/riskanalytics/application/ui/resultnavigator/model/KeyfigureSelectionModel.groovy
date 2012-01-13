package org.pillarone.riskanalytics.application.ui.resultnavigator.model

import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.joda.time.DateTime
import com.ulcjava.base.application.IComboBoxModel
import com.ulcjava.base.application.DefaultComboBoxModel

/**
 * @author martin.melchior
 */
class KeyfigureSelectionModel {

    IComboBoxModel keyfigureModel
    Number keyfigureParameter
    IComboBoxModel periodSelectionModel

    int numOfIterations
    int numOfPeriods
    DateTime startPeriod
    DateTime endPeriod

    KeyfigureSelectionModel(SimulationRun run) {
        numOfIterations = run.iterations
        numOfPeriods = run.periodCount
        startPeriod = run.startTime
        endPeriod = run.endTime

        keyfigureModel = new DefaultComboBoxModel(StatisticsKeyfigure.getNames())
        periodSelectionModel = new DefaultComboBoxModel(0..<numOfPeriods)
    }

    StatisticsKeyfigure getKeyfigure() {
        return StatisticsKeyfigure.getEnumValue((String) keyfigureModel.selectedItem)
    }

    int getPeriod() {
        return (int) periodSelectionModel.selectedItem
    }
}
