package org.pillarone.riskanalytics.application.ui.interaction

import com.ulcjava.testframework.standalone.AbstractStandaloneTestCase
import javax.swing.tree.TreePath
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.P1RATApplication
import org.pillarone.riskanalytics.core.fileimport.FileImportService
import com.ulcjava.testframework.operator.*

class ImportActionTests extends AbstractStandaloneTestCase {

    protected void setUp() {
        FileImportService.importModelsIfNeeded(["Application"])
        ModellingItemFactory.clear()
        super.setUp();
    }

    private def copyFileToUserHome(String file) {
        String userHome = System.properties["user.home"]
        String path = new File(".").getCanonicalPath()
        File importDir = new File(path, "src/java/models/application/")
        assertTrue "$path/src/java/models/application/ does not exist ", importDir.exists()
        File srcFile = new File(importDir, file)
        File destFile = new File(userHome, file)
        if (!destFile.exists()) {
            destFile.write srcFile.text
        }
        destFile.deleteOnExit()
    }

    protected Class getApplicationClass() {
        return P1RATApplication
    }

    void testDummy() {
        assertTrue true
    }

    void testImportParameter() {
        String importParameterFilename = "ApplicationParameters.groovy"
        copyFileToUserHome(importParameterFilename)

        ULCFrameOperator frame1 = new ULCFrameOperator("Risk Analytics")
        ULCTreeOperator tree1 = new ULCTreeOperator(frame1, new ComponentByNameChooser("selectionTree"))
        tree1.doExpandRow 0
        tree1.doExpandRow 1


        TreePath pathForImport = tree1.findPath(["Application", "Parameterization"] as String[])
        assertNotNull "path not found", pathForImport

        ULCPopupMenuOperator popUpMenu = tree1.callPopupOnPath(pathForImport)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Import")

        ULCFileChooserOperator fileChooser = new ULCFileChooserOperator()

        fileChooser.pathField.typeText "${System.properties["user.home"]}/$importParameterFilename"
        fileChooser.approve()

        TreePath path = tree1.findPath(["Application", "Parameterization", "ApplicationParameters"] as String[])

        assertNotNull "path to imported parameter not found", path

    }

    void testForceImportParameter() {
        String importParameterFilename = "ApplicationParameters.groovy"
        copyFileToUserHome(importParameterFilename)

        ULCFrameOperator frame1 = new ULCFrameOperator("Risk Analytics")
        ULCTreeOperator tree1 = new ULCTreeOperator(frame1, new ComponentByNameChooser("selectionTree"))
        tree1.doExpandRow 0
        tree1.doExpandRow 1


        TreePath pathForImport = tree1.findPath(["Application", "Parameterization"] as String[])
        assertNotNull "path not found", pathForImport

        ULCPopupMenuOperator popUpMenu = tree1.callPopupOnPath(pathForImport)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Import (force)")

        ULCFileChooserOperator fileChooser = new ULCFileChooserOperator()

        fileChooser.pathField.typeText "${System.properties["user.home"]}/$importParameterFilename"
        fileChooser.approve()

        TreePath path = tree1.findPath(["Application", "Parameterization", "ApplicationParameters"] as String[])
        assertNotNull "path to imported parameter not found", path

        //second import
        pathForImport = tree1.findPath(["Application", "Parameterization"] as String[])
        assertNotNull "path not found", pathForImport

        popUpMenu = tree1.callPopupOnPath(pathForImport)
        assertNotNull popUpMenu
        popUpMenu.pushMenu("Import (force)")

        fileChooser = new ULCFileChooserOperator()

        fileChooser.pathField.typeText "${System.properties["user.home"]}/$importParameterFilename"
        fileChooser.approve()

        pathForImport = tree1.findPath(["Application", "Parameterization"] as String[])
        assertNotNull "path not found", pathForImport

    }

    // TODO (Apr 16, 2009, msh): Test import of other items
}