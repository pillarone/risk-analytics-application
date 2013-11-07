package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel

public class TestMultiDimensionalParameterModel extends MultiDimensionalParameterModel {

    public TestMultiDimensionalParameterModel(model, node, columnIndex) {
        super(model, node, columnIndex);
    }

    public void save() {
    }

    public String getPathAsString() {
        ""
    }

    public void modelChanged() {
    }

    public def getMultiDimensionalParameterInstance() {
        return multiDimensionalParameter
    }
}