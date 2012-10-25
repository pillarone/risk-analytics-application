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
import org.pillarone.riskanalytics.core.components.IResource
import org.pillarone.riskanalytics.core.output.PacketCollector

class RuntimeParameterCollector implements IModelVisitor {

    Set<RuntimeParameterDescriptor> runtimeParameters = new HashSet<RuntimeParameterDescriptor>()

    void visitModel(Model model) {
        //this visitor is used when no collectors are attached, but we need to catch runtime params in PacketCollector
        //we make the call here because visitModel is called only once.
        visitComponent(new PacketCollector(), null)
    }

    void visitComponent(Component component, ModelPath path) {
        for (Map.Entry<String, Object> entry in GroovyUtils.getProperties(component)) {
            final String propertyName = entry.key
            if (propertyName.startsWith(RPC.RUNTIME_PARAMETER_PREFIX)) {
                final def propertyValue = entry.value
                if (propertyValue == null) {
                    throw new IllegalStateException("Runtime parameter ${component.class.name}.${propertyName} must have a default value")
                }
                addDescriptor(new RuntimeParameterDescriptor(propertyName: propertyName, typeClass: propertyValue.class, value: propertyValue))
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

    void visitResource(IResource resource, ModelPath path) {
        for (Map.Entry<String, Object> entry in GroovyUtils.getProperties(resource)) {
            final String propertyName = entry.key
            if (propertyName.startsWith(RPC.RUNTIME_PARAMETER_PREFIX)) {
                final def propertyValue = entry.value
                if (propertyValue == null) {
                    throw new IllegalStateException("Runtime parameter ${resource.class.name}.${propertyName} must have a default value")
                }
                addDescriptor(new RuntimeParameterDescriptor(propertyName: propertyName, typeClass: propertyValue.class, value: propertyValue))
            }
        }
    }

    private void addDescriptor(RuntimeParameterDescriptor descriptor) {
        if (!runtimeParameters.contains(descriptor)) {
            RuntimeParameterDescriptor existing = runtimeParameters.find { it.propertyName == descriptor.propertyName}
            if (existing != null) {
                throw new IllegalStateException("Ambiguous runtime parameter ${descriptor.propertyName}: ${descriptor.typeClass.simpleName} and ${existing.typeClass.simpleName}")
            }
            runtimeParameters << descriptor
        }
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
