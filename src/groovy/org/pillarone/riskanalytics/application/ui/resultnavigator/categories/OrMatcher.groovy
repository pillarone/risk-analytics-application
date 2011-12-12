package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
class OrMatcher implements ICategoryMatcher {
    List<ICategoryMatcher> children

    OrMatcher(List<ICategoryMatcher> children) {
        this.children = children
    }

    boolean isMatch(String path) {
        for (ICategoryMatcher child : children) {
            if(child.isMatch(path)) {
                return true
            }
        }
        return false
    }

    String getMatch(String path) {
        for (ICategoryMatcher child : children) {
            if(child.isMatch(path)) {
                return child.getMatch(path)
            }
        }
        return null
    }

    Matcher matcherType() {
        Matcher.OR
    }
}
