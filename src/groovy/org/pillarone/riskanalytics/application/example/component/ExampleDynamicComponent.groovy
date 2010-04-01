package org.pillarone.riskanalytics.application.example.component

import models.application.TestClaim
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList

class ExampleDynamicComponent extends DynamicComposedComponent {

    PacketList<TestClaim> outValue1 = new PacketList<TestClaim>(TestClaim)

    void wire() {

    }

    Component createDefaultSubComponent() {
        return new ExampleInputOutputComponentWithSubcomponent()
    }


}