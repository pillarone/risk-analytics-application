package org.pillarone.riskanalytics.application.ui.resultnavigator.categories.resolver

import com.ulcjava.base.application.ULCBoxPane
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.ICategoryResolver

/**
 * @author martin.melchior
 */
class CategoryResolverFactory {

    /**
     * Provides for given ICategoryResolver a suitable view in form of a ULCBoxPane.
     * @param matcher
     * @return
     */
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

    /**
     * Creates suitably configured instances of ICategoryResolver by referring to
     * <ul>
     *     <it> name of the category resolver (see ICategoryResolver.getName()) </it>
     *     <it> named parameters passed by the map</it>
     * </ul>
     * This method is used by the MapCategoriesBuilder to create ICategoryResolver instances at the nodes.
     * @param matcherType
     * @param params
     * @param children
     * @return
     */
    public static ICategoryResolver getCategoryMatcher(String matcherType, Map params) {
        switch(matcherType) {
            case AndResolver.NAME:
                return new AndResolver()
            case OrResolver.NAME:
                return new OrResolver()
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
                    return new ConditionalAssignmentResolver((String) params["value"])
                } catch (Exception ex) {
                    throw new IllegalArgumentException(ConditionalAssignmentResolver.EXCEPTION_MSG)
                }
            case SynonymToCategoryResolver.NAME:
                try {
                    return new SynonymToCategoryResolver((String) params["category"])
                } catch (Exception ex) {
                    throw new IllegalArgumentException(SynonymToCategoryResolver.EXCEPTION_MSG)
                }
            default:
                return null
        }
    }
}
