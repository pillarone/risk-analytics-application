package org.pillarone.riskanalytics.application.ui.customtable.model

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.StatisticsKeyfigure
import com.ulcjava.base.application.ULCAlert
import org.pillarone.riskanalytics.core.output.QuantilePerspective

/**
 *
 * @author ivo.nussbaumer
 */
class DataCellElement extends OutputElement {
    Object value
    int period
    StatisticsKeyfigure statistics
    Number parameter

    public DataCellElement () {
    }

    public DataCellElement (OutputElement outputElement, int periodIndex = 0, StatisticsKeyfigure statistics = StatisticsKeyfigure.MEAN, Number statisticsParameter = 0) {
        this.run = outputElement.run
        this.path = outputElement.path
        this.templatePath = outputElement.templatePath
        this.field = outputElement.field
        this.collector = outputElement.collector
        this.categoryMap = (Map)outputElement.categoryMap.clone()
        this.wildCards = outputElement.wildCards
        this.wildCardPath = outputElement.wildCardPath

        if (statisticsParameter == null)
            statisticsParameter = 0

        this.categoryMap[OutputElement.PERIOD] = periodIndex.toString()
        this.categoryMap[OutputElement.STATISTICS] = statistics.name
        this.categoryMap[OutputElement.STATISTICS_PARAMETER] = statisticsParameter.toString()

        this.categoryMap.remove(OutputElement.FIELD)
        this.categoryMap.remove(OutputElement.PATH)
        this.categoryMap.remove(OutputElement.COLLECTOR)

        this.period = periodIndex
        this.statistics = statistics
        this.parameter = statisticsParameter

        updateValue()
    }

    // Copy constructor
    public DataCellElement (DataCellElement dataCellElement) {
        this.run = dataCellElement.run
        this.path = dataCellElement.path
        this.templatePath = dataCellElement.templatePath
        this.field = dataCellElement.field
        this.collector = dataCellElement.collector
        this.categoryMap = (Map)dataCellElement.categoryMap.clone()
        this.wildCards = dataCellElement.wildCards
        this.wildCardPath = dataCellElement.wildCardPath

        this.value = dataCellElement.value
        this.period = dataCellElement.period
        this.statistics = dataCellElement.statistics
        this.parameter = dataCellElement.parameter
    }

    /**
     * just update the value of the DataCellElement
     */
    public void updateValue() {
        try {
            switch (statistics) {
                case StatisticsKeyfigure.VAR:
                    value = ResultAccessor.getVar(run, period, path, collector, field, parameter, QuantilePerspective.LOSS)
                    break;
                case StatisticsKeyfigure.TVAR:
                    value = ResultAccessor.getTvar(run, period, path, collector, field, parameter, QuantilePerspective.LOSS)
                    break;
                case StatisticsKeyfigure.PERCENTILE:
                    value = ResultAccessor.getPercentile(run, period, path, collector, field, parameter, QuantilePerspective.LOSS)
                    break;
                case StatisticsKeyfigure.STDEV:
                    value = ResultAccessor.getStdDev(run, period, path, collector, field)
                    break;
                case StatisticsKeyfigure.MIN:
                    value = ResultAccessor.getMin(run, period, path, collector, field)
                    break;
                case StatisticsKeyfigure.MEAN:
                    value = ResultAccessor.getMean(run, period, path, collector, field)
                    break;
                case StatisticsKeyfigure.MAX:
                    value = ResultAccessor.getMax(run, period, path, collector, field)
                    break;
                case StatisticsKeyfigure.ITERATION:
                    value = ResultAccessor.getSingleIterationValue(run, period, path, field, collector, parameter.intValue())
                    break;
            }
        } catch (Exception e) {
            value = "#ERROR"
        }
    }

    /**
     * Update the DataCellElement, by resolving the variables in the categoryMap
     * Also updates the values, if the DataCellElement has changed
     *
     * @param customTableModel the CustomTableModel (used for resolving the variables)
     * @return true if DataCellElement has changed
     */
    public boolean update(CustomTableModel customTableModel) {
        boolean changed = false
        Map<String, String> categoryMapCopy = new HashMap<String, String>()

        // resolve the variables
        for (String category : this.categoryMap.keySet()) {
            String value = this.categoryMap[category]
            if (value.startsWith("=")) {
                value = customTableModel.getValueAt(value.substring(1))
            }
            categoryMapCopy.put (category, value)
        }

        String new_path = this.getWildCardPath().getSpecificPath(categoryMapCopy)
        if (this.path != new_path) {
            this.path = new_path
            changed = true
        }

        //TODO: what's this? does not work by default, appears to be dependent on
        // keyfigure { synonymousTo(category: "Field") } in mapping
        String new_field = categoryMapCopy["keyfigure"]
        if (new_field != null) {
            if (this.field != new_field) {
                this.field = new_field
                changed = true
            }
        }

        int new_period = (int)Double.parseDouble(categoryMapCopy[OutputElement.PERIOD])
        if (this.period != new_period) {
            this.period = new_period
            changed = true
        }

        StatisticsKeyfigure new_statistics = StatisticsKeyfigure.getEnumValue(categoryMapCopy[OutputElement.STATISTICS])
        if (this.statistics != new_statistics) {
            this.statistics = new_statistics
            changed = true
        }

        try {
            Number new_parameter = Double.parseDouble(categoryMapCopy[OutputElement.STATISTICS_PARAMETER])
            if (this.parameter != new_parameter) {
                this.parameter = new_parameter
                changed = true
            }
        } catch (NumberFormatException e) {
            ULCAlert alert = new ULCAlert("Invalid input", "Value '${categoryMapCopy[OutputElement.STATISTICS_PARAMETER]}' is not a valid ${categoryMapCopy[OutputElement.STATISTICS]} number.", "Ok")
            alert.show()
        }

        // update the values, if the DataCellElement has changed
        if (changed)
            updateValue()

        return changed
    }
}
