package org.pillarone.riskanalytics.application.example.component

import org.pillarone.riskanalytics.core.components.ComposedComponent

class ExampleComposedComponent extends ComposedComponent {

    ExampleDynamicComponent subDynamicComponent = new ExampleDynamicComponent()

    void wire() {

    }


}