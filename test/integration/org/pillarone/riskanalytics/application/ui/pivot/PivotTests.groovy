package org.pillarone.riskanalytics.application.ui.pivot

import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ULCFrameOperator
import org.pillarone.riskanalytics.application.ui.pivot.view.PivotView
import org.pillarone.riskanalytics.application.ui.pivot.model.PivotModel
import org.pillarone.riskanalytics.application.ui.pivot.model.TreeStructureModel
import org.pillarone.riskanalytics.application.ui.pivot.model.Dimension
import org.pillarone.riskanalytics.application.ui.pivot.model.Coordinate


class PivotTests extends AbstractSimpleFunctionalTest {


    @Override
    protected void doStart() {
        ULCFrame frame = new ULCFrame("test")

        PivotView view = new PivotView(new PivotModel(new TestModel()))

        frame.setSize(800, 600)
        frame.setContentPane(view.content)
        frame.visible = true
    }

    void testFrame() {
        ULCFrameOperator frameOperator = new ULCFrameOperator("test")
        sleep 60000
    }

    class TestModel implements TreeStructureModel {
        /*List<Dimension> getDimensions() {
            return [new Dimension (id:  1, name: "Dimension  1", coordinates: [new Coordinate (id:  1, name: "coordinate 1/1"),
                                                                               new Coordinate (id:  2, name: "coordinate 1/2")]),

                    new Dimension (id:  2, name: "Dimension  2", coordinates: [new Coordinate (id:  3, name: "coordinate 2/1"),
                                                                               new Coordinate (id:  4, name: "coordinate 2/2"),
                                                                               new Coordinate (id:  5, name: "coordinate 2/3")]),

                    new Dimension (id:  3, name: "Dimension  3", coordinates: [new Coordinate (id:  6, name: "coordinate 3/1"),
                                                                               new Coordinate (id:  7, name: "coordinate 3/2"),
                                                                               new Coordinate (id:  8, name: "coordinate 3/3"),
                                                                               new Coordinate (id:  9, name: "coordinate 3/4"),
                                                                               new Coordinate (id: 10, name: "coordinate 3/5"),
                                                                               new Coordinate (id: 11, name: "coordinate 3/6"),
                                                                               new Coordinate (id: 12, name: "coordinate 3/7"),
                                                                               new Coordinate (id: 13, name: "coordinate 3/8"),
                                                                               new Coordinate (id: 14, name: "coordinate 3/9")]),

                    new Dimension (id:  4, name: "Dimension  4", coordinates: [new Coordinate (id: 15, name: "coordinate 4/1")]),
                    new Dimension (id:  5, name: "Dimension  5", coordinates: [new Coordinate (id: 16, name: "coordinate 5/1")]),
                    new Dimension (id:  6, name: "Dimension  6", coordinates: [new Coordinate (id: 17, name: "coordinate 6/1")]),
                    new Dimension (id:  7, name: "Dimension  7", coordinates: [new Coordinate (id: 18, name: "coordinate 7/1")]),
                    new Dimension (id:  8, name: "Dimension  8", coordinates: [new Coordinate (id: 19, name: "coordinate 8/1")]),
                    new Dimension (id:  9, name: "Dimension  9", coordinates: [new Coordinate (id: 20, name: "coordinate 9/1")]),
                    new Dimension (id: 10, name: "Dimension 10", coordinates: [new Coordinate (id: 21, name: "coordinate 10/1")]),
                    new Dimension (id: 11, name: "Dimension 11", coordinates: [new Coordinate (id: 22, name: "coordinate 11/1")])
            ]
        }*/
        List<Dimension> getDimensions() {
            return [new Dimension (id:  1, name: "LOB",          coordinates: [new Coordinate (id:  1, name: "Property"),
                                                                               new Coordinate (id:  2, name: "Description")]),

                    new Dimension (id:  2, name: "Key Figure",   coordinates: [new Coordinate (id:  3, name: "Claims Reported"),
                                                                               new Coordinate (id:  4, name: "Claims Paid"),
                                                                               new Coordinate (id:  5, name: "Premiums earned")]),

                    new Dimension (id:  3, name: "Period",       coordinates: [new Coordinate (id:  6, name: "P1"),
                                                                               new Coordinate (id:  7, name: "P2"),
                                                                               new Coordinate (id:  8, name: "P3")]),

                    new Dimension (id:  4, name: "Country",      coordinates: [new Coordinate (id: 9, name: "CH"),
                                                                               new Coordinate (id: 10, name: "DE"),
                                                                               new Coordinate (id: 12, name: "FR")]),

                    new Dimension (id:  5, name: "Legal Entity", coordinates: [new Coordinate (id: 13, name: "Legal Entity 1"),
                                                                               new Coordinate (id: 14, name: "Legal Entity 2")]),

                    new Dimension (id:  6, name: "Iteration",    coordinates: [new Coordinate (id: 15, name: "Iteration 1"),
                                                                               new Coordinate (id: 16, name: "Iteration 2")]),

                    new Dimension (id:  7, name: "Run ID",       coordinates: [new Coordinate (id: 17, name: "Run ID 1"),
                                                                               new Coordinate (id: 18, name: "Run ID 2")])
            ]
        }
    }

}
