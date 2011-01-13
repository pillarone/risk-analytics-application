package org.pillarone.riskanalytics.application.ui.util.server

import com.ulcjava.base.application.datatype.IDataType
import com.ulcjava.base.application.datatype.ULCAbstractDataType
import com.ulcjava.base.application.datatype.ULCAbstractErrorManager


class ULCFlexibleDateDataType extends ULCAbstractDataType implements IDataType {

    private List<String> possibleFormats
    private String displayFormat

    ULCFlexibleDateDataType(List possibleFormats, String displayFormat) {
        this.possibleFormats = possibleFormats
        this.displayFormat = displayFormat
    }

    ULCFlexibleDateDataType(ULCAbstractErrorManager ulcAbstractErrorManager, List<String> possibleFormats, String displayFormat) {
        super(ulcAbstractErrorManager)
        this.possibleFormats = possibleFormats
        this.displayFormat = displayFormat
    }

    @Override
    protected String typeString() {
        return "org.pillarone.riskanalytics.application.client.UIFlexibleDateDataType"
    }

    @Override
    protected void uploadStateUI() {
        super.uploadStateUI()
        setStateUI("formats", possibleFormats)
        setStateUI("displayFormat", displayFormat)
    }


}
