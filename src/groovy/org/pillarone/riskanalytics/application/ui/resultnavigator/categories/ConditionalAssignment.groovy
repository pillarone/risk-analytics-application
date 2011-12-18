package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
class ConditionalAssignment implements ICategoryMatcher {
    static final String NAME = "ByCondition"
    ICategoryMatcher condition
    String value

    ConditionalAssignment(String value, ICategoryMatcher condition) {
        this.value = value
        this.condition = condition
    }

    String getName() {
        return NAME
    }

    boolean isMatch(String path) {
        return condition.isMatch(path)
    }

    String getMatch(String path) {
        return condition.isMatch(path) ? value : null
    }
}
