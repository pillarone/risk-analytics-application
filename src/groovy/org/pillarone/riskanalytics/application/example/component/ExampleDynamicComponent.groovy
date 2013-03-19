package org.pillarone.riskanalytics.application.example.component

import models.application.TestClaim
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.packets.Packet

class ExampleDynamicComponent extends DynamicComposedComponent {

    PacketList<TestClaim> outValue1 = new PacketList<TestClaim>(TestClaim)
    PacketList<Packet> outValue2 = new PacketList<Packet>(Packet)

    void wire() {

    }

    Component createDefaultSubComponent() {
        return new ExampleInputOutputComponentWithSubcomponent()
    }


}