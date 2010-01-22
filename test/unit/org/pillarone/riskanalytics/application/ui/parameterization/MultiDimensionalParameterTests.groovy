package org.pillarone.riskanalytics.application.ui.parameterization

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.example.constraint.LinePercentage
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.MatrixMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.MultiDimensionalParameterDimension
import org.pillarone.riskanalytics.core.parameterization.SimpleConstraint
import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter

class MultiDimensionalParameterTests extends GroovyTestCase {

    protected void setUp() {
        super.setUp();
        ConstraintsFactory.registerConstraint(new SimpleConstraint())
        ConstraintsFactory.registerConstraint(new LinePercentage())
    }


    void testConstructor() {
        AbstractMultiDimensionalParameter param = new SimpleMultiDimensionalParameter([1, 2, 3])
        assertTrue param.valuesConverted

        param = new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]])
        assertFalse param.valuesConverted
    }

    void testRowCount() {
        AbstractMultiDimensionalParameter param = new SimpleMultiDimensionalParameter([1, 2, 3])
        assertEquals(3, param.rowCount)

        param = new SimpleMultiDimensionalParameter([[1, 2], [4, 5]])
        assertEquals(2, param.rowCount)
    }

    void testColumnCount() {
        AbstractMultiDimensionalParameter param = new SimpleMultiDimensionalParameter([1, 2, 3])
        assertEquals(1, param.columnCount)

        param = new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5]])
        assertEquals(2, param.columnCount)
    }

    void testGetValueAt() {
        AbstractMultiDimensionalParameter param = new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5]])
        assertEquals(4, param.getValueAt(0, 1))
        assertEquals("", param.getValueAt(2, 1))
    }

    void testGetValue() {
        AbstractMultiDimensionalParameter param = new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5]])
        assertEquals([[1, 2, 3], [4, 5]], param.getValues())
        param = new SimpleMultiDimensionalParameter([1, 2, 3])
        assertEquals([1, 2, 3], param.getValues())
    }

    void testSetValueAt() {
        AbstractMultiDimensionalParameter param = new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5]])
        param.setValueAt(2.2, 0, 1)
        assertEquals(2.2, param.getValueAt(0, 1))

        param.setValueAt(6, 2, 1)
        assertEquals(6, param.getValueAt(2, 1))

        // column index out of bound
        shouldFail(IndexOutOfBoundsException, {param.setValueAt(7, 1, 2)})

        // row index out of bound
        shouldFail(IndexOutOfBoundsException, {param.setValueAt(7, 3, 0)})
    }

    void testIncreaseDimension() {
        AbstractMultiDimensionalParameter param = new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]])

        assertEquals 3, param.rowCount
        assertEquals 2, param.columnCount

        param.dimension = new MultiDimensionalParameterDimension(3, 3)
        assertEquals 3, param.rowCount
        assertEquals 3, param.columnCount
        assertTrue param.values.every { it.size() == 3}

        param.dimension = new MultiDimensionalParameterDimension(3, 4)
        assertEquals 4, param.rowCount
        assertEquals 3, param.columnCount


        param = new SimpleMultiDimensionalParameter([1, 2, 3])

        assertEquals 3, param.rowCount
        assertEquals 1, param.columnCount

        param.dimension = new MultiDimensionalParameterDimension(2, 3)

        assertEquals 3, param.rowCount
        assertEquals 2, param.columnCount

        param.setValueAt(4, 0, 1)
        param.setValueAt(5, 1, 1)
        param.setValueAt(6, 2, 1)

    }

    void testDecreaseDimension() {
        AbstractMultiDimensionalParameter param = new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]])

        assertEquals 3, param.rowCount
        assertEquals 2, param.columnCount

        param.dimension = new MultiDimensionalParameterDimension(2, 2)
        assertEquals 2, param.rowCount
        assertEquals 2, param.columnCount

        param.dimension = new MultiDimensionalParameterDimension(2, 1)
        assertEquals 1, param.rowCount
        assertEquals 2, param.columnCount

        param = new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5]])
        param.dimension = new MultiDimensionalParameterDimension(2, 2)
        assertEquals 2, param.rowCount
        assertEquals 2, param.columnCount
        junit.framework.Assert.assertEquals("second row not shrinked", 2, param.values[1].size())

        param = new MatrixMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]], ['row1', 'row2', 'row3'], ['col1', 'col2'])
        param.dimension = new MultiDimensionalParameterDimension(1, 2)
        assertEquals 3, param.rowCount
        assertEquals 2, param.columnCount

        assertEquals 2, param.getRowNames().size()
        assertEquals 1, param.getColumnNames().size()

        param = new ComboBoxMatrixMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]], ['col1', 'col2', 'col3'], ITestComponentMarker)
        param.dimension = new MultiDimensionalParameterDimension(2, 2)
        assertEquals 3, param.rowCount
        assertEquals 3, param.columnCount

        assertEquals 2, param.getRowNames().size()
        assertEquals 2, param.getColumnNames().size()

    }

    void testGetColumnByName() {
        TableMultiDimensionalParameter param = new TableMultiDimensionalParameter([[1], [2]], ['Col 1', 'Col 2'])

        List col1 = param.getColumnByName('Col 1')
        assertEquals 1, col1[0]

        List col2 = param.getColumnByName('Col 2')
        assertEquals 2, col2[0]
        shouldFail(IllegalArgumentException, {
            List col3 = param.getColumnByName('Col 3')
            assertEquals 2, col3[0]
        })
    }

    void testIsEditable() {
        AbstractMultiDimensionalParameter param = new MatrixMultiDimensionalParameter([1, 2, 3], ['Row'], ['Col'])

        assertFalse param.isCellEditable(0, 0)
        assertFalse param.isCellEditable(0, 1)
        assertFalse param.isCellEditable(4, 0)

        assertTrue param.isCellEditable(1, 1)

    }


    void testToString() {
        AbstractMultiDimensionalParameter param = new SimpleMultiDimensionalParameter([1, 2, 3])
        param.max_tokens = 1
        String output = "new org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter(\n" +
                "\"\"\"\\\n" +
                "[1,2,3]\n" +
                "\"\"\"\n" +
                ")"

        assertEquals output, param.toString()

        param = new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]])
        param.max_tokens = 1
        output = "new org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter(\n" +
                "\"\"\"\\\n" +
                "[[1,2,3],\n" +
                "[4,5,6]\n" +
                "]\n" +
                "\"\"\"\n" +
                ")"

        assertEquals output, param.toString()
        param = new TableMultiDimensionalParameter([[1, 2, 3], [4, 5, 6], [7, 8, 9]], ['title1', 'title2', 'title3'])
        param.max_tokens = 1
        output = "new org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter(\n" +
                "\"\"\"\\\n" +
                "[[1,2,3],\n" +
                "[4,5,6],\n" +
                "[7,8,9]\n" +
                "]\n" +
                "\"\"\"\n" +
                ",[\"title1\",\"title2\",\"title3\"])"
        assertEquals output, param.toString()

        param = new MatrixMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]], ["row1", "row2"], ["col1", "col2"])
        param.max_tokens = 1
        output = "new org.pillarone.riskanalytics.core.parameterization.MatrixMultiDimensionalParameter(\n" +
                "\"\"\"\\\n" +
                "[[1,2,3],\n" +
                "[4,5,6]\n" +
                "]\n" +
                "\"\"\"\n" +
                ",[\"row1\",\"row2\"],[\"col1\",\"col2\"])"
        assertEquals output, param.toString()

        param = new ConstrainedMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]], ["row1", "row2"], ConstraintsFactory.getConstraints(SimpleConstraint.IDENTIFIER))
        param.max_tokens = 1
        output = "new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(\n" +
                "\"\"\"\\\n" +
                "[[1,2,3],\n" +
                "[4,5,6]\n" +
                "]\n" +
                "\"\"\"\n" +
                ",[\"row1\",\"row2\"], org.pillarone.modelling.parameterization.ConstraintsFactory.getConstraints('SIMPLE_CONSTRAINT'))"
        //TODO: temp. fix because of bug in core.. this test should be moved to core where possible
        assertEquals output, param.toString()
    }

    void testGetPossibleValues() {
        LocaleResources.setTestMode()

        AbstractMultiDimensionalParameter param = new ComboBoxMatrixMultiDimensionalParameter([0], ['hierarchy component'], ITestComponentMarker)
        Model model = new ApplicationModel()
        model.init()
        model.injectComponentNames()
        param.simulationModel = model

        def values = param.getPossibleValues(0, 1)
        assertEquals 1, values.size()
        assertTrue values.contains('hierarchy component')


        param = new ConstrainedMultiDimensionalParameter([['hierarchy component'], [0d]], ['line', 'percentage'], ConstraintsFactory.getConstraints(LinePercentage.IDENTIFIER))
        model = new ApplicationModel()
        model.init()
        model.injectComponentNames()
        param.simulationModel = model

        values = param.getPossibleValues(1, 0)
        assertEquals 1, values.size()
        assertTrue values.contains('hierarchy component')

        values = param.getPossibleValues(1, 1)
        assertFalse values instanceof Collection

        LocaleResources.clearTestMode()
    }

    void testGetValuesAsObjects() {
        AbstractMultiDimensionalParameter param = new ComboBoxTableMultiDimensionalParameter(['hierarchy component', 'hierarchy component'], ['line'], ITestComponentMarker)
        Model model = new ApplicationModel()
        model.init()
        model.injectComponentNames()
        param.simulationModel = model

        List lobs = param.getValuesAsObjects()
        assertEquals 2, lobs.size()
        assertEquals 'hierarchyComponent', lobs.get(0).name
        assertEquals 'hierarchyComponent', lobs.get(1).name

        param = new ComboBoxTableMultiDimensionalParameter([['hierarchy component', 'hierarchy component'], ['hierarchy component', 'hierarchy component']], ['line 1', 'line 2'], ITestComponentMarker)
        model = new ApplicationModel()
        model.init()
        model.injectComponentNames()
        param.simulationModel = model

        lobs = param.getValuesAsObjects()
        assertEquals 2, lobs.size()

        assertEquals 'hierarchyComponent', lobs.get(0).get(0).name
        assertEquals 'hierarchyComponent', lobs.get(0).get(1).name
        assertEquals 'hierarchyComponent', lobs.get(1).get(0).name
        assertEquals 'hierarchyComponent', lobs.get(1).get(1).name
    }

    //TODO: (msp) enable when new simulation engine is used
    /*void testSetSimulationModel() {
        CapitalEagleModel model = new CapitalEagleModel()
        model.init()
        model.injectComponentNames()

        AbstractMultiDimensionalParameter mdp = new ComboBoxMatrixMultiDimensionalParameter([[0, 1], [1, 0]], ['invalid name', 'mtpl'], LobMarker)
        mdp.setSimulationModel(model)

        assertFalse "invalid name" == mdp.getValueAt(0, 1)
        assertEquals "mtpl", mdp.getValueAt(0, 2)

        mdp = new ComboBoxTableMultiDimensionalParameter([['invalid name', 'mtpl']], ['title'], LobMarker)
        mdp.setSimulationModel(model)

        assertFalse "invalid name" == mdp.getValueAt(1, 0)
        assertEquals "mtpl", mdp.getValueAt(2, 0)

        mdp = new ConstrainedMultiDimensionalParameter([['invalid name', 'mtpl'], [0.5, 0.5]], ['line', '%'], ConstraintsFactory.getConstraints(ConstraintsFactory.LINE_PERCENTAGE))
        mdp.setSimulationModel(model)

        assertFalse "invalid name" == mdp.getValueAt(1, 0)
        assertEquals "mtpl", mdp.getValueAt(2, 0)
    }*/

}