package org.pillarone.riskanalytics.application.ui.base.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.WorkflowParameterizationNode
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resulttemplate.model.ResultConfigurationNode
import org.joda.time.DateTime

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ModellingItemNodeFilter implements ITableTreeFilter {

    public static final int ALL = -1
    public static final int WITHOUT_COMMENTS = 0
    public static final int WITH_COMMENTS = 1
    public String displayValue

    List values
    int column
    boolean allSelected = false

    public ModellingItemNodeFilter(List values, int column) {
        this.values = values;
        this.column = column
    }

    public ModellingItemNodeFilter(List values, int column, boolean all) {
        this(values, column)
        this.@allSelected = all
    }

    public boolean acceptNode(ITableTreeNode node) {
        return node ? internalAcceptNode(node) : false;
    }

    boolean internalAcceptNode(ParameterizationNode node) {
        if (allSelected || !values || values.size() == 0) return true
        return contains(node.values[column])
    }

    boolean internalAcceptNode(WorkflowParameterizationNode node) {
        if (allSelected || !values || values.size() == 0) return true
        return contains(node.values[column])
    }

    boolean internalAcceptNode(ResultConfigurationNode node) {
        if (allSelected || !values || values.size() == 0) return true
        return contains(node.values[column])
    }

    boolean internalAcceptNode(SimulationNode node) {
        if (allSelected || !values || values.size() == 0) return true
        return contains(node.values[0])
    }

    boolean internalAcceptNode(ITableTreeNode node) {
        return true
    }

    private boolean contains(String value) {
        boolean found = false
        if (column == ModellingInformationTableTreeModel.NAME) {
            if (values.size() == 0) return false
            for (String name: values) {
                if (value != null && value == name) {
                    found = true
                    break
                }
            }
        } else if (column == ModellingInformationTableTreeModel.TAGS) {
            for (String tag: values) {
                found = contains(tag, value)
                if (found) break;
            }
        } else {
            found = values?.contains(value);
        }
        return found
    }

    private boolean contains(Long value) {
        contains(value.intValue())
    }

    private boolean contains(int value) {
        switch (values[0]) {
            case ALL: displayValue = "All"; return true
            case WITHOUT_COMMENTS: displayValue = "Without comments"; return value == 0
            case WITH_COMMENTS: displayValue = "With comments"; return value >= 1
        }
    }

    private boolean contains(DateTime value) {
        return values.find { it == ModellingInformationTableTreeModel.simpleDateFormat.print(value)} != null
    }

    private boolean contains(String tag, String value) {
        if (!value) return false
        return value.split(",").any { it == tag}
    }

}