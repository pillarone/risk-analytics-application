package models.deterministicApplication

import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum
import org.pillarone.riskanalytics.core.example.parameter.ExampleParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter

model = DeterministicApplicationModel
periodCount = 1

components {
    parameterComponent {
        parmMultiDimensionalParameter[0] = new SimpleMultiDimensionalParameter([[0, 1], [2, 3]])
        parmEnumParameter[0] = ExampleEnum.FIRST_VALUE
        parmNestedMdp[0] = ExampleParameterObjectClassifier.NESTED_MDP.getParameterObject(ExampleParameterObjectClassifier.NESTED_MDP.parameters)
        parmMarkerValue[0] = new ConstrainedString(ITestComponentMarker, "value")
    }
    hierarchyComponent {
        parmValue[0] = 10
    }
    dynamicComponent {
        subTest {
            parmFirstParameter[0] = 1
            parmSecondParameter[0] = 2
            subFirstComponent {
                parmValue[0] = 10
            }
            subSecondComponent {
                parmValue[0] = 10
            }
        }
    }
    composedComponent {
        subDynamicComponent {
            subNested {
                parmFirstParameter[0] = 1
                parmSecondParameter[0] = 2
                subFirstComponent {
                    parmValue[0] = 10
                }
                subSecondComponent {
                    parmValue[0] = 10
                }
            }
        }
    }
}
