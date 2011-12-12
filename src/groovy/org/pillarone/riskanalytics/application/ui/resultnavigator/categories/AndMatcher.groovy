package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
class AndMatcher implements ICategoryMatcher {
    List<ICategoryMatcher> children

    AndMatcher(List<ICategoryMatcher> children) {
        this.children = children
    }

    boolean isMatch(String path) {
        boolean test = true
        for (ICategoryMatcher child : children) {
            test = test && child.isMatch(path)
        }
        return test
    }

    String getMatch(String path) {
        String value = children[0].getMatch(path)
        for (ICategoryMatcher child : children) {
            if(!child.getMatch(path).equals(value)) {
                return null
            }
        }
        return value
    }

    Matcher matcherType() {
        return Matcher.AND
    }
}
