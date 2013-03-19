package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem

class MultiDimensionalParameterCompareViewModel {

    AbstractMultiDimensionalParameter referenceParameter
    List<AbstractMultiDimensionalParameter> comparedParameters

    List<ParametrizedItem> parametrizedItems

    int periodIndex

    MultiDimensionalParameterCompareViewModel(AbstractMultiDimensionalParameter referenceParameter, List<AbstractMultiDimensionalParameter> comparedParameters, List<ParametrizedItem> parametrizedItems, int periodIndex) {
        this.comparedParameters = comparedParameters
        this.referenceParameter = referenceParameter
        this.parametrizedItems = parametrizedItems
        this.periodIndex = periodIndex

        if(parametrizedItems.size() != comparedParameters.size() + 1) {
            throw new IllegalArgumentException("There must be a parameterization for every supplied parameter.")
        }
    }


}
