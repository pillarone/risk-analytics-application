package org.pillarone.riskanalytics.application.reports

import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest

public class ResultDeletionTests extends AbstractSimpleFunctionalTest {

    String newParameterizationName
    final String MODEL_NAME = "StructureTest"

    protected void doStart() {
        
    }

//    public void start() {
//        LocaleResources.setTestMode()
//        FileImportService.importModelsIfNeeded([MODEL_NAME])
//        ModellingItemFactory.clear()
//
//        setValidParameterization()
//
//        ULCFrame frame1 = new P1RATFrameViewFactory().create()
//        frame1.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
//        frame1.title = "first"
//        frame1.setLocation 50, 50
//        frame1.visible = true
//    }
//
//    private def setValidParameterization() {
//        ParameterizationDAO.withTransaction {status ->
//            ParameterizationDAO parameterizationDAO = ParameterizationDAO.findByName('StructureTestParameters')
//            parameterizationDAO.valid = true
//            parameterizationDAO.save()
//        }
//    }
//
//    public void stop() {
//        LocaleResources.clearTestMode()
//    }
//
//    void testDeleteResult() {
//        //todo fix a test: a tabbedPane added for batch creation, therefore testDeleteResult doesn't work
//        //
//        ULCFrameOperator frame = new ULCFrameOperator("first")
//        ULCTreeOperator tree = new ULCTreeOperator(frame, new ComponentByNameChooser("selectionTree"))
//
//        ReportTests.simulate(MODEL_NAME, "StructureTestResultConfiguration", "StructureTestParameters", "testSim", tree, frame)
//
//        // after simulation, there should be a result in the tree
//        TreePath resultPath = tree.findPath([MODEL_NAME, "Results", "testSim"] as String[])
//        assertNotNull "resultPath not found", resultPath
//        // remove result
//        ULCPopupMenuOperator popUpMenu = tree.callPopupOnPath(resultPath)
//        assertNotNull popUpMenu
//        popUpMenu.pushMenu(["Delete"] as String[])
//
//        assertResultNotDisplayed(tree)
//
//        //call refresh
//        ULCButtonOperator refreshButton = new ULCButtonOperator(frame, new ComponentByNameChooser("refresh"))
//        refreshButton.clickMouse()
//
//        assertResultNotDisplayed(tree)
//    }
//
//    private def assertResultNotDisplayed(ULCTreeOperator tree) {
//        def resultPath = tree.findPath([MODEL_NAME, "Results"] as String[])//, [2, 0] as int[])
//        assertNotNull "resultPath found", resultPath
//        assertEquals "result should be deleted ", 0, tree.getChildCount(resultPath)
//    }
//
//    private def assertResultDisplayed(ULCTreeOperator tree) {
//        def resultPath = tree.findPath([MODEL_NAME, "Results"] as String[])//, [2, 0] as int[])
//        assertNotNull "resultPath found", resultPath
//        assertEquals "result should be displayed ", 1, tree.getChildCount(resultPath)
//    }

}
