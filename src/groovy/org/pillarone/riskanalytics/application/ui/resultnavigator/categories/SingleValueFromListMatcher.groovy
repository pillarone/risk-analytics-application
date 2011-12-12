package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import java.util.regex.Pattern

/**
 * @author martin.melchior
 */
class SingleValueFromListMatcher implements ICategoryMatcher {
    List<Pattern> patterns
    List<String> toMatch

    SingleValueFromListMatcher(List<String> toMatch) {
        this.toMatch = toMatch
        patterns = []
        for (String s : toMatch) {
            patterns.add(~s)
        }
    }

    boolean isMatch(String path) {
        boolean isFound = false
        for (Pattern pattern : patterns) {
            java.util.regex.Matcher matcher = path =~ pattern
            if (matcher.size()>0) {
                if (isFound) {
                    return false
                }
                isFound = true
            }
        }
        return isFound
    }

    String getMatch(String path) {
        boolean isFound = false
        String value = null
        for (int i = 0; i < patterns.size(); i++) {
            java.util.regex.Matcher matcher = path =~ patterns[i]
            if (matcher.size()>0) {
                if (isFound) {
                    return null
                }
                isFound = true
                value = toMatch[i]
            }
        }
        return value
    }

    Matcher matcherType() {
        Matcher.BY_SINGLE_LIST_VALUE
    }
}
