package org.pillarone.riskanalytics.application.dataaccess.function

import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.EqualsBuilder


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
            return new EqualsBuilder().append(functionClass, obj.functionClass).equals
        }
        return false
    }
}
