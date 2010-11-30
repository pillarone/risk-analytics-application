package org.pillarone.riskanalytics.application.ui.chart.model

import com.ulcjava.base.application.DefaultComboBoxModel
import com.ulcjava.base.application.IComboBoxModel
import org.pillarone.riskanalytics.application.ui.base.model.EnumComboBoxModel
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor

class CriteriaViewModel {
    QueryPaneModel queryModel
    IComboBoxModel keyFigureTypeModel
    IComboBoxModel comparatorModel = new DefaultComboBoxModel()
    double value = 99
    IComboBoxModel valueInterpretationModel = new DefaultComboBoxModel()
    DefaultComboBoxModel periodModel
    boolean enablePeriodComboBox

    public CriteriaViewModel(QueryPaneModel queryModel, boolean enablePeriodComboBox = true) {
        this.@queryModel = queryModel
        this.@enablePeriodComboBox = enablePeriodComboBox
        keyFigureTypeModel = new DefaultComboBoxModel(queryModel.shortPaths)
        comparatorModel = new EnumComboBoxModel(CriteriaComparator.values() as Object[], CriteriaComparator.GREATER_EQUALS, false)
        valueInterpretationModel = new EnumComboBoxModel(ValueInterpretationType.values() as Object[], ValueInterpretationType.ORDER_STATISTIC, true)
        createPeriodModel()
        selectedPeriod = queryModel.defaultPeriod
    }

    public void remove() {
        queryModel.removeCriteria(this)
    }

    public String getSelectedPath() {
        queryModel.longPaths[keyFigureTypeModel.selectedItem]
    }

    public String getField() {
        queryModel.fields[keyFigureTypeModel.selectedItem]
    }

    public String getCollector() {
        queryModel.collectors[keyFigureTypeModel.selectedItem]
    }

    public void setSelectedPath(String path) {
        keyFigureTypeModel.setSelectedItem(path)
    }

    public CriteriaComparator getSelectedComparator() {
        return comparatorModel.getSelectedEnum()
    }

    public void setSelectedComparator(CriteriaComparator comparator) {
        comparatorModel.selectedEnum = comparator
    }

    public double getInterpretedValue() {
        switch (valueInterpretationModel.selectedEnum) {
            case ValueInterpretationType.ABSOLUTE:
                return this.@value
            case ValueInterpretationType.PERCENTILE:
                return ResultAccessor.getPercentile(queryModel.simulationRun, selectedPeriod, selectedPath, collector, field, this.@value)
            case ValueInterpretationType.ORDER_STATISTIC:
                boolean countingFromLowerEnd = comparatorModel.selectedEnum == CriteriaComparator.LESS_THAN && comparatorModel.selectedEnum == CriteriaComparator.LESS_EQUALS
                return ResultAccessor.getNthOrderStatistic(queryModel.simulationRun, selectedPeriod, selectedPath, collector, field, this.@value, countingFromLowerEnd)
        }
    }

    public boolean validate() {
        if (valueInterpretationModel.getSelectedEnum() != ValueInterpretationType.ABSOLUTE) {
            if (value < 0 || value > 100) return false
        }
        return true
    }

    public void setValue(String s) {
        value = Double.parseDouble(s)
    }

    public void setValue(double d) {
        value = d
    }

    public def getSelectedPeriod() {
        Object selectedString = periodModel.getSelectedItem()
        if (selectedString == "in all periods") { return null }
        return queryModel.getPeriodNumber(selectedString)
    }

    public void setSelectedPeriod(int period) {
        periodModel.selectedItem = queryModel.getPeriodLabel(period)
    }

    private void createPeriodModel() {
        periodModel = new DefaultComboBoxModel()
        queryModel.simulationRun.periodCount.times {
            periodModel.addElement queryModel.getPeriodLabel(it)
        }
        periodModel.addElement "in all periods"
    }
}


public enum CriteriaComparator {
    LESS_THAN("<"),
    LESS_EQUALS("<="),
    EQUALS("="),
    GREATER_THAN(">"),
    GREATER_EQUALS(">=")

    private String displayName

    private CriteriaComparator(String displayName) {
        this.@displayName = displayName
    }


    public String toString() {
        return displayName
    }
}

public enum ValueInterpretationType {
    ABSOLUTE, ORDER_STATISTIC, PERCENTILE
}
