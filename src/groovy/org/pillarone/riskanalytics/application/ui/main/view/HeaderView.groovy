package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.util.Insets
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.view.item.BatchUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ResultConfigurationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationUIItem
import org.pillarone.riskanalytics.application.ui.settings.model.UserSettingsViewModel
import org.pillarone.riskanalytics.application.ui.settings.view.UserSettingsViewDialog
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.main.action.*

/**
 *  UI for the header of RiskAnalytics to show File menus and action buttons
 * for saving, refreshing and running a simulation
 * @author fouad.jaada@intuitive-collaboration.com
 */
class HeaderView extends AbstractView {

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
    ULCMenuItem runItem
    ULCMenuItem exportAllItemsNewstVersion
    ULCMenuItem exportAllItems
    ULCMenuItem importAllItems
    ULCMenuItem saveItem
    ULCMenuItem exitItem
    ULCMenu helpMenu
    ULCMenuItem aboutItem
    ULCMenuItem settingsMenu
    ULCButtonGroup windowMenuItemGroup
    //buttons
    ULCButton saveButton
    ULCButton refreshButton
    ULCButton runButton

    ULCLabel lockedLabel

    NavigationBarTopPane navigationBarTopPane
    ULCTableTree navigationTableTree

    RiskAnalyticsMainModel model
    AbstractTableTreeModel navigationTableTreeModel
    Map windowMenus = [:]

    public HeaderView(ULCTableTree navigationTableTree, RiskAnalyticsMainModel model) {
        this.navigationTableTree = navigationTableTree
        this.model = model
        this.navigationTableTreeModel = model.navigationTableTreeModel
        init()
    }

    @Override
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
        exportAllNewestVersionAction = new ExportAllAction(model, true)
        exportAllAction = new ExportAllAction(model, false)
        importAllAction = new ImportAllAction(model, "ImportAllParameterizations")
        saveAction = new SaveAction(content, model)
        runAction = new SimulationAction(navigationTableTree, model)

        //init menu
        menuBar = new ULCMenuBar()
        fileMenu = new ULCMenu(UIUtils.getText(HeaderView, "File"))
        fileMenu.mnemonic = 'F'
        saveItem = new ULCMenuItem(saveAction)
        saveItem.icon = null
        saveItem.mnemonic = 'S'
        saveItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK, false)
        saveAllItem = new ULCMenuItem(new SaveAllAction(model))
        refreshItem = new ULCMenuItem(refreshAction)
        runItem = new SimulationRunMenuItem(runAction)
        exportAllItemsNewstVersion = new ULCMenuItem(exportAllNewestVersionAction)
        exportAllItems = new ULCMenuItem(exportAllAction)
        importAllItems = new ULCMenuItem(importAllAction)
        exitItem = new ULCMenuItem(new ExitAction())
        exitItem.mnemonic = 'X'

        helpMenu = new ULCMenu(UIUtils.getText(HeaderView, "Help"))
        helpMenu.mnemonic = 'H'

        aboutItem = new ULCMenuItem(UIUtils.getText(HeaderView, "About"))
        aboutItem.mnemonic = 'A'

        settingsMenu = new ULCMenuItem(UIUtils.getText(HeaderView, "Settings"))

        windowMenu = new ULCMenu(UIUtils.getText(HeaderView, "Window"))

        //init buttons
        saveButton = new ULCButton(saveAction)
        saveButton.text = null
        saveButton.setMargin(new Insets(3, 3, 3, 3));
        saveButton.setBorderPainted(false);

        refreshButton = new ULCButton(refreshAction)
        refreshButton.name = "refresh"
        refreshButton.text = null
        refreshButton.toolTipText = "Refresh"
        refreshButton.setMargin(new Insets(3, 3, 3, 3));
        refreshButton.setBorderPainted(false);

        runButton = new ULCButton(runAction)
        runButton.text = null
        runButton.setMargin(new Insets(3, 3, 3, 3));
        runButton.setBorderPainted(false);
        //init labels
        lockedLabel = new ULCLabel()
        lockedLabel.text = null
        lockedLabel.icon = UIUtils.getIcon("locked-active.png")//UIUtils.getIcon("clear.png")

    }

    void layoutComponents() {
        fileMenu.add(runItem)
        navigationTableTree?.addTreeSelectionListener(runItem)
        fileMenu.add(refreshItem)
        fileMenu.add(saveItem)
        fileMenu.add(saveAllItem)
        fileMenu.addSeparator()
        if (UserContext.isStandAlone()) {
            fileMenu.add(exportAllItemsNewstVersion)
            fileMenu.add(exportAllItems)
            fileMenu.add(importAllItems)
            fileMenu.addSeparator()
        }

        fileMenu.add(exitItem)

        aboutItem.addActionListener([actionPerformed: {event ->  new AboutDialog(UlcUtilities.getWindowAncestor(content)).visible = true}] as IActionListener)
        helpMenu.add(aboutItem)

        settingsMenu.addActionListener([actionPerformed: {event ->
            new UserSettingsViewDialog(new UserSettingsViewModel(), UlcUtilities.getWindowAncestor(content)).visible = true
        }] as IActionListener)
        windowMenu.add(settingsMenu)
        windowMenuItemGroup = new ULCButtonGroup()


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

        content.add(ULCBoxPane.BOX_EXPAND_TOP, toolBar)
        content.add(ULCBoxPane.BOX_RIGHT_TOP, rightToolBar)
        syncMenuBar()
    }

    void attachListeners() {}

    //todo fja refactoring to IEnabler
    public boolean syncMenuBar() {
        saveAction.enabled = saveAction.isEnabled()
        if (model.currentItem) {
            runAction.enabled = !((model.currentItem instanceof SimulationUIItem) || (model.currentItem instanceof BatchUIItem))
            if (model.currentItem instanceof ParameterizationUIItem || model.currentItem instanceof ResultConfigurationUIItem) {
                if (model.currentItem.isEditable()) {
                    lockedLabel.icon = UIUtils.getIcon("locked-active.png")
                } else {
                    lockedLabel.icon = UIUtils.getIcon("locked-inactive.png")
                }
            } else {
                lockedLabel.icon = UIUtils.getIcon("clear.png")
            }
        } else {
            runAction.enabled = false
            lockedLabel?.icon = UIUtils.getIcon("clear.png")
        }

    }

    void modelAdded(Model model, RiskAnalyticsMainView mainView) {
        String name = WindowSelectionAction.getMenuName(model)
        if (windowMenus[name]) return
        ULCCheckBoxMenuItem item = new ULCCheckBoxMenuItem(new WindowSelectionAction(model, mainView))
        item.setGroup(windowMenuItemGroup)
        windowMenu.add(item)
        windowMenus[name] = item

        mainView.setWindowTitle(name)
    }


}
