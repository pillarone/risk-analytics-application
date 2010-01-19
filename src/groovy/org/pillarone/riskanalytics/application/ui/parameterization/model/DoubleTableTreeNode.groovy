package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.SimpleValueParameterizationTableTreeNode

//Subclass needed so that the renderer knows that this is a double parameter node
class DoubleTableTreeNode extends SimpleValueParameterizationTableTreeNode {

    public DoubleTableTreeNode(List parameters) {
        super(parameters)
    }

}
