package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleCollectorIterationNode extends DefaultMutableTableTreeNode {
    Object[] values

    public SingleCollectorIterationNode(values) {
        super([String.valueOf(values[0])] as Object[], true)
        this.values = values;
    }
}

