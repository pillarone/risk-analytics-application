package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ParameterNotFoundException
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

/**
 * User: Fouad Jaada
 */

public class CompareParameterizationTableTreeModel extends AbstractTableTreeModel {
    private List<Parameterization> parameterizations
    ParameterizationTreeBuilder builder
    Model simulationModel
    ITableTreeNode root
    Boolean readOnly = false
    Map nonValidValues = [:]
    int minPeriod = -1
    List differentsNode = []

    CompareParameterizationTableTreeModel(ParameterizationTreeBuilder builder, List<Parameterization> parameterizations) {
        this.parameterizations = parameterizations
        this.builder = builder
        this.root = builder.root
        minPeriod = ParameterizationUtilities.getMinPeriod(parameterizations)
    }


    public int getColumnCount() {
        return parameterizations.size() * minPeriod + 1;
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "Name"
        }

        if (builder != null) {
            return builder.item.getPeriodLabel(getPeriodIndex(column)) + ": " + getParameterizationName(column)
        }
        return null
    }

    protected int getParameterizationIndex(int column) {
        if (column == 0)
            return 0
        return (column - 1) % (getColumnCount() - 1)
    }

    protected String getParameterizationName(int column) {
        Parameterization parameterization = parameterizations.get((column - 1) % parameterizations.size())
        return parameterization.name + " v" + parameterization.versionNumber
    }


    protected int getPeriodIndex(int column) {
        return (column - 1).intdiv(parameterizations.size())
    }

    public int getParameterizationsSize() {
        return parameterizations.size()
    }

    public boolean isDifferent(Object node) {
        if (node instanceof CompareParameterizationTableTreeNode) {
            return internalIsDifferent(node.parameterizationTableTreeNode, node)
        } else if (node instanceof CompareParameterizationClassifierTableTreeNode) {
            return internalIsDifferent(node)
        }
        return false
    }

    protected boolean internalIsDifferent(ParameterizationTableTreeNode node, CompareParameterizationTableTreeNode compareNode) {
        boolean different = false
        for (int i = 1; i < getColumnCount(); i += getParameterizationsSize()) {
            def refObject = getValueAt(compareNode, i)
            for (int j = 1; j < 2 || j < getParameterizationsSize(); j++) {
                def object = getValueAt(compareNode, i + j)
                if (refObject != object) {
                    different = true
                    differentsNode << compareNode
                    break;
                }
            }
        }
        return different
    }

    protected boolean internalIsDifferent(CompareParameterizationClassifierTableTreeNode compareNode) {
        boolean different = false
        for (int i = 1; i < getColumnCount(); i += getParameterizationsSize()) {
            def refObject = getValueAt(compareNode, i)
            for (int j = 1; j < 2 || j < getParameterizationsSize(); j++) {
                def object = getValueAt(compareNode, i + j)
                if (refObject != object) {
                    different = true
                    differentsNode << compareNode
                    break;
                }
            }
        }
        return different
    }

    protected boolean internalIsDifferent(MultiDimensionalParameterizationTableTreeNode node, CompareParameterizationTableTreeNode compareNode) {
        boolean different = false
        for (int i = 1; i < getColumnCount(); i += getParameterizationsSize()) {
            MultiDimensionalParameterHolder parameterHolder = compareNode.getParameterHolder(i)
            if (parameterHolder == null) return true
            AbstractMultiDimensionalParameter multiDimensionalParameter = parameterHolder.getBusinessObject()
            List<List> values = multiDimensionalParameter.values
            for (int j = 1; j < 2 || j < getParameterizationsSize(); j++) {
                MultiDimensionalParameterHolder currentParameterHolder = compareNode.getParameterHolder(i + j)
                if (currentParameterHolder == null) return true
                AbstractMultiDimensionalParameter currentMultiDimensionalParameter = currentParameterHolder.businessObject
                List<List> currentValues = currentMultiDimensionalParameter.values

                if (currentValues.size() != values.size()) {
                    different = true
                    differentsNode << compareNode
                    break
                }
                for (int k = 0; k < values.size(); k++) {
                    List currentList = currentValues[k]
                    List list = values[k]
                    if (currentList.size() != list.size()) {
                        different = true
                        differentsNode << compareNode
                        break
                    }
                    for (int l = 0; l < list.size(); l++) {
                        if (currentList[l] != list[l]) {
                            different = true
                            differentsNode << compareNode
                            break
                        }
                    }
                }
            }
            return different
        }
    }

    public Object getValueAt(Object node, int i) {
        def value
        if (nonValidValues[[node, i]] != null) {
            value = nonValidValues[[node, i]]
        } else {
            value = node.getValueAt(i)
        }
        if (value != null && !(value instanceof Number || value instanceof Date)) {
            value = value.toString()
        }
        return value
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int i) {
        return parent.getChildAt(index)
    }

    public int getChildCount(Object node) {
        return node.childCount
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0
    }

    public int getIndexOfChild(Object parent, Object child) {
        return parent.getIndex(child)
    }


}

class ParameterizationUtilities {

    public static final Color ERROR_BG = new Color(255, 153, 153)

    public static int getMinPeriod(List<Parameterization> parameterizations) {
        int minPeriod = parameterizations.get(0).periodCount
        for (Parameterization p : parameterizations) {
            if (p.periodCount < minPeriod)
                minPeriod = p.periodCount
        }
        return minPeriod
    }

    public static boolean isParameterObjectParameter(String path, List<ParametrizedItem> items) {
        boolean result = false
        for (ParametrizedItem item in items) {
            try {
                ParameterHolder parameterHolder = item.getParameterHoldersForFirstPeriod(path)
                if (parameterHolder instanceof ParameterObjectParameterHolder) {
                    result = true
                }
            } catch (ParameterNotFoundException e) {
                //this parameter does not exist in all the compared parameterizations
            }
        }
        return result
    }

    public static List getParameters(List<Simulation> simulations) {
        List parameters = []
        simulations.each { Simulation simulation ->
            Parameterization parameterization = simulation.parameterization
            parameterization.load()
            parameters << parameterization
        }
        return parameters
    }


}