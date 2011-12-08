package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author martin.melchior
 */
class RegexMatcher implements ICategoryMatcher {
    Pattern pattern
    int groupDefiningMemberName

    RegexMatcher(String regex, int groupDefiningMemberName) {
        pattern = ~str
        this.groupDefiningMemberName = groupDefiningMemberName
    }

    boolean isMatch(String path) {
        Matcher matcher = path =~ pattern
        return matcher.size()>0
    }

    String getMatch(String path) {
        Matcher matcher = path =~ pattern
        return matcher.size()>0 ? matcher[0][groupDefiningMemberName] : null
    }
}
