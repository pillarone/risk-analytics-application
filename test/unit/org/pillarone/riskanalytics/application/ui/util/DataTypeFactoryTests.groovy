package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.application.datatype.ULCNumberDataType
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum

class DataTypeFactoryTests extends GroovyTestCase {

    void testNoDataType() {
        assertNull "No DataType for Strings", DataTypeFactory.getDataType("String", false)
        assertNull "No DataType for enum", DataTypeFactory.getDataType(ExampleEnum.FIRST_VALUE, false)
    }

    void testDoubleDataTypeForEdit() {
        Double value = new Double(1.2)
        ULCNumberDataType dataType = DataTypeFactory.getDataType(value, true)
        assertEquals Double.simpleName, dataType.classType
        assertFalse "grouping used", dataType.groupingUsed
        assertEquals "minFractionDigits", 0, dataType.minFractionDigits
        assertEquals "maxFractionDigits", 20, dataType.maxFractionDigits

        assertSame dataType, DataTypeFactory.getDataType(value, true)
        assertSame dataType, DataTypeFactory.getDataType(1.1d, true)
    }

    void testDoubleDataTypeForNonEdit() {
        Double value = new Double(1.2)
        ULCNumberDataType dataType = DataTypeFactory.getDataType(value, false)
        assertEquals Double.simpleName, dataType.classType
        assertTrue "grouping not used", dataType.groupingUsed
        assertEquals "minFractionDigits", 0, dataType.minFractionDigits
        assertEquals "maxFractionDigits", 20, dataType.maxFractionDigits

        assertSame dataType, DataTypeFactory.getDataType(value, false)
        assertSame dataType, DataTypeFactory.getDataType(1.1d, false)
        assertSame dataType, DataTypeFactory.getDataType(1.1, false)
        assertNotSame dataType, DataTypeFactory.getDataType(1.1d, true)
    }

    void testIntegerDataTypeForEdit() {
        Integer value = new Integer(2)
        ULCNumberDataType dataType = DataTypeFactory.getDataType(value, true)
        assertEquals Integer.simpleName, dataType.classType
        assertFalse "grouping used", dataType.groupingUsed

        assertSame dataType, DataTypeFactory.getDataType(value, true)
    }

    void testIntegerDataTypeForNonEdit() {
        Integer value = new Integer(2)
        ULCNumberDataType dataType = DataTypeFactory.getDataType(value, false)
        assertEquals Integer.simpleName, dataType.classType
        assertTrue "grouping not used", dataType.groupingUsed

        assertSame dataType, DataTypeFactory.getDataType(value, false)
        assertNotSame dataType, DataTypeFactory.getDataType(value, true)
    }
}