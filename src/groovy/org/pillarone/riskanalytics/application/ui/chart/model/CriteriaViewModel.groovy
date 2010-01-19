package org.pillarone.riskanalytics.application.ui.chart.model

import com.ulcjava.base.application.DefaultComboBoxModel
import com.ulcjava.base.application.IComboBoxModel
import org.pillarone.riskanalytics.application.ui.base.model.EnumComboBoxModel
import org.pillarone.riskanalytics.application.ui.chart.model.QueryPaneModel
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor

class CriteriaViewModel {
    QueryPaneModel queryModel
    IComboBoxModel keyFigureTypeModel
    IComboBoxModel comparatorModel = new DefaultComboBoxModel()
    double value = 99
    IComboBoxModel valueIntepretationModel = new DefaultComboBoxModel()
    DefaultComboBoxModel periodModel
    boolean enablePeriodComboBox

    public CriteriaViewModel(QueryPaneModel queryModel, boolean enablePeriodComboBox = true) {
        this.@queryModel = queryModel
        this.@enablePeriodComboBox = enablePeriodComboBox
        keyFigureTypeModel = new DefaultComboBoxModel(queryModel.shortPaths)
        comparatorModel = new EnumComboBoxModel(CriteriaComparator.values() as Object[], CriteriaComparator.GREATER_EQUALS, false)
        valueIntepretationModel = new EnumComboBoxModel(ValueIntepretationType.values() as Object[], ValueIntepretationType.PERCENTILE, true)
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

    public double getIntepretedValue() {
        if (valueIntepretationModel.getSelectedEnum() == ValueIntepretationType.ABSOLUTE) {
            return this.@value
        } else {
            return ResultAccessor.getPercentile(queryModel.simulationRun, selectedPeriod, selectedPath, collector, field, this.@value)
        }
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

public enum ValueIntepretationType {
    ABSOLUTE, PERCENTILE
}
