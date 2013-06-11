package org.pillarone.riskanalytics.application.dataaccess.function

import groovy.transform.CompileStatic
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.EqualsBuilder


@CompileStatic
class FunctionDescriptor {

    Class functionClass

    FunctionDescriptor(Class functionClass) {
        this.functionClass = functionClass
    }

    @Override
    int hashCode() {
        return new HashCodeBuilder().append(functionClass.getName()).toHashCode()
    }

    @Override
    boolean equals(Object obj) {
        if(obj instanceof FunctionDescriptor) {
            return new EqualsBuilder().append(functionClass, ((FunctionDescriptor)obj).getFunctionClass()).equals
        }
        return false
    }
}
