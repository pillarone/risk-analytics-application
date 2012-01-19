package org.pillarone.riskanalytics.application.ui.resultnavigator.model

import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.WildCardPath
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor

/**
 * @author martin.melchior
 */
class OutputElement {
    static final String PATH = "Path"
    static final String FIELD = "Field"
    static final String COLLECTOR = "Collector"
    static final String PERIOD = "period"
    static final String STATISTICS = "statistics"
    static final String STATISTICS_PARAMETER = "parameter"


    SimulationRun run
    String path
    String templatePath
    String field
    String collector
    Map<String,String> categoryMap = [:]  // may also contain elements with null value
    List<String> wildCards
    WildCardPath wildCardPath

    void addCategoryValue(String category, String value) {
        categoryMap[category] = value
    }

    Object getCategoryValue(String category) {
        return categoryMap[category]
    }

    public boolean equals(Object o){
        if (o instanceof OutputElement) {
            OutputElement el = (OutputElement) o
            return el.path.equals(path) && el.field.equals(field) && el.collector.equals(collector) \
                    && (el.categoryMap==null ? categoryMap==null : el.categoryMap.equals(categoryMap))
        }
        return false
    }

    public int hashCode() {
        return path.hashCode()+field.hashCode()+collector.hashCode()
    }

}
