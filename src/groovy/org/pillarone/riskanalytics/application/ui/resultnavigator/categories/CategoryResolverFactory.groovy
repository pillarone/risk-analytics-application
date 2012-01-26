package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import com.ulcjava.base.application.ULCBoxPane

/**
 * @author martin.melchior
 */
class CategoryResolverFactory {

    static ULCBoxPane getMatcherView(ICategoryResolver matcher) {
        switch (matcher) {
            case WordMatchResolver:
                return new WordMatchResolverView((WordMatchResolver)matcher)
            case EnclosingMatchResolver:
                return new EnclosingMatchResolverView((EnclosingMatchResolver)matcher)
            default:
                return null
        }
    }

    static ICategoryResolver getCategoryMatcher(String matcherType, Map params, List children) {
        switch(matcherType) {
            case AndResolver.NAME:
                return children ? new AndResolver(children) : AndResolver()
            case OrResolver.NAME:
                return children ? new OrResolver(children) : new OrResolver()
            case WordMatchResolver.NAME:
                try {
                    return new WordMatchResolver((List<String>) params["toMatch"])
                } catch (Exception ex) {
                    throw new IllegalArgumentException(WordMatchResolver.EXCEPTION_MSG)
                }
            case RegexMatchResolver.NAME:
                try {
                    return new RegexMatchResolver((String) params["regex"], (Integer) params["groupDefiningMemberName"])
                } catch (Exception ex) {
                    throw new IllegalArgumentException(RegexMatchResolver.EXCEPTION_MSG)
                }
            case EnclosingMatchResolver.NAME:
                try {
                    return new EnclosingMatchResolver((List<String>)params["prefix"], (List<String>) params["suffix"])
                } catch (Exception ex) {
                    throw new IllegalArgumentException(EnclosingMatchResolver.EXCEPTION_MSG)
                }
            case EndingMatchResolver.NAME:
                try {
                    return new EndingMatchResolver((String) params["prefix"])
                } catch (Exception ex) {
                    throw new IllegalArgumentException(EndingMatchResolver.EXCEPTION_MSG)
                }
            case ConditionalAssignmentResolver.NAME:
                try {
                    return children ? new ConditionalAssignmentResolver((String) params["value"], (ICategoryResolver) children[0]) \
                                    : new ConditionalAssignmentResolver((String) params["value"])
                } catch (Exception ex) {
                    throw new IllegalArgumentException(ConditionalAssignmentResolver.EXCEPTION_MSG)
                }
            case SynonymToCategory.NAME:
                try {
                    return new SynonymToCategory((String) params["category"])
                } catch (Exception ex) {
                    throw new IllegalArgumentException(SynonymToCategory.EXCEPTION_MSG)
                }
            default:
                return null
        }
    }
}
