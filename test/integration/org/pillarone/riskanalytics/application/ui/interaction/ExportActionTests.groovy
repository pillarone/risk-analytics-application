package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import com.ulcjava.testframework.operator.*

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

    void testExportParameter() {
        String exportParameterFilename = "ExportTestParameters.groovy"

        ULCFrameOperator frame1 = new ULCFrameOperator("Risk Analytics")
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

        assertTrue "File not written", exportedFile.exists()
    }

    // TODO (Apr 16, 2009, msh): Test export of other items
}