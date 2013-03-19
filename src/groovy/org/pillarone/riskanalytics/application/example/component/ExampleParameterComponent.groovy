package org.pillarone.riskanalytics.application.example.component

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.example.marker.ITest2ComponentMarker
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum
import org.pillarone.riskanalytics.core.example.parameter.ExampleParameterObject
import org.pillarone.riskanalytics.core.example.parameter.ExampleParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker
import org.pillarone.riskanalytics.core.packets.Packet
import org.pillarone.riskanalytics.core.packets.PacketList

class ExampleParameterComponent extends Component implements ITest2ComponentMarker{

    AbstractMultiDimensionalParameter parmMultiDimensionalParameter = new SimpleMultiDimensionalParameter([[0, 1], [2, 3]])
    ExampleEnum parmEnumParameter = ExampleEnum.FIRST_VALUE
    ExampleParameterObject parmNestedMdp = ExampleParameterObjectClassifier.NESTED_MDP.getParameterObject(ExampleParameterObjectClassifier.NESTED_MDP.parameters)
    ConstrainedString parmMarkerValue = new ConstrainedString(ITestComponentMarker, "value")

    PacketList<Packet> outValue2 = new PacketList<Packet>(Packet)
    

    protected void doCalculation() {

    }


}