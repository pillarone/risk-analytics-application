package org.pillarone.riskanalytics.application.example.component

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker

class ExampleSimpleParameterComponent extends Component implements ITestComponentMarker {

    Integer parmValue = 10

    protected void doCalculation() {

    }


}