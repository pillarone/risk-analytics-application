package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import java.util.regex.Matcher
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
            Matcher matcher = path =~ pattern
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
            Matcher matcher = path =~ patterns[i]
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
}
