package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.util.I18NUtilities
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
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
        ParameterObjectParameterHolder parameterObjectHolder = parametrizedItem.getParameterHoldersForFirstPeriod(parameterPath)
        IParameterObjectClassifier classifier = parameterObjectHolder.classifier
        List<IParameterObjectClassifier> classifiers = simulationModel.configureClassifier(parameterObjectHolder.path, classifier.classifiers)
        for(AbstractParameterObjectClassifier singleClassifier in classifiers){
            String resourceBundleKey = singleClassifier.typeName
            String modelKey = singleClassifier.toString()
            String value = I18NUtilities.findParameterDisplayName(parent, "type." + resourceBundleKey)
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
        int period = column - 1
        LOG.debug("Setting value to node @ ${parameterPath} P${period}")
        parametrizedItem.updateParameterValue(parameterPath, period, getKeyForValue(value))
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
