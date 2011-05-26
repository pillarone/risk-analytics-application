package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import org.pillarone.riskanalytics.core.simulation.engine.actions.RuntimeParameterCollector as RPC

import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.model.IModelVisitor
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.model.ModelPath
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.core.components.ComponentUtils

class RuntimeParameterCollector implements IModelVisitor {

    Set<RuntimeParameterDescriptor> runtimeParameters = new HashSet<RuntimeParameterDescriptor>()

    void visitModel(Model model) {
    }

    void visitComponent(Component component, ModelPath path) {
        for (Map.Entry<String, Object> entry in GroovyUtils.getProperties(component)) {
            final String propertyName = entry.key
            if (propertyName.startsWith(RPC.RUNTIME_PARAMETER_PREFIX)) {
                final def propertyValue = entry.value
                if (propertyValue == null) {
                    throw new IllegalStateException("Runtime parameter ${component.class.name}.${propertyName} must have a default value")
                }
                runtimeParameters << new RuntimeParameterDescriptor(propertyName: propertyName, typeClass: propertyValue.class, value: propertyValue)
            }
        }
        if (component instanceof DynamicComposedComponent) {
            if (component.getComponentList().empty) {
                visitComponent(component.createDefaultSubComponent(), path)
            }
        }
    }

    void visitParameterObject(IParameterObject parameterObject, ModelPath path) {
    }

    public static class RuntimeParameterDescriptor {

        String propertyName
        Class typeClass
        def value

        @Override
        int hashCode() {
            return new HashCodeBuilder().append(propertyName).append(typeClass).toHashCode()
        }

        @Override
        boolean equals(Object obj) {
            if (obj instanceof RuntimeParameterDescriptor) {
                return obj.propertyName == propertyName && obj.typeClass == typeClass
            }
            return false
        }

        String getDisplayName() {
            return ComponentUtils.getNormalizedName(propertyName)
        }

    }

}
