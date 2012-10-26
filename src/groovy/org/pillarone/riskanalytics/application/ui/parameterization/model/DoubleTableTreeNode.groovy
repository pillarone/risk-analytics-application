package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem

//Subclass needed so that the renderer knows that this is a double parameter node
class DoubleTableTreeNode extends SimpleValueParameterizationTableTreeNode {

    public DoubleTableTreeNode(String path, ParametrizedItem item) {
        super(path, item)
    }

}
