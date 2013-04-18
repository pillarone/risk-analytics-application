package org.pillarone.riskanalytics.application.ui.base.model

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent

/**
 * This class is used only just to identify the nodes for DynamicComposedComponents
 */
@CompileStatic
class DynamicComposedComponentTableTreeNode extends ComponentTableTreeNode {

    DynamicComposedComponentTableTreeNode(DynamicComposedComponent component, String propertyName) {
        super(component, propertyName);
    }

}