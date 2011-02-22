package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.core.model.Model

class ModelNode extends ItemNode {

    public ModelNode(Model model) {
        super(model, false, false)
    }
}