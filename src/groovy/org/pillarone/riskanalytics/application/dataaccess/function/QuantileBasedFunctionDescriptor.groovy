package org.pillarone.riskanalytics.application.dataaccess.function

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.core.output.QuantilePerspective

class QuantileBasedFunctionDescriptor extends FunctionDescriptor {

    def parameter
    QuantilePerspective quantilePerspective

    QuantileBasedFunctionDescriptor(Class functionClass, def parameter, QuantilePerspective quantilePerspective) {
        super(functionClass)
        this.parameter = parameter
        this.quantilePerspective = quantilePerspective
    }

    @Override
    int hashCode() {
        return new HashCodeBuilder().append(functionClass.getName()).append(parameter).append(quantilePerspective.toString()).toHashCode()
    }

    @Override
    boolean equals(Object obj) {
        if (obj instanceof QuantileBasedFunctionDescriptor) {
            return new EqualsBuilder().append(functionClass, obj.functionClass).append(parameter, obj.parameter).append(quantilePerspective, obj.quantilePerspective).equals
        }
        return false
    }
}
