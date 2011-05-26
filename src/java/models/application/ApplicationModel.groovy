package models.application

import org.pillarone.riskanalytics.application.example.component.ExampleComposedComponent
import org.pillarone.riskanalytics.application.example.component.ExampleDynamicComponent
import org.pillarone.riskanalytics.application.example.component.ExampleParameterComponent
import org.pillarone.riskanalytics.application.example.component.ExampleSimpleParameterComponent
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.example.component.ExampleParameterComponent as EPC

/**
 * Model with different components that can be used for tests in the application plugin
 */
class ApplicationModel extends StochasticModel {

    EPC globalParameterComponent
    ExampleParameterComponent parameterComponent
    ExampleSimpleParameterComponent hierarchyComponent
    ExampleDynamicComponent dynamicComponent
    ExampleComposedComponent composedComponent

    void initComponents() {
        globalParameterComponent = new EPC()
        parameterComponent = new ExampleParameterComponent()
        hierarchyComponent = new ExampleSimpleParameterComponent()
        dynamicComponent = new ExampleDynamicComponent()
        composedComponent = new ExampleComposedComponent()
    }

    void wireComponents() {

    }


}
