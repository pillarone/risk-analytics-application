package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.ui.main.action.ExportAction
import org.pillarone.riskanalytics.core.fileimport.FileImportService

class ExportActionTests extends AbstractStandaloneTestCase {

    File exportedFile

    protected void setUp() {
        FileImportService.importModelsIfNeeded(["Application"])
        ModellingItemFactory.clear()
        super.setUp();
    }

    protected void tearDown() {
        super.tearDown();
        exportedFile?.deleteOnExit()
    }

    protected Class getApplicationClass() {
        return P1RATApplication
    }

    void testValidateFileName() {
        String sep = File.separator
        String filename1 = "c:" + sep + "test" + sep + "f1.groovy"
        String filename2 = "c:" + sep + "test" + sep + "folder1" + sep + "test*? 6&98().groovy"
        String filename3 = "test1234+*%&end.groovy"
        String filename4 = sep + "test" + sep + "f1.groovy"
        assertEquals filename1, ExportAction.validateFileName(filename1)
        assertEquals "c:" + sep + "test" + sep + "folder1" + sep + "test698.groovy", ExportAction.validateFileName(filename2)
        assertEquals "test1234end.groovy", ExportAction.validateFileName(filename3)
        assertEquals filename4, ExportAction.validateFileName(filename4)

    }

    void testExportParameter() {
        String exportParameterFilename = "ExportTestParameters.groovy"

        /*ULCFrameOperator frame1 = new ULCFrameOperator("Risk Analytics")
        ULCTreeOperator tree1 = new ULCTreeOperator(frame1, new ComponentByNameChooser("selectionTree"))
        tree1.doExpandRow 0
        tree1.doExpandRow 1


        TreePath pathForExport = tree1.findPath(["Application", "Parameterization", "ApplicationParameters"] as String[])
        assertNotNull "path not found", pathForExport

        ULCPopupMenuOperator popUpMenu = tree1.callPopupOnPath(pathForExport)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Export")

        ULCFileChooserOperator fileChooser = new ULCFileChooserOperator()
        File directory = fileChooser.currentDirectory
        fileChooser.pathField.typeText exportParameterFilename
        fileChooser.approve()

        exportedFile = new File(directory, exportParameterFilename)

        assertTrue "File not written", exportedFile.exists()*/
    }

    // TODO (Apr 16, 2009, msh): Test export of other items
}