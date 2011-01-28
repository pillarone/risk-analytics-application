package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import com.ulcjava.testframework.operator.ULCDialogOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.operator.ULCMenuItemOperator
import com.ulcjava.testframework.operator.ULCPopupMenuOperator
import com.ulcjava.testframework.operator.ULCTabbedPaneOperator
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.operator.ULCTextAreaOperator
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.awt.event.InputEvent
import java.text.DecimalFormat
import models.core.CoreModel

import org.pillarone.riskanalytics.core.ModelStructureDAO
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.output.DBCleanUpService
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.fileimport.ModelStructureImportService
import org.pillarone.riskanalytics.application.ui.util.UIUtils

class ParameterViewTests extends AbstractSimpleFunctionalTest {

    Parameterization parameterization

    protected void doStart() {
        new DBCleanUpService().cleanUp()
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreParameters'])
        new ModelStructureImportService().compareFilesAndWriteToDB(['CoreStructure'])

        ModellingItemFactory.clear()

        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"
        CoreModel model = new CoreModel()
        model.initComponents()

        def dao = ParameterizationDAO.findByName('CoreParameters')
        parameterization = ModellingItemFactory.getParameterization(dao)
        parameterization.load()
        dao = ModelStructureDAO.findByModelClassName(model.class.name)
        ModelStructure structure = ModellingItemFactory.getModelStructure(dao)
        structure.load()
        ParameterViewModel parameterViewModel = new ParameterViewModel(model, parameterization, structure)
        frame.setContentPane(new ParameterView(parameterViewModel).content)
        ULCClipboard.install()
        UIUtils.setRootPane(frame)
        frame.visible = true
    }

    void testPropertiesView() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTabbedPaneOperator tabbedPaneOperator = new ULCTabbedPaneOperator(frameOperator, new ComponentByNameChooser('tabbedPane'))
        tabbedPaneOperator.selectPage(1)
        ULCTextAreaOperator textAreaOperator = new ULCTextAreaOperator(frameOperator, new ComponentByNameChooser('textArea'))
        textAreaOperator.typeText('Comment')
        tabbedPaneOperator.selectPage(0)
        assertEquals 'Comment', parameterization.comment
    }

// TODO (msh): check with dani
/*
    void testCollapseExpandTree() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTableTreeOperator tableTreeOperator = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeRowHeader"))
        assertTrue(tableTreeOperator.isExpanded(0))
        tableTreeOperator.doCollapseRow 0
        assertTrue(tableTreeOperator.isCollapsed(0))
        tableTreeOperator.doExpandRow 0
        assertTrue(tableTreeOperator.isExpanded(0))

        tableTreeOperator.selectCell(1, 0)
        tableTreeOperator.pushKey(KeyEvent.VK_LEFT)
        assertTrue("left key should collaps node", tableTreeOperator.isCollapsed(1))

        tableTreeOperator.pushKey(KeyEvent.VK_RIGHT)
        assertTrue("right key should expand node", tableTreeOperator.isExpanded(1))

        tableTreeOperator.selectCell(2, 0)
        tableTreeOperator.pushKey(KeyEvent.VK_RIGHT)
        assertTrue("key listener did not manipulate tree state", tableTreeOperator.isExpanded(2))

        tableTreeOperator.pushKey(KeyEvent.VK_LEFT)
        assertTrue("key listener did not manipulate tree state", tableTreeOperator.isCollapsed(2))
    }
*/


    void testExpandAll() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTableTreeOperator componentTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeRowHeader"))

        componentTree.doCollapseRow(1)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON1_MASK)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON3_MASK)

        ULCPopupMenuOperator popupMenuOperator = new ULCPopupMenuOperator(frameOperator, new ComponentByNameChooser("popup.expand"))
        ULCMenuItemOperator expandItem = new ULCMenuItemOperator(popupMenuOperator, "Expand node")
        expandItem.clickMouse()
        assertTrue componentTree.isExpanded(1)

        ULCTableTreeOperator valueTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeContent"))
        assertTrue valueTree.isExpanded(1)
    }

    void testPasteToTree() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTableTreeOperator componentTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeRowHeader"))

        componentTree.doCollapseRow(1)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON1_MASK)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON3_MASK)

        ULCPopupMenuOperator popupMenuOperator = new ULCPopupMenuOperator(frameOperator, new ComponentByNameChooser("popup.expand"))
        ULCMenuItemOperator expandItem = new ULCMenuItemOperator(popupMenuOperator, "Expand node")
        expandItem.clickMouse()
        assertTrue componentTree.isExpanded(1)
        ULCTableTreeOperator valueTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeContent"))

        valueTree.selectCell(2, 0)
        // select a cell above the comboBox, as selection of the ComboBox would open it
        valueTree.pushKey(KeyEvent.VK_DOWN)
        assertEquals "selected row", 3, valueTree.getSelectedRow()
        assertEquals "TYPE0", valueTree.getValueAt(valueTree.selectedRow, valueTree.selectedColumn)

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        DecimalFormat format = new DecimalFormat()
        String pasteData = "TYPE0\n${format.format(1.5d)}\n${format.format(1.6d)}".toString()
        clipboard.setContents(new StringSelection(pasteData), null)

        valueTree.pushKey(KeyEvent.VK_V, KeyEvent.CTRL_MASK)

        assertEquals new Double(1.6), valueTree.getValueAt(valueTree.selectedRow + 2, valueTree.selectedColumn)

    }

    void testPasteToTreeWithStructureChange() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTableTreeOperator componentTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeRowHeader"))

        componentTree.doCollapseRow(1)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON1_MASK)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON3_MASK)

        ULCPopupMenuOperator popupMenuOperator = new ULCPopupMenuOperator(frameOperator, new ComponentByNameChooser("popup.expand"))
        ULCMenuItemOperator expandItem = new ULCMenuItemOperator(popupMenuOperator, "Expand node")
        expandItem.clickMouse()
        assertTrue componentTree.isExpanded(1)
        ULCTableTreeOperator valueTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeContent"))

        valueTree.selectCell(2, 0)
        // select a cell above the comboBox, as selection of the ComboBox would open it
        valueTree.pushKey(KeyEvent.VK_DOWN)
        assertEquals "TYPE0", valueTree.getValueAt(valueTree.selectedRow, valueTree.selectedColumn)

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        String pasteData = "TYPE2\n1.5\n\2.5\n3.5\n"
        clipboard.setContents(new StringSelection(pasteData), null)

        valueTree.pushKey(KeyEvent.VK_V, KeyEvent.CTRL_MASK)

        ULCDialogOperator alertOperator = new ULCDialogOperator("Notification")

    }

    void testPasteToTreeWithInvalidDataFormat() {
        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTableTreeOperator componentTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeRowHeader"))

        componentTree.doCollapseRow(1)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON1_MASK)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON3_MASK)

        ULCPopupMenuOperator popupMenuOperator = new ULCPopupMenuOperator(frameOperator, new ComponentByNameChooser("popup.expand"))
        ULCMenuItemOperator expandItem = new ULCMenuItemOperator(popupMenuOperator, "Expand node")
        expandItem.clickMouse()
        assertTrue componentTree.isExpanded(1)
        ULCTableTreeOperator valueTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeContent"))

        valueTree.selectCell(2, 0)
        // select a cell above the comboBox, as selection of the ComboBox would open it
        // do not move down to produce an error because of wrong data format
