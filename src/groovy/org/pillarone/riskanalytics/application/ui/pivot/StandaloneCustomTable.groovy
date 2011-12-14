package org.pillarone.riskanalytics.application.ui.pivot

import com.ulcjava.applicationframework.application.SingleFrameApplication
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCFrame
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.ResultNavigator
import org.pillarone.riskanalytics.application.ui.pivot.view.PivotView

class StandaloneCustomTable extends SingleFrameApplication {

    PivotView pivotView

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
        pivotView = new PivotView();
        return pivotView.content;
    }
}
