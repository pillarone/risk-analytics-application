package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder

class ParameterizationClassifierTableTreeNode extends AbstractMultiValueParameterizationTableTreeNode {

    public ParameterizationClassifierTableTreeNode(List parameter) {
        super(parameter);
        name = "type"
    }

    public List initValues() {
        List possibleValues = []
        ParameterObjectParameterHolder parameterObjectHolder = parameter.find { it != null }
        IParameterObjectClassifier classifier = parameterObjectHolder.classifier
        classifier.classifiers.each {
            String resourceBundleKey = it.typeName
            String modelKey = it.toString()
            String value = I18NUtils.findParameterDisplayName(parent, "type." + resourceBundleKey)
            if (value != null) {
                possibleValues << value
            } else {
                possibleValues << modelKey
            }
            localizedValues[value] = modelKey
            localizedKeys[modelKey] = value != null ? value : modelKey
        }
        return possibleValues
    }


    public void setValueAt(Object value, int column) {
        parameter.get(column - 1)?.value = getKeyForValue(value)
    }

    public getExpandedCellValue(int column) {
        ParameterObjectParameterHolder parameterObjectHolder = parameter.get(column - 1)
        getValueForKey(parameterObjectHolder?.classifier?.toString())
    }
}

class CompareParameterizationClassifierTableTreeNode extends ParameterizationClassifierTableTreeNode {

    Map parametersMap = [:]
    int size

    public CompareParameterizationClassifierTableTreeNode(Map parametersMap, int size) {
        super(parametersMap.get(0));
        this.parametersMap = parametersMap;
        this.size = size
    }

    public void setValueAt(Object value, int column) {
        parameter = parametersMap.get(getParameterizationIndex(column))
        parameter.get(getPeriodIndex(column)).value = getKeyForValue(value)
    }

    public getExpandedCellValue(int column) {
        String value = ""
        try {
            parameter = parametersMap.get(getParameterizationIndex(column))
            value = getValueForKey(parameter.get(getPeriodIndex(column))?.value)
        } catch (Exception ex) {
            value = getClassifier(parameter)
        }
        return value == "[]" ? "" : value
    }

    private String getClassifier(def parameter) {
        try {
            return parameter.classifier.displayName
        } catch (Exception ex) {
            return ""
        }
    }

    protected int getParameterizationIndex(int column) {
        if (column == 0)
            return 0
        return (column - 1) % size
    }

    protected int getPeriodIndex(int column) {
        if (column == 0)
            return 0
        return (column - 1).intdiv(size)
    }


}
