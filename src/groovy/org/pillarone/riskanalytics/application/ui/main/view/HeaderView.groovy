package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.Insets
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.shared.IDefaults
import grails.util.Holders
import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ResultConfigurationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationSettingsUIItem
import org.pillarone.riskanalytics.application.ui.settings.model.UserSettingsViewModel
import org.pillarone.riskanalytics.application.ui.settings.view.UserSettingsViewDialog
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.security.LogoutService
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

/**
 *  UI for the header of RiskAnalytics to show File menus and action buttons
 * for saving, refreshing and running a simulation
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class HeaderView extends AbstractView {
    private static final Log LOG = LogFactory.getLog(HeaderView.class)

    ULCBoxPane content

    ULCToolBar toolBar
    ULCToolBar rightToolBar
    //actions
    SaveAction saveAction
    SimulationAction runAction
    RefreshAction refreshAction
    ExportAllAction exportAllNewestVersionAction
    ExportAllAction exportAllAction
    ImportAllAction importAllAction
    //menus
    ULCMenuBar menuBar
    ULCMenu windowMenu
    ULCMenu fileMenu
    ULCMenuItem saveAllItem
    ULCMenuItem refreshItem
    SimulationRunMenuItem runItem
    ULCMenuItem exportAllItemsNewstVersion
    ULCMenuItem exportAllItems
    ULCMenuItem importAllItems
    ULCMenuItem saveItem
    ULCMenuItem exitItem
    ULCMenu helpMenu
    ULCMenuItem aboutItem
    ULCMenuItem traceLogItem
    ULCMenuItem settingsMenu
    ULCButtonGroup windowMenuItemGroup
    ULCButtonGroup extensionMenuItemGroup
    //buttons
    ULCButton saveButton
    ULCButton refreshButton
    ULCButton runButton

    ULCLabel lockedLabel
    ULCComboBox userInfoComboBox
    DefaultComboBoxModel userInfoComboBoxModel

    NavigationBarTopPane navigationBarTopPane
    ULCTableTree navigationTableTree

    @Resource
    DetailViewManager detailViewManager
    @Resource
    NavigationTableTreeModel navigationTableTreeModel
    Map windowMenus = [:]
    @Resource
    SelectionTreeView selectionTreeView

    @Override
    @PostConstruct
    void init() {
        super.init()
        syncMenuBar()
    }


    void initComponents() {
        content = new ULCBoxPane(2, 0)
        //init toolbar
        toolBar = new ULCToolBar()
        toolBar.floatable = false
        rightToolBar = new ULCToolBar()
        rightToolBar.floatable = false
        //init actions
        refreshAction = new RefreshAction(navigationTableTreeModel)
        exportAllNewestVersionAction = new ExportAllAction(true)
        exportAllAction = new ExportAllAction(false)
        importAllAction = new ImportAllAction("ImportAllParameterizations")
        saveAction = new SaveAction(content)
        runAction = new SimulationAction(selectionTreeView.selectionTree)

        //init menu
        menuBar = new ULCMenuBar()
        fileMenu = new ULCMenu(UIUtils.getText(HeaderView, "File"))
        fileMenu.mnemonic = 'F' as char
        saveItem = new ULCMenuItem(saveAction)
        saveItem.icon = null
        saveItem.mnemonic = 'S' as char
        saveItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK, false)
        saveAllItem = new ULCMenuItem(new SaveAllAction())
        refreshItem = new ULCMenuItem(refreshAction)
        runItem = new SimulationRunMenuItem(runAction)
        exportAllItemsNewstVersion = new ULCMenuItem(exportAllNewestVersionAction)
        exportAllItems = new ULCMenuItem(exportAllAction)
        importAllItems = new ULCMenuItem(importAllAction)
        exitItem = new ULCMenuItem(new ExitAction())
        exitItem.mnemonic = 'X' as char

        helpMenu = new ULCMenu(UIUtils.getText(HeaderView, "Help"))
        helpMenu.mnemonic = 'H' as char

        aboutItem = new ULCMenuItem(UIUtils.getText(HeaderView, "About"))
        aboutItem.mnemonic = 'A' as char

        traceLogItem = new ULCMenuItem(UIUtils.getText(HeaderView, "Log"))
        traceLogItem.mnemonic = 'L' as char

        settingsMenu = new ULCMenuItem(UIUtils.getText(HeaderView, "Settings"))

        windowMenu = new ULCMenu(UIUtils.getText(HeaderView, "Window"))

        //init buttons
        saveButton = new ULCButton(saveAction)
        saveButton.text = null
        saveButton.margin = new Insets(3, 3, 3, 3);
        saveButton.borderPainted = false;

        refreshButton = new ULCButton(refreshAction)
        refreshButton.name = "refresh"
        refreshButton.text = null
        refreshButton.toolTipText = "Refresh"
        refreshButton.margin = new Insets(3, 3, 3, 3);
        refreshButton.borderPainted = false;

        runButton = new ULCButton(runAction)
        runButton.text = null
        runButton.margin = new Insets(3, 3, 3, 3);
        runButton.borderPainted = false;
        //init labels
        lockedLabel = new ULCLabel()
        lockedLabel.text = null
        lockedLabel.icon = UIUtils.getIcon("locked-active.png")

        userInfoComboBoxModel = new DefaultComboBoxModel([UIUtils.userInfo, UIUtils.getText(HeaderView.class, "Logout")])
        userInfoComboBox = new ULCComboBox(userInfoComboBoxModel)
        userInfoComboBox.opaque = false
        userInfoComboBox.border = BorderFactory.createLineBorder(Color.lightGray, 1)
        userInfoComboBox.minimumSize = new Dimension(120, 20)
        userInfoComboBox.visible = UserContext.applet
    }

    void layoutComponents() {
        fileMenu.add(runItem)
        selectionTreeView.selectionTree?.addTreeSelectionListener(runItem)
        fileMenu.add(refreshItem)
        fileMenu.add(saveItem)
        fileMenu.add(saveAllItem)
        fileMenu.addSeparator()
        if (UserContext.standAlone) {
            fileMenu.add(exportAllItemsNewstVersion)
            fileMenu.add(exportAllItems)
            fileMenu.add(importAllItems)
            fileMenu.addSeparator()
        }

        fileMenu.add(exitItem)

        aboutItem.addActionListener([actionPerformed: { event -> new AboutDialog(UlcUtilities.getWindowAncestor(content)).visible = true }] as IActionListener)
        helpMenu.add(aboutItem)

        traceLogItem.addActionListener([actionPerformed: { event -> new UserTraceDialog(UlcUtilities.getWindowAncestor(content)).dialog.visible = true }] as IActionListener)
        helpMenu.add(traceLogItem)

        settingsMenu.addActionListener([actionPerformed: { event ->
            new UserSettingsViewDialog(new UserSettingsViewModel(), UlcUtilities.getWindowAncestor(content)).visible = true
        }] as IActionListener)
        windowMenu.add(settingsMenu)
        windowMenu.addSeparator()
        windowMenuItemGroup = new ULCButtonGroup()
        extensionMenuItemGroup = new ULCButtonGroup()


        menuBar.add(fileMenu)
        menuBar.add(windowMenu)
        menuBar.add(helpMenu)



        toolBar.add(refreshButton)
        toolBar.add(saveButton)
        toolBar.add(runButton)
        toolBar.addSeparator()

        navigationBarTopPane = new NavigationBarTopPane(toolBar, navigationTableTreeModel)
        navigationBarTopPane.init()

        rightToolBar.add(UIUtils.spaceAround(lockedLabel, 6, 3, 3, 3))
        rightToolBar.add(UIUtils.spaceAround(userInfoComboBox, 6, 3, 3, 3))

        content.add(IDefaults.BOX_EXPAND_TOP, toolBar)
        content.add(IDefaults.BOX_RIGHT_TOP, rightToolBar)
        syncMenuBar()
    }

    void attachListeners() {
        userInfoComboBox.addActionListener([actionPerformed: { ActionEvent evt ->
            if (userInfoComboBoxModel.selectedItem == UIUtils.getText(HeaderView.class, "Logout")) {
                String url = null
                try {
                    Holders.grailsApplication.mainContext.getBean('logoutService', LogoutService).logout(false)
                    url = UserContext.baseUrl + "/logout"
                    ClientContext.showDocument(url, "_self")
                } catch (Exception ex) {
                    LOG.error("Logout error by calling $url : $ex")
                } finally {
                    ApplicationContext.terminate()
                }
            }
        }] as IActionListener)
    }

    //todo fja refactoring to IEnabler
    // TODO Does this code need to also consider Resources ??
    public boolean syncMenuBar() {
        saveAction.enabled = saveAction.enabled
        if (detailViewManager.currentUIItem) {
            runAction.enabled = !((detailViewManager.currentUIItem instanceof SimulationSettingsUIItem) || (detailViewManager.currentUIItem instanceof BatchUIItem))
            if (detailViewManager.currentUIItem instanceof ParameterizationUIItem || detailViewManager.currentUIItem instanceof ResultConfigurationUIItem) {
                if (detailViewManager.currentUIItem.editable) {
                    lockedLabel.icon = UIUtils.getIcon("locked-inactive.png")
                } else {
                    lockedLabel.icon = UIUtils.getIcon("locked-active.png")
                }
            } else {
                lockedLabel.icon = UIUtils.getIcon("clear.png")
            }
        } else {
            runAction.enabled = false
            lockedLabel?.icon = UIUtils.getIcon("clear.png")
        }
    }

    void modelAdded(Model model, CardPaneManager cardPaneManager) {
        String name = WindowSelectionAction.getMenuName(model)
        if (windowMenus[name]) {
            return
        }
        ULCCheckBoxMenuItem item = new ULCCheckBoxMenuItem(new WindowSelectionAction(model, cardPaneManager))
        item.group = windowMenuItemGroup
        windowMenu.add(item)
        windowMenus[name] = item
    }

    void addWindowMenuEntry(String title, ULCCardPane cardPane, boolean selected) {
        ULCCheckBoxMenuItem item = new ULCCheckBoxMenuItem(new MainCardSelectionAction(title, cardPane))
        item.selected = selected
        item.group = extensionMenuItemGroup
        windowMenu.add(item)
    }
}
