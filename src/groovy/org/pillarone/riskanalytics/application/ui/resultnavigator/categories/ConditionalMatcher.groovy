package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
class ConditionalMatcher implements ICategoryMatcher {
    ICategoryMatcher condition
    ICategoryMatcher matcher

    ConditionalMatcher(ICategoryMatcher matcher, ICategoryMatcher condition) {
        this.matcher = matcher
        this.condition = condition
    }

    boolean isMatch(String path) {
        return condition.isMatch(path) && matcher.isMatch(path)
    }

    String getMatch(String path) {
        return condition.isMatch(path) ? matcher.getMatch(path) : null
    }
}
