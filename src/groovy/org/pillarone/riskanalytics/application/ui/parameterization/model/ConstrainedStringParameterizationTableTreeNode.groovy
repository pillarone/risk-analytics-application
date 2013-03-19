package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem
import org.pillarone.riskanalytics.core.simulation.item.parameter.ConstrainedStringParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

class ConstrainedStringParameterizationTableTreeNode extends AbstractMultiValueParameterizationTableTreeNode {

    private Model simulationModel
    private Class markerClass
    private Map normalizedToName = [:]
    private Map nameToNormalized = [:]

    public ConstrainedStringParameterizationTableTreeNode(String path, ParametrizedItem item, Model model) {
        super(path, item);
        this.simulationModel = model
        ConstrainedStringParameterHolder holder = parametrizedItem.getParameterHoldersForAllPeriods(parameterPath)[0]
        markerClass = holder.businessObject.getMarkerClass()
    }

    public Object doGetExpandedCellValue(int column) {
        ConstrainedStringParameterHolder param = parametrizedItem.getParameterHolder(parameterPath, column - 1)
        def value = param?.businessObject?.stringValue
        if (value) {
            if (nameToNormalized.containsKey(value)) {
                value = nameToNormalized.get(value)
            } else {
                //when a new component is added dynamically, the parameter value is the component default which is
                //not contained by this map
                List values = nameToNormalized.values().toList()
                if (values.size() != 0) {
                    value = values.get(0)
                }
            }
        }
        return value
    }

    public void setValueAt(Object value, int column) {
        int period = column - 1
        LOG.debug("Setting value to node @ ${path} P${period}")
        parametrizedItem.updateParameterValue(parameterPath, period, normalizedToName.get(value))
    }

    protected List initValues() {
        List components = simulationModel.getMarkedComponents(markerClass)
        List<String> result = []
        for (Component c in components) {
            result << c.getNormalizedName()
            fillMaps(c)
        }
        validateValue(result)
        return result
    }

    //if a new DCC which contains a CS is added this makes sure the displayed value is also stored in the parameter

    private void validateValue(List values) {
        int i = 1;
        for (ConstrainedStringParameterHolder holder in parametrizedItem.getParameterHoldersForAllPeriods(parameterPath)) {
            String normalizedName = nameToNormalized.get(holder.businessObject.stringValue)
            if (normalizedName == null || !values.contains(normalizedName)) {
                if (values.empty) {
                    if (holder.businessObject.stringValue != "") {
                        parametrizedItem.updateParameterValue(holder.path, holder.periodIndex, "")
                    }
                } else {
                    setValueAt(values[0], i)
                }
            }
            i++
        }
    }

    public void addComponent(Component component) {
        if (markerClass.isAssignableFrom(component.getClass())) {
            values << component.getNormalizedName()
            fillMaps(component)
            validateValue(values)
        }

    }

    public void removeComponent(Component component) {
        if (markerClass.isAssignableFrom(component.getClass())) {
            values.remove(component.getNormalizedName())
            normalizedToName.remove(component.getNormalizedName())
            nameToNormalized.remove(component.getName())
            validateValue(values)
        }
    }

    private void fillMaps(Component component) {
        normalizedToName.put(component.getNormalizedName(), component.getName())
        nameToNormalized.put(component.getName(), component.getNormalizedName())
    }

}