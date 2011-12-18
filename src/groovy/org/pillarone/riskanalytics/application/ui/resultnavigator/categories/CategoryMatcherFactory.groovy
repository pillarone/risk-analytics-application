package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import com.ulcjava.base.application.ULCBoxPane

/**
 * @author martin.melchior
 */
class CategoryMatcherFactory {

    static ULCBoxPane getMatcherView(ICategoryMatcher matcher) {
        switch (matcher) {
            case SingleValueFromListMatcher:
                return new SingleValueFromListMatcherView((SingleValueFromListMatcher)matcher)
            case EnclosingMatcher:
                return new EnclosingMatcherView((EnclosingMatcher)matcher)
            default:
                return null
        }
    }

    static ICategoryMatcher getCategoryMatcher(String matcherType, Object... p) {
        switch(matcherType) {
            case SingleValueFromListMatcher.NAME:
                if (p[0] instanceof List<String>) {
                    return new SingleValueFromListMatcher((List<String>)p[0]);
                } else {
                    return null
                }
            case RegexMatcher.NAME:
                if (p[0] instanceof String && p[1] instanceof Integer) {
                    return new RegexMatcher((String)p[0], (Integer) p[1])
                } else {
                    return null
                }
            case EnclosingMatcher.NAME:
                if (p[0] instanceof String && p[1] instanceof String) {
                    return new EnclosingMatcher((String)p[0], (String) p[1])
                } else {
                    return null
                }
            case EndingMatcher.NAME:
                if (p[0] instanceof String) {
                    return new EndingMatcher((String)p[0])
                } else {
                    return null
                }
            case ConditionalAssignment.NAME:
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
