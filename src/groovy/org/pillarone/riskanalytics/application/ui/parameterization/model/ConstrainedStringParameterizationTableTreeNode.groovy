package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.parameter.ConstrainedStringParameter
import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractMultiValueParameterizationTableTreeNode
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.parameter.ConstrainedStringParameterHolder

class ConstrainedStringParameterizationTableTreeNode extends AbstractMultiValueParameterizationTableTreeNode {

    private Model simulationModel
    private Class markerClass
    private Map normalizedToName = [:]
    private Map nameToNormalized = [:]

    public ConstrainedStringParameterizationTableTreeNode(List parameter, Model model) {
        super(parameter);
        this.simulationModel = model
        ConstrainedStringParameterHolder holder = parameter.find { it != null}
        markerClass = holder.businessObject.getMarkerClass()
    }

    public Object getExpandedCellValue(int column) {
        ConstrainedStringParameterHolder param = parameter.get(column - 1)
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
        parameter.get(column - 1)?.value = normalizedToName.get(value)
    }

    protected List initValues() {
        List components = simulationModel.getMarkedComponents(markerClass)
        List result = []
        for (Component c in components) {
            result << c.getNormalizedName()
            fillMaps(c)
        }
        setValidValueIfEmpty(result)
        return result
    }

    //if a new DCC which contains a CS is added this makes sure the displayed value is also stored in the parameter
    private void setValidValueIfEmpty(List values) {
        if (values.size() > 0) {
            int i = 1;
            for (ConstrainedStringParameterHolder holder in parameter) {
                String normalizedName = nameToNormalized.get(holder.businessObject.stringValue)
                if (normalizedName == null || !values.contains(normalizedName)) {
                    setValueAt(values[0], i)
                }
                i++
            }
        }
    }

    public void addComponent(Component component) {
        if (markerClass.isAssignableFrom(component.getClass())) {
            values << component.getNormalizedName()
            fillMaps(component)
        }

    }

    public void removeComponent(Component component) {
        if (markerClass.isAssignableFrom(component.getClass())) {
            values.remove(component.getNormalizedName())
            normalizedToName.remove(component.getNormalizedName())
            nameToNormalized.remove(component.getName())
        }
    }

    private void fillMaps(Component component) {
        normalizedToName.put(component.getNormalizedName(), component.getName())
        nameToNormalized.put(component.getName(), component.getNormalizedName())
    }

}