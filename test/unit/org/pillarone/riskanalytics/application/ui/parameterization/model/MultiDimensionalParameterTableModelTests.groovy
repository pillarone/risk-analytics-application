package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.event.ITableModelListener
import com.ulcjava.base.application.event.TableModelEvent
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.example.marker.ITestComponentMarker
import org.pillarone.riskanalytics.core.parameterization.*

class MultiDimensionalParameterTableModelTests extends GroovyTestCase {


    void testRowCount() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([1, 2, 3]))
        assertEquals(3, model.rowCount)
        assertEquals(3, model.valueRowCount)

        model = new MultiDimensionalParameterTableModel(new TableMultiDimensionalParameter([1, 2, 3], ['Column 1']))
        assertEquals(4, model.rowCount)
        assertEquals(3, model.valueRowCount)

        model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([[1, 2], [4, 5]]))
        assertEquals(2, model.rowCount)
        assertEquals(2, model.valueRowCount)

        model = new MultiDimensionalParameterTableModel(new TableMultiDimensionalParameter([[1, 2], [4, 5]], ['Column 1', 'Column 2']))
        assertEquals(3, model.rowCount)
        assertEquals(2, model.valueRowCount)
    }

    void testColumnCount() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([1, 2, 3]))
        assertEquals(1, model.columnCount)
        assertEquals(1, model.valueColumnCount)

        model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5]]))
        assertEquals(2, model.columnCount)
        assertEquals(2, model.valueColumnCount)
    }

    void testGetValueAt() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5]]), true)
        assertEquals(4, model.getValueAt(0, 1))
        assertEquals("", model.getValueAt(2, 1))

        model = new MultiDimensionalParameterTableModel(new TableMultiDimensionalParameter([[1, 2, 3], [4, 5]], ['Column', 'Column 2']))
        assertEquals('Column 2', model.getValueAt(0, 2))
        assertEquals(4, model.getValueAt(1, 2))
        assertEquals('', model.getValueAt(3, 2))

        model = new MultiDimensionalParameterTableModel(new MatrixMultiDimensionalParameter([[1, 2, 3], [4, 5]], ['Row'], ['Column']), true)
        assertEquals('', model.getValueAt(0, 1))
        assertEquals('Column', model.getValueAt(0, 2))
        assertEquals('Row', model.getValueAt(1, 1))
        assertEquals(4, model.getValueAt(1, 3))
        assertEquals('', model.getValueAt(3, 3))
    }

    void testGetValue() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5]]))
        assertEquals([[1, 2, 3], [4, 5]], model.currentValues())
        model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([1, 2, 3]))
        assertEquals([1, 2, 3], model.currentValues())
    }

    void testSetValueAt() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5]]), true)
        model.setValueAt(2.2, 0, 1)
        assertEquals(2.2, model.getValueAt(0, 1))

        model.setValueAt(6, 2, 1)
        assertEquals(6, model.getValueAt(2, 1))

        assertEquals 1, model.getValueAt(1, 0)
        model.setValueAt(0, 0, 0)
        assertEquals "", model.getValueAt(0, 0)

        // column index out of bound
        shouldFail(IndexOutOfBoundsException, {model.setValueAt(7, 1, 2)})

        // row index out of bound
        shouldFail(IndexOutOfBoundsException, {model.setValueAt(7, 3, 0)})

        model = new MultiDimensionalParameterTableModel(new MatrixMultiDimensionalParameter([[1, 2, 3], [4, 5]], ['Row'], ['Column']), true)
        model.setValueAt(2.2, 1, 1)
        assertEquals(2.2, model.getValueAt(1, 2))

        // column index out of bound
        shouldFail(IndexOutOfBoundsException, {model.setValueAt(7, 1, 3)})

        // row index out of bound
        shouldFail(IndexOutOfBoundsException, {model.setValueAt(7, 4, 1)})

        //test date handling
        model = new MultiDimensionalParameterTableModel(new TableMultiDimensionalParameter([[1, 2], [new DateTime(2009, 1, 1, 0, 0, 0, 0), new DateTime(2008, 1, 1, 0, 0, 0, 0)]], ['col', 'col2']), true)
        Object value = model.getValueAt(1, 2)
        assertTrue value instanceof Date
        GregorianCalendar cal = new GregorianCalendar()
        cal.time = value

        assertEquals 2009, cal.get(Calendar.YEAR)
        assertEquals 0, cal.get(Calendar.MONTH)
        assertEquals 1, cal.get(Calendar.DAY_OF_MONTH)

        cal.set(Calendar.DAY_OF_MONTH, 15)

        model.setValueAt(cal.time, 1, 2)
        List values = model.currentValues()[1]
        assertTrue values.every { it instanceof DateTime }
        assertEquals 15, values[0].dayOfMonth

    }

    void testIncreaseDimension() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]]))

        assertEquals 3, model.rowCount
        assertEquals 2, model.columnCount
        assertEquals 3, model.valueRowCount
        assertEquals 2, model.valueColumnCount

        model.dimension = new MultiDimensionalParameterDimension(3, 3)
        assertEquals 3, model.rowCount
        assertEquals 3, model.columnCount
        assertEquals 3, model.valueRowCount
        assertEquals 3, model.valueColumnCount

        model.dimension = new MultiDimensionalParameterDimension(3, 4)
        assertEquals 4, model.rowCount
        assertEquals 3, model.columnCount
        assertEquals 4, model.valueRowCount
        assertEquals 3, model.valueColumnCount

        model = new MultiDimensionalParameterTableModel(new MatrixMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]], ['Row'], ['Column']))

        assertEquals 4, model.rowCount
        assertEquals 3, model.columnCount
        assertEquals 3, model.valueRowCount
        assertEquals 2, model.valueColumnCount

        model.dimension = new MultiDimensionalParameterDimension(3, 3)
        assertEquals 4, model.rowCount
        assertEquals 4, model.columnCount
        assertEquals 3, model.valueRowCount
        assertEquals 3, model.valueColumnCount

        model.dimension = new MultiDimensionalParameterDimension(3, 4)
        assertEquals 5, model.rowCount
        assertEquals 4, model.columnCount
        assertEquals 4, model.valueRowCount
        assertEquals 3, model.valueColumnCount
    }

    void testDecreaseDimension() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]]))

        assertEquals 3, model.rowCount
        assertEquals 2, model.columnCount

        model.dimension = new MultiDimensionalParameterDimension(2, 2)
        assertEquals 2, model.rowCount
        assertEquals 2, model.columnCount

        model.dimension = new MultiDimensionalParameterDimension(2, 1)
        assertEquals 1, model.rowCount
        assertEquals 2, model.columnCount

        model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([[1, 2, 3], [4, 5]]))
        model.dimension = new MultiDimensionalParameterDimension(2, 2)
        assertEquals 2, model.rowCount
        assertEquals 2, model.columnCount
        assertEquals("second row not shrinked", 2, model.currentValues()[1].size())

        model = new MultiDimensionalParameterTableModel(new MatrixMultiDimensionalParameter([[1, 2, 3], [4, 5, 6]], ['Row'], ['Column']))

        assertEquals 4, model.rowCount
        assertEquals 3, model.columnCount
        assertEquals 3, model.valueRowCount
        assertEquals 2, model.valueColumnCount

        model.dimension = new MultiDimensionalParameterDimension(2, 2)
        assertEquals 3, model.rowCount
        assertEquals 3, model.columnCount
        assertEquals 2, model.valueRowCount
        assertEquals 2, model.valueColumnCount

        model.dimension = new MultiDimensionalParameterDimension(2, 1)
        assertEquals 2, model.rowCount
        assertEquals 3, model.columnCount
        assertEquals 1, model.valueRowCount
        assertEquals 2, model.valueColumnCount

        model = new MultiDimensionalParameterTableModel(new MatrixMultiDimensionalParameter([[1, 2, 3], [4, 5]], ['Row'], ['Column']))
        model.dimension = new MultiDimensionalParameterDimension(2, 2)
        assertEquals 3, model.rowCount
        assertEquals 3, model.columnCount
        assertEquals 2, model.valueRowCount
        assertEquals 2, model.valueColumnCount
        assertEquals("second row not shrinked", 2, model.currentValues()[1].size())


    }

    void testAddColumnAt() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new MatrixMultiDimensionalParameter([[1, 2, 3], [4, 5, 6], [7, 8, 9]], ['Row1', 'Row2', 'Row3'], ['Column1', 'Column2', 'Column3']), true)
        assertEquals 4, model.rowCount
        assertEquals 5, model.columnCount


        assertTrue model.getValueAt(0, 2) == "Column1"
        assertTrue model.getValueAt(0, 3) == "Column2"
        assertTrue model.getValueAt(1, 0) == 1
        assertTrue model.getValueAt(1, 1) == "Row1"
        assertTrue model.getValueAt(2, 0) == 2
        assertTrue model.getValueAt(3, 4) == 9



        model.addColumnAt 0
        assertEquals 4, model.rowCount
        assertEquals 6, model.columnCount

        assertTrue model.getValueAt(0, 2) == "Column1"
        assertTrue model.getValueAt(0, 3) == "Column1"
        assertTrue model.getValueAt(1, 2) == 0
        assertTrue model.getValueAt(1, 2) == 0
        assertTrue model.getValueAt(2, 2) == 0


        model = new MultiDimensionalParameterTableModel(new MatrixMultiDimensionalParameter([[1, 2, 3], [4, 5, 6], [7, 8, 9]], ['Row1', 'Row2', 'Row3'], ['Column1', 'Column2', 'Column3']), true)
        model.addColumnAt 1



        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.columnCount; j++) {
                println "assertTrue model.getValueAt($i, $j) == ${model.getValueAt(i, j)}"
            }
        }

        assertTrue model.getValueAt(0, 2) == "Column1"
        assertTrue model.getValueAt(0, 3) == "Column1"
        assertTrue model.getValueAt(0, 4) == "Column2"
        assertTrue model.getValueAt(1, 3) == 0
        assertTrue model.getValueAt(2, 3) == 0
        assertTrue model.getValueAt(3, 3) == 0

    }

    void testRemoveColumnAt() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new ComboBoxMatrixMultiDimensionalParameter([[1, 2, 3], [4, 5, 6], [7, 8, 9]], ['Row1', 'Row2', 'Row3'], ITestComponentMarker), true)

        assertEquals 4, model.rowCount
        assertEquals 5, model.columnCount

        assertEquals "Row1", model.getValueAt(0, 2)
        assertEquals "Row2", model.getValueAt(0, 3)
        assertEquals "Row3", model.getValueAt(0, 4)

        model.removeColumnAndRow 1

        assertEquals 3, model.rowCount
        assertEquals 4, model.columnCount

        assertEquals "Row1", model.getValueAt(0, 2)
        assertEquals "Row3", model.getValueAt(0, 3)


    }


    void testAddRowAt() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new MatrixMultiDimensionalParameter([[1, 2, 3], [4, 5, 6], [7, 8, 9]], ['Row1', 'Row2', 'Row3'], ['Column1', 'Column2', 'Column3']), true)

        assertEquals 4, model.rowCount
        assertEquals 5, model.columnCount

        assertTrue model.getValueAt(1, 1) == "Row1"
        assertTrue model.getValueAt(2, 1) == "Row2"
        assertTrue model.getValueAt(3, 1) == "Row3"

        model.addColumnAndRow 1

        assertEquals 5, model.rowCount
        assertEquals 6, model.columnCount

        assertTrue model.getValueAt(1, 1) == "Row1"
        assertTrue model.getValueAt(2, 1) == "Row1"
        assertTrue model.getValueAt(3, 1) == "Row2"
        assertTrue model.getValueAt(4, 1) == "Row3"
        assertTrue model.getValueAt(2, 2) == 0
        assertTrue model.getValueAt(2, 3) == 1
        assertTrue model.getValueAt(2, 4) == 0
    }

    void testRemoveRowAt() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new MatrixMultiDimensionalParameter([[1, 2, 3], [4, 5, 6], [7, 8, 9]], ['Row1', 'Row2', 'Row3'], ['Column1', 'Column2', 'Column3']), true)

        assertEquals 4, model.rowCount
        assertEquals 5, model.columnCount

        assertTrue model.getValueAt(1, 1) == "Row1"
        assertTrue model.getValueAt(2, 1) == "Row2"
        assertTrue model.getValueAt(3, 1) == "Row3"

        model.removeRowAt 1

        assertTrue model.getValueAt(1, 1) == "Row1"
        assertTrue model.getValueAt(2, 1) == "Row3"
    }

    void testIndexedTable() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([[11, 12, 13], [14, 15, 16]]), true)

        assertEquals 3, model.getRowCount()
        assertEquals 3, model.getValueRowCount()
        assertEquals 2 + 1, model.getColumnCount()
        assertEquals 2 + 1, model.getValueColumnCount()

        assertTrue model.getValueAt(0, 0) == ""
        assertTrue model.getValueAt(1, 0) == 1
        assertTrue model.getValueAt(2, 0) == 2

    }


    void testMoveColumn() {
        def list = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
        def cols = ['Column1', 'Column2', 'Column3']
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new TableMultiDimensionalParameter(list, cols))

        // row 0 titles
        assertEquals "Column1", model.getValueAt(0, 1)
        assertEquals "Column2", model.getValueAt(0, 2)
        assertEquals "Column3", model.getValueAt(0, 3)

        //row 1
        assertEquals 1, model.getValueAt(1, 1)
        assertEquals 4, model.getValueAt(1, 2)
        assertEquals 7, model.getValueAt(1, 3)

        model.moveColumnTo(0, 1)

        assertEquals "Column2", model.getValueAt(0, 1)
        assertEquals "Column1", model.getValueAt(0, 2)
        assertEquals "Column3", model.getValueAt(0, 3)

        //row 1
        assertEquals 4, model.getValueAt(1, 1)
        assertEquals 1, model.getValueAt(1, 2)
        assertEquals 7, model.getValueAt(1, 3)

    }

    void testMoveRow() {
        def list = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
        def cols = ['Column1', 'Column2', 'Column3']
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new TableMultiDimensionalParameter(list, cols))

        // row 0 titles
        assertEquals "Column1", model.getValueAt(0, 1)
        assertEquals "Column2", model.getValueAt(0, 2)
        assertEquals "Column3", model.getValueAt(0, 3)

        //row 1
        assertEquals 1, model.getValueAt(1, 1)
        assertEquals 4, model.getValueAt(1, 2)
        assertEquals 7, model.getValueAt(1, 3)

        //row 2
        assertEquals 2, model.getValueAt(2, 1)
        assertEquals 5, model.getValueAt(2, 2)
        assertEquals 8, model.getValueAt(2, 3)

        model.moveRowTo 0, 1

        // row 0 titles
        assertEquals "Column1", model.getValueAt(0, 1)
        assertEquals "Column2", model.getValueAt(0, 2)
        assertEquals "Column3", model.getValueAt(0, 3)

        //row 1
        assertEquals 2, model.getValueAt(1, 1)
        assertEquals 5, model.getValueAt(1, 2)
        assertEquals 8, model.getValueAt(1, 3)

        //row 2
        assertEquals 1, model.getValueAt(2, 1)
        assertEquals 4, model.getValueAt(2, 2)
        assertEquals 7, model.getValueAt(2, 3)
    }

    void testMoveColumnAndRow() {
        def list = [[1, 2, 3], [2, 1, 6], [3, 6, 1]]
        def cols = ['Column1', 'Column2', 'Column3']
        def rows = ['Row1', 'Row2', 'Row3']

        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new MatrixMultiDimensionalParameter(list, rows, cols))

        /*
             0   space    Column1  Column2   Column3
             1   Row1     1        2         3
             2   Row2     2        1         6
             3   Row3     3        6         1
         */
        assertEquals "Column1", model.getValueAt(0, 2)
        assertEquals "Column2", model.getValueAt(0, 3)
        assertEquals "Column3", model.getValueAt(0, 4)

        assertEquals "Row1", model.getValueAt(1, 1)
        assertEquals "Row2", model.getValueAt(2, 1)
        assertEquals "Row3", model.getValueAt(3, 1)

        //row 1
        assertEquals 1, model.getValueAt(1, 2)
        assertEquals 2, model.getValueAt(1, 3)
        assertEquals 3, model.getValueAt(1, 4)

        //row 2
        assertEquals 2, model.getValueAt(2, 2)
        assertEquals 1, model.getValueAt(2, 3)
        assertEquals 6, model.getValueAt(2, 4)

        //row 3
        assertEquals 3, model.getValueAt(3, 2)
        assertEquals 6, model.getValueAt(3, 3)
        assertEquals 1, model.getValueAt(3, 4)

        model.moveColumnAndRow 0, 1

        /*
        expected matrix

            0   space    Column2  Column1   Column3
            1   Row2     1        2         6
            2   Row1     2        1         3
            3   Row3     6        3         1
        */
        assertEquals "Column2", model.getValueAt(0, 2)
        assertEquals "Column1", model.getValueAt(0, 3)
        assertEquals "Column3", model.getValueAt(0, 4)

        assertEquals "Row2", model.getValueAt(1, 1)
        assertEquals "Row1", model.getValueAt(2, 1)
        assertEquals "Row3", model.getValueAt(3, 1)

        //row 1
        assertEquals 1, model.getValueAt(1, 2)
        assertEquals 2, model.getValueAt(1, 3)
        assertEquals 6, model.getValueAt(1, 4)

        // row 2
        assertEquals 2, model.getValueAt(2, 2)
        assertEquals 1, model.getValueAt(2, 3)
        assertEquals 3, model.getValueAt(2, 4)

        //row 3
        assertEquals 6, model.getValueAt(3, 2)
        assertEquals 3, model.getValueAt(3, 3)
        assertEquals 1, model.getValueAt(3, 4)

    }

    void testMoveColumnAndRowByComboBoxMatrixMultiDimensionalParameter() {
        def list = [[1, 2, 3], [2, 1, 6], [3, 6, 1]]
        def rows = ['Row1', 'Row2', 'Row3']

        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new ComboBoxMatrixMultiDimensionalParameter(list, rows, ITestComponentMarker))//MatrixMultiDimensionalParameter(list, rows, cols))

        /*
             0   space    Column1  Column2   Column3
             1   Row1     1        2         3
             2   Row2     2        1         6
             3   Row3     3        6         1
         */
        assertEquals "Row1", model.getValueAt(0, 2)
        assertEquals "Row2", model.getValueAt(0, 3)
        assertEquals "Row3", model.getValueAt(0, 4)

        assertEquals "Row1", model.getValueAt(1, 1)
        assertEquals "Row2", model.getValueAt(2, 1)
        assertEquals "Row3", model.getValueAt(3, 1)

        //row 1
        assertEquals 1, model.getValueAt(1, 2)
        assertEquals 2, model.getValueAt(1, 3)
        assertEquals 3, model.getValueAt(1, 4)

        //row 2
        assertEquals 2, model.getValueAt(2, 2)
        assertEquals 1, model.getValueAt(2, 3)
        assertEquals 6, model.getValueAt(2, 4)

        //row 3
        assertEquals 3, model.getValueAt(3, 2)
        assertEquals 6, model.getValueAt(3, 3)
        assertEquals 1, model.getValueAt(3, 4)

        model.moveColumnAndRow 0, 1

        /*
        expected matrix

            0   space    Column2  Column1   Column3
            1   Row2     1        2         6
            2   Row1     2        1         3
            3   Row3     6        3         1
        */

        assertEquals "Row2", model.getValueAt(0, 2)
        assertEquals "Row1", model.getValueAt(0, 3)
        assertEquals "Row3", model.getValueAt(0, 4)

        assertEquals "Row2", model.getValueAt(1, 1)
        assertEquals "Row1", model.getValueAt(2, 1)
        assertEquals "Row3", model.getValueAt(3, 1)

        //row 1
        assertEquals 1, model.getValueAt(1, 2)
        assertEquals 2, model.getValueAt(1, 3)
        assertEquals 6, model.getValueAt(1, 4)

        // row 2
        assertEquals 2, model.getValueAt(2, 2)
        assertEquals 1, model.getValueAt(2, 3)
        assertEquals 3, model.getValueAt(2, 4)

        //row 3
        assertEquals 6, model.getValueAt(3, 2)
        assertEquals 3, model.getValueAt(3, 3)
        assertEquals 1, model.getValueAt(3, 4)

    }




    void testIncreaseDimensionByTwo() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([1]))

        model.dimension = new MultiDimensionalParameterDimension(1, 3)
        assertEquals 3, model.rowCount
        assertEquals 1, model.columnCount

    }

    void testBulkChange() {
        MultiDimensionalParameterTableModel model = new MultiDimensionalParameterTableModel(new SimpleMultiDimensionalParameter([1]))

        model.dimension = new MultiDimensionalParameterDimension(2, 20)
        List events = []
        model.addTableModelListener([tableChanged: {TableModelEvent e ->
            events << e
        }] as ITableModelListener)

        int oldValue = model.getValueAt(1, 0)
        model.setValueAt(oldValue, 1, 0)
        assertEquals "no event expected when setting the same value", 0, events.size()

        oldValue = model.getValueAt(1, 0)
        model.setValueAt(oldValue + 1, 1, 0)
        assertEquals "changed value, event expected", 1, events.size()

        events.clear()
        model.startBulkChange()

        oldValue = model.getValueAt(1, 0)
        model.setValueAt(oldValue + 1, 1, 0)

        oldValue = model.getValueAt(3, 0)
        model.setValueAt(oldValue + 1, 3, 0)

        oldValue = model.getValueAt(5, 0)
        model.setValueAt(oldValue + 1, 5, 0)
        assertTrue "no event expected on bulkChange", events.empty

        model.stopBulkChange()
        assertEquals "bulk change finished, 1 event expected", 1, events.size()
        TableModelEvent event = events[0]
        assertTrue "row update event", event.isRowsUpdatedEvent()
        assertEquals "first row", 1, event.firstRow
        assertEquals "last row", 5, event.lastRow
    }


}