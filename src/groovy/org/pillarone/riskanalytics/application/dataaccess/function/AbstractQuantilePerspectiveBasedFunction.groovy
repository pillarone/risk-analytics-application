package org.pillarone.riskanalytics.application.dataaccess.function

import org.pillarone.riskanalytics.core.output.QuantilePerspective

abstract class AbstractQuantilePerspectiveBasedFunction<E> extends AbstractResultFunction implements IParametrizedFunction<E> {

    protected QuantilePerspective quantilePerspective

    AbstractQuantilePerspectiveBasedFunction(QuantilePerspective quantilePerspective) {
        this.quantilePerspective = quantilePerspective
    }

    QuantilePerspective getQuantilePerspective() {
        return quantilePerspective
    }

    @Override
    String getDisplayName() {
        final String localizedName = super.getDisplayName()
        return "${getParameter()} $localizedName $quantilePerspective"
    }

    @Override
    FunctionDescriptor createDescriptor() {
        return new QuantileBasedFunctionDescriptor(getClass(), getParameter(), getQuantilePerspective())
    }
}
