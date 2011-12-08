package org.pillarone.riskanalytics.application.ui.resultnavigator

import com.ulcjava.applicationframework.application.SingleFrameApplication
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.ResultNavigator
import com.ulcjava.applicationframework.application.ToolBarFactory
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.SimulationRunSelectionDialog
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.core.output.SimulationRun
import com.ulcjava.applicationframework.application.Action

class StandaloneResultNavigator extends SingleFrameApplication {

    ResultNavigator contentView
    SimulationRunSelectionDialog simulationRunSelection

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
        ULCToolBar toolBar = new ToolBarFactory(getContext().getActionMap(this)).createToolBar("loadSimulationRunAction")
        toolBar.add(ULCFiller.createHorizontalGlue())
        ULCComponent icon = new ULCLabel()
        icon.setName("logo.Label")
        toolBar.add(icon)
        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 5))
        getMainView().setToolBar(toolBar);
        contentView = new ResultNavigator(getContext());
        return contentView.getContentView();
    }

    @Action
    public void loadSimulationRunAction() {
        if (!simulationRunSelection) {
            simulationRunSelection = new SimulationRunSelectionDialog(UlcUtilities.getWindowAncestor(contentView.getContentView()))
            IActionListener newModelListener = new IActionListener() {
                public void actionPerformed(ActionEvent event) {
                    inspect(simulationRunSelection.getSelectRun());
                    simulationRunSelection.setVisible(false);
                }
            }
            simulationRunSelection.addSaveActionListener(newModelListener);
        }
        simulationRunSelection.setVisible(true);
    }

    public void inspect(SimulationRun run) {
        contentView.loadSimulationRun(run)
    }
}
