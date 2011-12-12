package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import java.util.regex.Pattern

/**
 * @author martin.melchior
 */
class EnclosingMatcher implements ICategoryMatcher {
    Pattern pattern

    EnclosingMatcher(String prefix, String suffix) {
        String str = "($prefix)(\\w*)($suffix)"
        pattern = ~str
    }

    EnclosingMatcher(List<String> prefixes, List<String> suffixes) {
        String prefix = prefixes[0]
        String str = "($prefix"
        for (int i = 1; i < prefixes.size(); i++) {
            prefix = prefixes[i]
            str += "|$prefix"
        }
        String suffix = suffixes[0]
        str += ")(\\w*)($suffix"
        for (int i = 1; i < suffixes.size(); i++) {
            suffix = suffixes[i]
            str += "|$suffix"
        }
        str += ")"
        pattern = ~str
    }

    boolean isMatch(String path) {
        java.util.regex.Matcher matcher = path =~ pattern
        return matcher.size()>0
    }

    String getMatch(String path) {
        java.util.regex.Matcher matcher = path =~ pattern
        return matcher.size()>0 ? matcher[0][2] : null
    }

    Matcher matcherType() {
        Matcher.BY_ENCLOSING
    }
}
