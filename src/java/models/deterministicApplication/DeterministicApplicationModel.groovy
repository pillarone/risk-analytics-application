package models.deterministicApplication

import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.joda.time.Period
import org.pillarone.riskanalytics.application.example.component.ExampleComposedComponent
import org.pillarone.riskanalytics.application.example.component.ExampleDynamicComponent
import org.pillarone.riskanalytics.application.example.component.ExampleSimpleParameterComponent
import org.pillarone.riskanalytics.application.example.component.ExampleParameterComponent


class DeterministicApplicationModel extends DeterministicModel {

    ExampleParameterComponent parameterComponent
    ExampleSimpleParameterComponent hierarchyComponent
    ExampleDynamicComponent dynamicComponent
    ExampleComposedComponent composedComponent

    void initComponents() {
        parameterComponent = new ExampleParameterComponent()
        hierarchyComponent = new ExampleSimpleParameterComponent()
        dynamicComponent = new ExampleDynamicComponent()
        composedComponent = new ExampleComposedComponent()
    }

    void wireComponents() {

    }

    @Override
    Period getPeriodLength() {
        return Period.months(1)
    }

}
