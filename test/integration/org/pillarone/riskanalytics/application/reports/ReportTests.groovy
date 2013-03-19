package org.pillarone.riskanalytics.application.reports

import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationActionsPane
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import com.ulcjava.testframework.operator.*

public class ReportTests extends AbstractStandaloneTestCase {

    int simulationNumber = 0
    ULCFrameOperator frame
    ULCTreeOperator tree

    protected void setUp() {
        FileImportService.importModelsIfNeeded(['CapitalEagle'])
        ModellingItemFactory.clear()
        super.setUp();
    }

    protected Class getApplicationClass() {
        P1RATApplication
    }

    static boolean isRunningOnHudson() {
        System.properties.'user.dir'.contains('hudson')
    }

    public void testReports() {
/*        if (isRunningOnHudson()) return
        ReportFactory.testMode = true

        frame = new ULCFrameOperator("Risk Analytics")
        assertNotNull frame

        tree = new ULCTreeOperator(frame, new ComponentByNameChooser("selectionTree"))
        assertNotNull tree

        //1 RI Management
        generateReport('CapitalEagle', 'Reports (DB)', 'One Reinsurance Program', 'Management Summary')
        //1 RI Actuary
        generateReport('CapitalEagle', 'Reports (DB)', 'One Reinsurance Program', 'Actuary Summary')

        //4 RI Management
        generateReport('CapitalEagle', 'Reports (DB)', 'Four Reinsurance Programs', 'Management Summary')
        ReportFactory.testMode = false*/
    }

    public void generateReport(String modelName, String templateName, String parameterName, String reportName) {
        int currentSimulationNumber = simulationNumber++
        simulate(modelName, templateName, parameterName, "testSim" + currentSimulationNumber, tree)

        TreePath path = tree.findPath([modelName, "Results", "testSim" + currentSimulationNumber] as String[], [2, 0, 0] as int[])
        assertNotNull "path not found", path

        ULCPopupMenuOperator popUpMenu = tree.callPopupOnPath(path)
        assertNotNull popUpMenu

        popUpMenu.pushMenu(["Reports", "Generate Report " + reportName] as String[])
        assertTrue ReportFactory.generationSuccessful
    }

    /**
     * Is static to enable reuse from other TestCasess
     */
    static void simulate(String modelName, String templateName, String parameterName, String simulationNameString, ULCTreeOperator tree, ULCFrameOperator frame) {
        TreePath path = tree.findPath([modelName, "Parameterization", parameterName] as String[])
        assertNotNull "path not found", path

        ULCPopupMenuOperator popUpMenu = tree.callPopupOnPath(path)
        assertNotNull popUpMenu

        popUpMenu.pushMenu(["Run Simulation"] as String[])

        ULCButtonOperator runButton = new ULCButtonOperator(frame, "${SimulationActionsPane.getSimpleName()}.run")
        assertNotNull runButton
        ULCButtonOperator stopButton = new ULCButtonOperator(frame, "stop")
        assertNotNull stopButton
        ULCButtonOperator openResults = new ULCButtonOperator(frame, "open")
        assertNotNull openResults
        ULCTextFieldOperator iterationCount = new ULCTextFieldOperator(frame, new ComponentByNameChooser("iterationCount"))
        assertNotNull iterationCount

        ULCTextFieldOperator simulationName = new ULCTextFieldOperator(frame, new ComponentByNameChooser("simulationName"))
        assertNotNull simulationName
        ULCComboBoxOperator parameter = new ULCComboBoxOperator(frame, new ComponentByNameChooser("parametrizationNamesComboBox"))
        assertNotNull parameter
        ULCComboBoxOperator template = new ULCComboBoxOperator(frame, new ComponentByNameChooser("templateNamesComboBox"))
        assertNotNull template
        ULCComboBoxOperator model = new ULCComboBoxOperator(frame, new ComponentByNameChooser("modelComboBox"))
        assertNotNull model
        ULCComboBoxOperator output = new ULCComboBoxOperator(frame, new ComponentByNameChooser("outputStrategyComboBox"))
        assertNotNull output
        ULCProgressBarOperator progressBar = new ULCProgressBarOperator(frame, new ComponentByNameChooser('progressBar'))
        assertNotNull progressBar

        simulationName.enterText(simulationNameString)
        iterationCount.enterText("2")
        assertTrue "run button should be enabled by iterationCount", runButton.enabled
        assertFalse "stop button should be disabled by iterationCount", stopButton.enabled
        template.selectItem(templateName) //'Reports (DB)'
        parameter.selectItem(parameterName) // 'One Reinsurance Program'
        output.selectItem("Database: Normal Insert")


        runButton.getFocus() // seems that the clickMouse has no focus on the button
        runButton.clickMouse()
        while (!runButton.enabled) {
            sleep 500
        }
    }


}
