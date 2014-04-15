package org.pillarone.riskanalytics.application.ui.base.model

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.main.view.item.ModelUIItem

@CompileStatic
class ModelNode extends ItemNode {

    public ModelNode(ModelUIItem modelUIItem) {
        super(modelUIItem, false, false)
    }

    @Override
    ModelUIItem getItemNodeUIItem() {
        super.itemNodeUIItem as ModelUIItem
    }
}