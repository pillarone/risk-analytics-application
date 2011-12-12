package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

/**
 * @author martin.melchior
 */
class CategoryMatcherFactory {

    static ICategoryMatcher getCategoryMatcher(Matcher matcher, Object... p) {
        switch(matcher) {
            case Matcher.BY_SINGLE_LIST_VALUE:
                if (p[0] instanceof List<String>) {
                    return new SingleValueFromListMatcher((List<String>)p[0]);
                } else {
                    return null
                }
            case Matcher.BY_REGEX:
                if (p[0] instanceof String && p[1] instanceof Integer) {
                    return new RegexMatcher((String)p[0], (Integer) p[1])
                } else {
                    return null
                }
            case Matcher.BY_ENCLOSING:
                if (p[0] instanceof String && p[1] instanceof String) {
                    return new EnclosingMatcher((String)p[0], (String) p[1])
                } else {
                    return null
                }
            case Matcher.BY_ENDING:
                if (p[0] instanceof String) {
                    return new EndingMatcher((String)p[0])
                } else {
                    return null
                }
            case Matcher.BY_CONDITION:
                if (p[0] instanceof String && p[1] instanceof ICategoryMatcher) {
                    return new ConditionalAssignment((String) p[0], (ICategoryMatcher) p[1])
                } else {
                    return null
                }
            default:
                return null
        }
    }
}
