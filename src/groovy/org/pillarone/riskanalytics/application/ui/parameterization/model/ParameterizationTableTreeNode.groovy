package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.HTMLUtilities
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

abstract class ParameterizationTableTreeNode extends SimpleTableTreeNode {

    protected final Log LOG = LogFactory.getLog(getClass())

    Set<ParameterValidation> errors

    ParametrizedItem parametrizedItem
    String parameterPath

    public ParameterizationTableTreeNode(String path, ParametrizedItem item) {
        super(getNodeName(path))
        this.parameterPath = path
        this.parametrizedItem = item
    }


    boolean isCellEditable(int i) {
        return i == 0 ? false : getExpandedCellValue(i) != null
    }

    abstract public void setValueAt(Object o, int i)

    Object getExpandedCellValue(int column) {
        if (parametrizedItem.hasParameterAtPath(parameterPath, column - 1)) {
            return doGetExpandedCellValue(column)
        }
        return null
    }

    abstract Object doGetExpandedCellValue(int column)

    protected static String getNodeName(String path) {
        path.substring(path.lastIndexOf(':') + 1)
    }

    public String getDisplayName() {
        String value = null
        value = this.lookUp(value, "")
        if (value == null) {
            value = super.getDisplayName()
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

    public String getErrorMessage() {
        if (!errors) return null
        StringBuilder sb = new StringBuilder("")
        for (ParameterValidation error : errors) {
            sb.append(error.getLocalizedMessage(LocaleResources.getLocale()) + "<br> ")
        }
        return HTMLUtilities.convertToHtml(sb.toString())
    }

    public Color getErrorColor() {
        if (!errors) return Color.black
        if (errors.any { it.validationType == ValidationType.ERROR}) return UIUtils.getColor(ValidationType.ERROR)
        if (errors.any { it.validationType == ValidationType.WARNING}) return UIUtils.getColor(ValidationType.WARNING)
        if (errors.any { it.validationType == ValidationType.HINT}) return UIUtils.getColor(ValidationType.HINT)
        return Color.black
    }

    public void addError(ParameterValidation error) {
        if (!errors) errors = new HashSet<ParameterValidation>()
        if (!errors.any {it.msg == error.msg})
            errors << error
    }

    private String lookUp(String value, String tooltip) {
        String displayName
        if (parent != null) {
            displayName = findParameterDisplayNameInParentNodes(parent, tooltip)
        }
        return displayName
    }

    private String findParameterDisplayNameInParentNodes(ITableTreeNode node, String toolTip = "") {
        String value = null
        if (node instanceof ComponentTableTreeNode) {
            value = I18NUtils.findParameterDisplayName(node, path.substring(node.path.length() + 1), toolTip)
        } else {
            if (node instanceof ParameterObjectParameterTableTreeNode) {
                value = I18NUtils.findParameterDisplayName(node, path.substring(node.path.length() + 1), toolTip)
            }
            if (value == null && node.parent != null) {
                value = findParameterDisplayNameInParentNodes(node.getParent(), toolTip)
            }
        }
        return value
    }

}


