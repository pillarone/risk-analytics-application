package org.pillarone.riskanalytics.application.example.resource

import org.pillarone.riskanalytics.core.components.AbstractResource
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope

class ApplicationResource extends AbstractResource {

    boolean defaultCalled = false
    SimulationScope simulationScope

    int parmInteger = 10
    String parmString = "test"

    void useDefault() {
        defaultCalled = true
    }

    static structure = {
        integer {
            parmInteger()
        }

        string {
            parmString()
        }
    }


}
