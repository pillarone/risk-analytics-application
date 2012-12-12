package org.pillarone.riskanalytics.application.ui.chart.model

import com.ulcjava.base.application.DefaultComboBoxModel
import com.ulcjava.base.application.IComboBoxModel
import org.pillarone.riskanalytics.application.ui.base.model.EnumComboBoxModel
import org.pillarone.riskanalytics.application.ui.chart.view.CriteriaView
import org.pillarone.riskanalytics.core.dataaccess.CompareOperator
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.AggregatedWithSingleAvailableCollectingModeStrategy
import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.core.output.QuantilePerspective

class CriteriaViewModel {
    QueryPaneModel queryModel
    IComboBoxModel keyFigureTypeModel
    IComboBoxModel comparatorModel = new DefaultComboBoxModel()
    double value = 99
    IComboBoxModel valueInterpretationModel = new DefaultComboBoxModel()
    DefaultComboBoxModel periodModel
    boolean enablePeriodComboBox

    private List<ICriteriaModelChangeListener> listeners = []

    public CriteriaViewModel(QueryPaneModel queryModel, boolean enablePeriodComboBox = true) {
        this.@queryModel = queryModel
        this.@enablePeriodComboBox = enablePeriodComboBox
        keyFigureTypeModel = new DefaultComboBoxModel(queryModel.shortPaths)
        comparatorModel = new EnumComboBoxModel(CriteriaComparator.values() as Object[], CriteriaComparator.LESS_EQUALS, false)
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

    public boolean isSingleValueCollector() {
        CollectorMapping collectorMapping = CollectorMapping.findByCollectorName(AggregatedWithSingleAvailableCollectingModeStrategy.IDENTIFIER)
        return (getCollector().equals(collectorMapping?.collectorName))
    }

    public void setSelectedPath(String path) {
        keyFigureTypeModel.setSelectedItem(path)
        notifyCriteriaChanged()
    }

    public CriteriaComparator getSelectedComparator() {
        return comparatorModel.getSelectedEnum()
    }

    public void setSelectedComparator(CriteriaComparator comparator) {
        comparatorModel.selectedEnum = comparator
        notifyCriteriaChanged()
    }

    public Double getInterpretedValue() throws Exception {
        switch (valueInterpretationModel.selectedEnum) {
            case ValueInterpretationType.ABSOLUTE:
                return this.@value
            case ValueInterpretationType.PERCENTILE:
                return ResultAccessor.getPercentile(queryModel.simulationRun, selectedPeriod, selectedPath, collector, field, this.@value, QuantilePerspective.LOSS)
            case ValueInterpretationType.ORDER_STATISTIC:
                return ResultAccessor.getNthOrderStatistic(queryModel.simulationRun, selectedPeriod, selectedPath, collector,
                        field, this.@value, CriteriaComparator.getCompareOperator((String) comparatorModel.selectedItem))
        }
    }

    public boolean validate() {
        if (valueInterpretationModel.getSelectedEnum() != ValueInterpretationType.ABSOLUTE) {
            return isValid(CriteriaComparator.getCompareOperator((String) comparatorModel.selectedItem), value)
        }
        return true
    }

    public static boolean isValid(CompareOperator criteriaComparator, double value) {
        switch (criteriaComparator) {
            case CompareOperator.LESS_THAN: return (value > 0 && value <= 100)
            case CompareOperator.LESS_EQUALS: return (value > 0 && value <= 100)
            case CompareOperator.EQUALS: return (value > 0 && value <= 100)
            case CompareOperator.GREATER_THAN: return (value >= 0 && value < 100)
            case CompareOperator.GREATER_EQUALS: return (value >= 0 && value < 100)
            default: return false
        }
    }

    public String getErrorMessage() {
        if (valueInterpretationModel.getSelectedEnum() != ValueInterpretationType.ABSOLUTE) {
            if (!isValid(CriteriaComparator.getCompareOperator((String) comparatorModel.selectedItem), value)) {
                return CriteriaView.getErrorMessage(valueInterpretationModel.getSelectedEnum())
            }
        }
        return null
    }


    public void setValue(String s) {
        value = Double.parseDouble(s)
        notifyCriteriaChanged()
    }

    public void setValue(double d) {
        value = d
        notifyCriteriaChanged()
    }

    public def getSelectedPeriod() {
        Object selectedString = periodModel.getSelectedItem()
        if (selectedString == "in all periods") { return null }
        return queryModel.getPeriodNumber(selectedString)
    }

    public void setSelectedPeriod(int period) {
        periodModel.selectedItem = queryModel.getPeriodLabel(period)
        notifyCriteriaChanged()
    }

    private void createPeriodModel() {
        periodModel = new DefaultComboBoxModel()
        queryModel.simulationRun.periodCount.times {
            periodModel.addElement queryModel.getPeriodLabel(it)
        }
        periodModel.addElement "in all periods"
    }

    void addListener(ICriteriaModelChangeListener listener) {
        listeners << listener
    }

    void removeListener(ICriteriaModelChangeListener listener) {
        listeners.remove(listener)
    }

    void notifyCriteriaChanged() {
        listeners*.criteriaChanged()
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

    public static CompareOperator getCompareOperator(String displayName) {
        if (displayName.equals('<')) {
            return CompareOperator.LESS_THAN
        }
        else if (displayName.equals('<=')) {
            return CompareOperator.LESS_EQUALS
        }
        else if (displayName.equals('=')) {
            return CompareOperator.EQUALS
        }
        else if (displayName.equals('>')) {
            return CompareOperator.GREATER_THAN
        }
        else if (displayName.equals('>=')) {
            return CompareOperator.GREATER_EQUALS
        }
        return null
    }
}

public enum ValueInterpretationType {
    ABSOLUTE, ORDER_STATISTIC, PERCENTILE
}
