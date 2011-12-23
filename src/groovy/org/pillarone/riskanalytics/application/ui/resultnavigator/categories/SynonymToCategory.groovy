package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class SynonymToCategory implements ICategoryResolver {
    static final String NAME = "Synonym to category"
    String category

    SynonymToCategory(String category) {
        this.category = category
    }

    String getName() {
        return NAME
    }

    boolean isResolvable(OutputElement element) {
        return element.getCategoryValue(category) != null
    }

    String getResolvedValue(OutputElement element) {
        return element.getCategoryValue(category)
    }

    boolean createTemplatePath(OutputElement element, String category) {
        return false
    }
}
