package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractCompareAction extends SelectionTreeAction {

    public AbstractCompareAction(String actionName, ULCTableTree tree, RiskAnalyticsMainModel model) {
        super(actionName, tree, model)
    }

    public void validate() throws IllegalArgumentException, Exception {
        List elements = getAllSelectedObjects()
        if (!elements || elements.size() < 2) throw new IllegalArgumentException("select at lease two items for compare")
        Class modelClass = getSelectedModel().class
        Class itemClass = elements[0].item.class
        if (!elements.every { it.item.modelClass == modelClass && itemClass == it.item.class }) {
            throw new IllegalArgumentException("not comparable selected items ")
        }
    }

    public boolean isEnabled() {
        try {
            validate()
        } catch (IllegalArgumentException ex) {
            return false
        }
        return true
    }
}
