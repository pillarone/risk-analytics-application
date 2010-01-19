package org.pillarone.riskanalytics.application.example.component

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.components.Component

class ExampleDynamicComponent extends DynamicComposedComponent {

    void wire() {

    }

    Component createDefaultSubComponent() {
        return new ExampleInputOutputComponentWithSubcomponent()
    }


}