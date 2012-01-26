package org.pillarone.riskanalytics.application.ui.resultnavigator.categories

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement

/**
 * @author martin.melchior
 */
public interface ICategoryResolver {
    boolean isResolvable(OutputElement element)
    String getResolvedValue(OutputElement element)
    boolean createTemplatePath(OutputElement element, String category)
    String getName()
    void addChildResolver(ICategoryResolver resolver)
}

