package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.Parameterization


class MultiDimensionalParameterCompareViewModel {

    AbstractMultiDimensionalParameter referenceParameter
    List<AbstractMultiDimensionalParameter> comparedParameters

    List<Parameterization> parameterizations

    int periodIndex

    MultiDimensionalParameterCompareViewModel(AbstractMultiDimensionalParameter referenceParameter, List<AbstractMultiDimensionalParameter> comparedParameters, List<Parameterization> parameterizations, int periodIndex) {
        this.comparedParameters = comparedParameters
        this.referenceParameter = referenceParameter
        this.parameterizations = parameterizations
        this.periodIndex = periodIndex

        if(parameterizations.size() != comparedParameters.size() + 1) {
            throw new IllegalArgumentException("There must be a parameterization for every supplied parameter.")
        }
    }


}
