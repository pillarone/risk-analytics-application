package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class OrMatcher implements ICategoryResolver {
    static final String NAME = "Or"
    List<ICategoryResolver> children

    OrMatcher(List<ICategoryResolver> children) {
        this.children = children
    }

    String getName() {
        return NAME
    }

    boolean isResolvable(OutputElement element) {
        for (ICategoryResolver child : children) {
            if(child.isResolvable(element)) {
                return true
            }
        }
        return false
    }

    String getResolvedValue(OutputElement element) {
        for (ICategoryResolver child : children) {
            if(child.isResolvable(element)) {
                return child.getResolvedValue(element)
            }
        }
        return null
    }

    boolean createTemplatePath(OutputElement element, String category) {
        if (!isResolvable(element)) return
        for (ICategoryResolver resolver in children) {
            if (resolver.createTemplatePath(element, category)) return true
        }
        return false
    }
}
