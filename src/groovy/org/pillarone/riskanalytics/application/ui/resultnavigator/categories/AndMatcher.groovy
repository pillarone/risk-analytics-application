package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class AndMatcher implements ICategoryResolver {
    static final String NAME = "And"
    List<ICategoryResolver> children

    AndMatcher(List<ICategoryResolver> children) {
        this.children = children
    }

    String getName() {
        return NAME
    }

    boolean isResolvable(OutputElement element) {
        boolean test = true
        for (ICategoryResolver child : children) {
            test = test && child.isResolvable(element)
        }
        return test
    }

    String getResolvedValue(OutputElement element) {
        String value = children[0].getResolvedValue(element)
        for (ICategoryResolver child : children) {
            if(!child.getResolvedValue(element).equals(value)) {
                return null
            }
        }
        return value
    }

    boolean createTemplatePath(OutputElement element, String category) {
        if (!isResolvable(element)) return
        for (ICategoryResolver resolver in children) {
            if (!resolver.createTemplatePath(element, category)) return false
        }
        return true
    }
}
