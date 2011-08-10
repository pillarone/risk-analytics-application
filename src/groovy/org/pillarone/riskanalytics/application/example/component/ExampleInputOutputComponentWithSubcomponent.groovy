package org.pillarone.riskanalytics.application.example.component

import org.pillarone.riskanalytics.core.components.ComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.packets.SingleValuePacket

class ExampleInputOutputComponentWithSubcomponent extends ComposedComponent {

    PacketList<SingleValuePacket> outFirstValue = new PacketList<SingleValuePacket>(SingleValuePacket)
    PacketList<SingleValuePacket> outSecondValue = new PacketList<SingleValuePacket>(SingleValuePacket)

    Integer parmFirstParameter = 1
    Integer parmSecondParameter = 2

    String runtimeStringParameter = "test"

    ExampleSimpleParameterComponent subFirstComponent = new ExampleSimpleParameterComponent()
    ExampleSimpleParameterComponent subSecondComponent = new ExampleSimpleParameterComponent()

    void wire() {

    }


}