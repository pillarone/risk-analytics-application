package org.pillarone.riskanalytics.application.ui.resultnavigator.model

/**
 * @author martin.melchior
 */
class OutputElement {
    static final String PATH = "Path"
    static final String FIELD = "Field"
    static final String COLLECTOR = "Collector"
    String path
    String field
    String collector
    Map<String,Object> categoryMap = [:]

    void addCategoryValue(String category, Object value) {
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
