package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.SimpleValueParameterizationTableTreeNode

//Subclass needed so that the renderer knows that this is a int parameter node
class IntegerTableTreeNode extends SimpleValueParameterizationTableTreeNode {

    public IntegerTableTreeNode(List parameters) {
        super(parameters)
    }

}