//        valueTree.pushKey(KeyEvent.VK_DOWN)

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        String pasteData = "TYPE0\n1.5\n1.6"
        clipboard.setContents(new StringSelection(pasteData), null)

        valueTree.pushKey(KeyEvent.VK_V, KeyEvent.CTRL_MASK)


    }

    public void testExpandWithFilter_PMO_240() {

        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTableTreeOperator componentTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeRowHeader"))

        ULCComboBoxOperator filter = new ULCComboBoxOperator(frameOperator, new ComponentByNameChooser("filter"))
        filter.selectItem 2

        componentTree.clickOnCell(0, 0, 1, InputEvent.BUTTON1_MASK)
        componentTree.clickOnCell(0, 0, 1, InputEvent.BUTTON3_MASK)

        ULCPopupMenuOperator popupMenuOperator = new ULCPopupMenuOperator(frameOperator, new ComponentByNameChooser("popup.expand"))
        ULCMenuItemOperator expandItem = new ULCMenuItemOperator(popupMenuOperator, "Expand node")
        expandItem.clickMouse()
        assertTrue componentTree.isExpanded(0)

        assertEquals "subcomponent", componentTree.getValueAt(2, 0)

        componentTree.doCollapseRow 3
        assertTrue componentTree.isCollapsed(3)
    }

    public void testExpandAfterCollapseSelectionChange_PMO_240() {

        ULCFrameOperator frameOperator = new ULCFrameOperator(new ComponentByNameChooser("test"))
        ULCTableTreeOperator componentTree = new ULCTableTreeOperator(frameOperator, new ComponentByNameChooser("parameterTreeRowHeader"))

        componentTree.doCollapseRow 3
        componentTree.doCollapseRow 1

        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON1_MASK)
        componentTree.clickOnCell(1, 0, 1, InputEvent.BUTTON3_MASK)

        ULCPopupMenuOperator popupMenuOperator = new ULCPopupMenuOperator(frameOperator, new ComponentByNameChooser("popup.expand"))
        ULCMenuItemOperator expandItem = new ULCMenuItemOperator(popupMenuOperator, "Expand node")
        expandItem.clickMouse()
        assertTrue componentTree.isExpanded(1)

        componentTree.doCollapseRow 1

        componentTree.clickOnCell(2, 0, 1, InputEvent.BUTTON1_MASK)
        componentTree.clickOnCell(2, 0, 1, InputEvent.BUTTON3_MASK)
        popupMenuOperator = new ULCPopupMenuOperator(frameOperator, new ComponentByNameChooser("popup.expand"))
        expandItem = new ULCMenuItemOperator(popupMenuOperator, "Expand node")
        expandItem.clickMouse()

        assertTrue "row 4 not expanded", componentTree.isExpanded(2)
        assertTrue "row 3 expanded", componentTree.isCollapsed(1)
    }
}
