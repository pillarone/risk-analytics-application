package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem

//Subclass needed so that the renderer knows that this is a int parameter node
class IntegerTableTreeNode extends SimpleValueParameterizationTableTreeNode {

    public IntegerTableTreeNode(String path, ParametrizedItem item) {
        super(path, item)
    }

}
