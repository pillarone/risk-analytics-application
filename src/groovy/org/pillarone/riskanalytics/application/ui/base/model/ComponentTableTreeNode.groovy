package org.pillarone.riskanalytics.application.ui.base.model

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.util.ComponentUtils
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.components.Component

@CompileStatic
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
        value = lookUp(value, "")

        if (value == null) {
            value = super.getDisplayName()
        } else {
            cachedDisplayName = value
        }
        return value
    }

    public String getToolTip() {
        if (!cachedToolTip) {
            String value = name
            cachedToolTip = lookUp(value, TOOLTIP)
            if (!cachedToolTip)
                cachedToolTip = super.getToolTip()
        }
        return cachedToolTip
    }

    private String lookUp(String value, String tooltip) {
        String displayName
        if (!ComponentUtils.isDynamicComposedSubComponentNode(this)) {
            displayName = I18NUtils.findComponentDisplayNameByTreeNode(this, tooltip)
        }

        if (displayName == null && !ComponentUtils.isDynamicComposedSubComponentNode(this)) {
            displayName = I18NUtils.findComponentDisplayNameInModelBundle(path, tooltip)
        }
        if (displayName == null && !ComponentUtils.isDynamicComposedSubComponentNode(this)) {
            displayName = I18NUtils.findComponentDisplayNameInComponentBundle(component, tooltip)
        }
        return displayName
    }

    public void setName(String newName) {
        this.@name = newName
    }
}