package org.pillarone.riskanalytics.application.example.component

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.packets.Packet

class ExampleInputOutputComponentWithSubcomponent extends ComposedComponent {

    PacketList<Packet> outFirstValue = new PacketList<Packet>()
    PacketList<Packet> outSecondValue = new PacketList<Packet>()

    Integer parmFirstParameter = 1
    Integer parmSecondParameter = 2

    ExampleSimpleParameterComponent subFirstComponent = new ExampleSimpleParameterComponent()
    ExampleSimpleParameterComponent subSecondComponent = new ExampleSimpleParameterComponent()

    void wire() {

    }


}