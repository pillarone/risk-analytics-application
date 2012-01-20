package org.pillarone.riskanalytics.application.ui.customtable

import com.ulcjava.applicationframework.application.SingleFrameApplication
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCFrame

import org.pillarone.riskanalytics.application.ui.customtable.view.CustomTableView

class StandaloneCustomTable extends SingleFrameApplication {

    CustomTableView customTableView

    @Override
    protected ULCComponent createStartupMainContent() {
        return getContentView()
    }

    @Override
    protected void initFrame(ULCFrame frame) {
        super.initFrame(frame)
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.setSize(1000, 750)
        frame.setExtendedState(ULCFrame.NORMAL)
        frame.toFront()
        frame.locationRelativeTo = null
    }

    /**
     * Sets up the content of the main application window.
     * @return the component with the content of the main application window.
     */
    protected ULCComponent getContentView() {
        customTableView = new CustomTableView(null);
        return customTableView.content;
    }
}
