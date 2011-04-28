package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.shared.ErrorCodes
import java.text.SimpleDateFormat
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.core.parameter.DateParameter
import org.pillarone.riskanalytics.core.parameter.DoubleParameter
import org.pillarone.riskanalytics.core.parameter.IntegerParameter
import org.pillarone.riskanalytics.core.parameter.Parameter
import com.ulcjava.base.application.datatype.*
import org.pillarone.ulc.server.ULCFlexibleDateDataType

public class DataTypeFactory {

    static IDataType getDataType(def value, boolean editMode) {
        return null
    }

    static IDataType getDataType(Parameter value, boolean editMode) {
        IDataType dataType = null
        switch (value.persistedClass()) {
            case IntegerParameter: dataType = getDataType(0 as int, editMode)
                break

            case DoubleParameter: dataType = getDataType(0 as double, editMode)
                break
            case DateParameter: dataType = getDataType(new Date(), editMode)
                break
        }
        return dataType
    }


    static IDataType getDataType(BigDecimal value, boolean editMode) {
        return getDataType(value as Double, editMode)
    }

    static IDataType getDataType(Date value, boolean editMode) {
        return getDateDataType()
    }

    static IDataType getDataType(Double value, boolean editMode) {
        if (editMode) {
            return getDoubleDataTypeForEdit()
        }
        return getDoubleDataTypeForNonEdit()
    }

    static IDataType getDataType(Integer value, boolean editMode) {
        if (editMode) {
            return getIntegerDataTypeForEdit()
        }
        return getIntegerDataTypeForNonEdit()

    }

    static IDataType getDateDataType() {
        IDataType dateDataType = UserContext.getAttribute("dateDataType")

        if (dateDataType == null) { //TODO: crashes when used with error manager in MDPs
            dateDataType = new ULCFlexibleDateDataType(/*getErrorManager(),*/ DateFormatUtils.getInputDateFormats(), SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, UIUtils.getClientLocale()).toPattern())
            UserContext.setAttribute("dateDataType", dateDataType)
        }

        return dateDataType

    }

    static IDataType getIntegerDataTypeForEdit() {
        IDataType integerDataTypeForEdit = UserContext.getAttribute("integerDataTypeForEdit")

        if (integerDataTypeForEdit == null) {
            integerDataTypeForEdit = new ULCNumberDataType(getErrorManager(), UIUtils.clientLocale)
            integerDataTypeForEdit.classType = Integer
            integerDataTypeForEdit.groupingUsed = false
            UserContext.setAttribute("integerDataTypeForEdit", integerDataTypeForEdit)
        }

        return integerDataTypeForEdit

    }

    static IDataType getIntegerDataTypeForNonEdit() {
        IDataType integerDataTypeForNonEdit = UserContext.getAttribute("integerDataTypeForNonEdit")

        if (integerDataTypeForNonEdit == null) {
            integerDataTypeForNonEdit = new ULCNumberDataType(getErrorManager(), UIUtils.clientLocale)
            integerDataTypeForNonEdit.classType = Integer
            integerDataTypeForNonEdit.groupingUsed = true
            UserContext.setAttribute("integerDataTypeForNonEdit", integerDataTypeForNonEdit)
        }

        return integerDataTypeForNonEdit
    }

    static IDataType getDoubleDataTypeForEdit() {
        IDataType floatingPointDataTypeForEdit = UserContext.getAttribute("floatingPointDataTypeForEdit")
        if (floatingPointDataTypeForEdit == null) {
            floatingPointDataTypeForEdit = new ULCNumberDataType(getErrorManager(), UIUtils.clientLocale)
            floatingPointDataTypeForEdit.classType = Double
            floatingPointDataTypeForEdit.minFractionDigits = 0
            floatingPointDataTypeForEdit.maxFractionDigits = 20
            floatingPointDataTypeForEdit.groupingUsed = false
            UserContext.setAttribute("floatingPointDataTypeForEdit", floatingPointDataTypeForEdit)
        }
        return floatingPointDataTypeForEdit
    }

    static IDataType getDoubleDataTypeForNonEdit() {
        IDataType floatingPointDataTypeForNonEdit = UserContext.getAttribute("floatingPointDataTypeForNonEdit")
        if (floatingPointDataTypeForNonEdit == null) {
            floatingPointDataTypeForNonEdit = new ULCNumberDataType(getErrorManager(), UIUtils.clientLocale)
            floatingPointDataTypeForNonEdit.minFractionDigits = 0
            floatingPointDataTypeForNonEdit.maxFractionDigits = 20
            floatingPointDataTypeForNonEdit.groupingUsed = true
            UserContext.setAttribute("floatingPointDataTypeForNonEdit", floatingPointDataTypeForNonEdit)
        }
        return floatingPointDataTypeForNonEdit
    }

    static IDataType getDoubleDataType() {
        ULCNumberDataType dataType = new ULCNumberDataType(UIUtils.clientLocale)
        dataType.classType = Double
        dataType.minFractionDigits = 1
        dataType.maxFractionDigits = 2
        return dataType
    }

    static ULCNumberDataType getNumberDataType() {
        return new ULCNumberDataType(UIUtils.clientLocale)
    }


    private static ULCAbstractErrorManager getErrorManager() {
        ULCDefaultErrorManager errorManager = UserContext.getAttribute("errorManager")
        if (errorManager == null) {
            def messages = [:]
            messages[ErrorCodes.ERROR_CODE_BAD_INTEGER_FORMAT] = "wrong number format"
            messages[ErrorCodes.ERROR_CODE_BAD_DOUBLE_FORMAT] = "wrong number format"
            messages[ErrorCodes.ERROR_CODE_BAD_DATE_FORMAT] = "wrong date format"
            errorManager = new ULCDefaultErrorManager(messages)
            UserContext.setAttribute("errorManager", errorManager)
        }
        return errorManager
    }

}
