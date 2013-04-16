package org.pillarone.riskanalytics.application.dataaccess.function

import groovy.transform.CompileStatic
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

@CompileStatic
class ParametrizedFunctionDescriptor extends FunctionDescriptor {

    def parameter

    ParametrizedFunctionDescriptor(Class functionClass, def parameter) {
        super(functionClass)
        this.parameter = parameter
    }

    @Override
    int hashCode() {
        return new HashCodeBuilder().append(functionClass.getName()).append(parameter).toHashCode()
    }

    @Override
    boolean equals(Object obj) {
        if (obj instanceof ParametrizedFunctionDescriptor) {
            return new EqualsBuilder().append(functionClass, obj.getFunctionClass()).append(parameter, obj.getParameter()).equals
        }
        return false
    }
}
