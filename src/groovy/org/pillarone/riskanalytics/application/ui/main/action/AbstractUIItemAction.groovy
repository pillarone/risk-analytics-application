package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractUIItemAction extends SelectionTreeAction {
    AbstractUIItem abstractUIItem

    public AbstractUIItemAction(String name, ULCTableTree tree, AbstractUIItem abstractUIItem) {
        super(name, tree, abstractUIItem.mainModel)
        this.abstractUIItem = abstractUIItem
    }
}
