package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.IMutableTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.view.IMultiValueTableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem

abstract class AbstractMultiValueParameterizationTableTreeNode extends ParameterizationTableTreeNode implements IMultiValueTableTreeNode {

    protected Map localizedValues = [:]
    protected Map localizedKeys = [:]


    List values

    public AbstractMultiValueParameterizationTableTreeNode(String path, ParametrizedItem item) {
        super(path, item)
    }

    final void setParent(IMutableTableTreeNode parent) {
        super.setParent(parent);
        if (parent != null) {
            this.values = initValues()
        }
    }

    public String getKeyForValue(String value) {
        if (localizedValues.containsKey(value)) {
            return localizedValues[value]
        } else {
            return value
        }
    }

    public String getValueForKey(String key) {
        if (localizedKeys.containsKey(key)) {
            return localizedKeys[key]
        } else {
            return key
        }
    }

    abstract protected List initValues()
}
