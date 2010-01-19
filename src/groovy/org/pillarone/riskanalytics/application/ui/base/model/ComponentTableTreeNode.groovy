package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.application.ui.base.view.ComponentUtils
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.components.Component

class ComponentTableTreeNode extends SimpleTableTreeNode {

    Component component

    public ComponentTableTreeNode(Component component, String propertyName) {
        super(propertyName)
        this.component = component
    }

    public String getDisplayName() {
        if (cachedDisplayName != null)
            return cachedDisplayName

        String value = null
        if (!ComponentUtils.isDynamicComposedSubComponentNode(this))
            value = I18NUtils.findComponentDisplayNameByTreeNode(this)

        if (value == null && !ComponentUtils.isDynamicComposedSubComponentNode(this)) {
            value = I18NUtils.findComponentDisplayNameInModelBundle(path)
        }
        if (value == null && !ComponentUtils.isDynamicComposedSubComponentNode(this)) {
            value = I18NUtils.findComponentDisplayNameInComponentBundle(component)
        }

        if (value == null) {
            value = super.getDisplayName()
        } else {
            cachedDisplayName = value
        }
        return value
    }


    public void setName(String newName) {
        this.@name = newName
    }
}