package org.pillarone.riskanalytics.application.example.component

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum

class ExampleSimpleParameterComponent extends Component implements ITestComponentMarker {

    Integer parmValue = 10

    DateTime runtimeDateParameter = new DateTime(2011, 1, 1, 0, 0, 0, 0)
    Integer runtimeIntParameter = 5
    Double runtimeDoubleParameter = 1.1
    ExampleEnum runtimeEnumParameter = ExampleEnum.SECOND_VALUE
    Boolean runtimeBooleanParameter = true

    protected void doCalculation() {

    }


}