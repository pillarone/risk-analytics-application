package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
class ConditionalAssignment implements ICategoryMatcher {
    ICategoryMatcher condition
    String value

    ConditionalAssignment(String value, ICategoryMatcher condition) {
        this.value = value
        this.condition = condition
    }

    boolean isMatch(String path) {
        return condition.isMatch(path)
    }

    String getMatch(String path) {
        return condition.isMatch(path) ? value : null
    }
}
