package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder

class ParameterizationClassifierTableTreeNode extends AbstractMultiValueParameterizationTableTreeNode {

    private Model simulationModel

    public ParameterizationClassifierTableTreeNode(String path, ParametrizedItem item, Model simulationModel) {
        super(path, item);
        this.simulationModel = simulationModel
        name = "type"
    }

    public List initValues() {
        List possibleValues = []
        ParameterObjectParameterHolder parameterObjectHolder = parametrizedItem.getParameterHoldersForAllPeriods(parameterPath)[0]
        IParameterObjectClassifier classifier = parameterObjectHolder.classifier
        List<IParameterObjectClassifier> classifiers = simulationModel.configureClassifier(parameterObjectHolder.path, classifier.classifiers)
        classifiers.each {
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
        ParameterHolder parameterHolder = parametrizedItem.getParameterHolder(parameterPath, column - 1)
        LOG.debug("Setting value to node @ ${path} P${column - 1}")
        parameterHolder.value = getKeyForValue(value)
    }

    public doGetExpandedCellValue(int column) {
        ParameterObjectParameterHolder parameterObjectHolder = parametrizedItem.getParameterHolder(parameterPath, column - 1)
        getValueForKey(parameterObjectHolder?.classifier?.toString())
    }
}

class CompareParameterizationClassifierTableTreeNode extends ParameterizationClassifierTableTreeNode {

    List<ParametrizedItem> itemsToCompare = []
    int size

    public CompareParameterizationClassifierTableTreeNode(String path, List<ParametrizedItem> items, int size, Model model) {
        super(path, items[0], model);
        this.itemsToCompare = items
        this.size = size
    }

    public void setValueAt(Object value, int column) {
    }

    @Override
    Object getExpandedCellValue(int column) {
        if (itemsToCompare[getParameterizationIndex(column)].hasParameterAtPath(parameterPath, getPeriodIndex(column))) {
            return doGetExpandedCellValue(column)
        }
        return null
    }

    public doGetExpandedCellValue(int column) {
        ParameterObjectParameterHolder parameterObjectHolder = itemsToCompare[getParameterizationIndex(column)].getParameterHolder(parameterPath, getPeriodIndex(column))
        return getValueForKey(parameterObjectHolder?.classifier?.toString())
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
