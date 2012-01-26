package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
class ConditionalAssignmentResolver implements ICategoryResolver {
    static final String NAME = "conditionedOn"
    static final String EXCEPTION_MSG = """The conditionedOn resolver should be initialized with a category value assigned and
                                                a condition in form of another resolver."""
    ICategoryResolver condition
    String value

    ConditionalAssignmentResolver(String value) {
        this.value = value
    }

    ConditionalAssignmentResolver(String value, ICategoryResolver condition) {
        this.value = value
        this.condition = condition
    }

    String getName() {
        return NAME
    }

    void addChildResolver(ICategoryResolver resolver) {
        condition = resolver
    }

    boolean isResolvable(OutputElement element) {
        return condition.isResolvable(element)
    }

    String getResolvedValue(OutputElement element) {
        return condition.isResolvable(element) ? value : null
    }

    boolean createTemplatePath(OutputElement element, String category) {
        return false
    }
}
