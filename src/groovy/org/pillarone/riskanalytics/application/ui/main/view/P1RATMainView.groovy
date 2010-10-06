package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.Insets
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.application.util.ULCIcon
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import org.pillarone.riskanalytics.application.ui.batch.action.TreeDoubleClickAction
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.batch.view.NewBatchView
import org.pillarone.riskanalytics.application.ui.main.model.IP1RATModelListener
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.CompareParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationUtilities
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.result.model.CompareSimulationsViewModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.result.model.SimulationNode
import org.pillarone.riskanalytics.application.ui.resultconfiguration.view.ResultConfigurationView
import org.pillarone.riskanalytics.application.ui.settings.model.UserSettingsViewModel
import org.pillarone.riskanalytics.application.ui.settings.view.UserSettingsViewDialog
import org.pillarone.riskanalytics.application.ui.simulation.view.CalculationConfigurationView
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationConfigurationView
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.ui.util.server.ULCVerticalToggleButton
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.result.view.*
import org.pillarone.riskanalytics.core.simulation.item.*

class P1RATMainView implements IP1RATModelListener, IModellingItemChangeListener, PropertyChangeListener {

    P1RATModel model

    ULCBoxPane content
    ULCMenuBar menuBar
    ULCToolBar toolBar
    ULCToolBar rightToolBar

    SaveAction saveAction
    SimulationAction runAction
    RefreshAction refreshAction
    ExportAllAction exportAllNewestVersionAction
    ExportAllAction exportAllAction
    ImportAllAction importAllAction
    ShowUserSettingsAction settingsAction

    ULCBoxPane treePane
    ULCCardPane modelPane
    ULCMenu windowMenu
    ULCButtonGroup windowMenuItemGroup
    ULCTree selectionTree

    ULCLabel lockedLabel

    private AboutDialog aboutDialog
    private UserSettingsViewDialog settingsViewDialog
    CompareSimulationsViewModel compareSimulationsViewModel
    CompareParameterViewModel compareParameterViewModel
    def view

    Map openItems
    Map openModels
    Map windowMenus

    TabbedPaneManagerHelper tabbedPaneManagerHelper = new TabbedPaneManagerHelper()

    public P1RATMainView(P1RATModel model) {
        this.model = model
        openItems = [:]
        openModels = [:]
        windowMenus = [:]
        initComponents()
        layoutComponents()
        attachListeners()
    }

    void initComponents() {

        content = new ULCBoxPane(2, 0)
        model.rootPaneForAlerts = content

        selectionTree = new ULCTree(model.selectionTreeModel)
        initMenuBar()

        treePane = new ULCBoxPane(1, 1)
        modelPane = new ULCCardPane()

        selectionTree.name = "selectionTree"
        selectionTree.rootVisible = false
        selectionTree.showsRootHandles = true
        selectionTree.editable = false
        selectionTree.setCellRenderer(new MainSelectionTreeCellRenderer(selectionTree, model))

    }

    void layoutComponents() {
        ULCScrollPane treeScrollPane = new ULCScrollPane(selectionTree)
        treeScrollPane.minimumSize = new Dimension(200, 600)
        modelPane.minimumSize = new Dimension(600, 600)
        treePane.add(ULCBoxPane.BOX_EXPAND_EXPAND, treeScrollPane)
        ULCSplitPane splitPane = new ULCSplitPane(ULCSplitPane.HORIZONTAL_SPLIT)
        splitPane.dividerLocation = 200
        splitPane.dividerSize = 5

        splitPane.setLeftComponent(treePane)
        splitPane.setRightComponent(modelPane)

        ULCBoxPane selectionSwitchPane = new ULCBoxPane(1, 0)
        selectionSwitchPane.preferredSize = new Dimension(25, 90)
        ULCVerticalToggleButton navigationSwitchButton = new ULCVerticalToggleButton(new ToggleSplitPaneAction(splitPane, "Navigation"))
        navigationSwitchButton.selected = true
        selectionSwitchPane.add(ULCBoxPane.BOX_LEFT_TOP, navigationSwitchButton);

        ULCBoxPane toolBarLockPane = new ULCBoxPane(2, 0)
        toolBarLockPane.add(ULCBoxPane.BOX_EXPAND_TOP, toolBar)
        toolBarLockPane.add(ULCBoxPane.BOX_RIGHT_TOP, rightToolBar)

        content.add(2, ULCBoxPane.BOX_EXPAND_TOP, toolBarLockPane)
        content.add(ULCBoxPane.BOX_LEFT_TOP, selectionSwitchPane)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, splitPane)
    }

    void attachListeners() {
        model.addModelListener(this)
        model.addPropertyChangeListener("currentItem", this)
        //add action listener
        selectionTree.addActionListener(new TreeDoubleClickAction(selectionTree, model))
        selectionTree.registerKeyboardAction(new DeleteAction(selectionTree, model), KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), ULCComponent.WHEN_FOCUSED)
    }


    private def initMenuBar() {
        refreshAction = new RefreshAction(model)
        exportAllNewestVersionAction = new ExportAllAction(this, model, true)
        exportAllAction = new ExportAllAction(this, model, false)
        importAllAction = new ImportAllAction(this, model, "ImportAllParameterizations")
        saveAction = new SaveAction(model)
        settingsAction = new ShowUserSettingsAction(this)
        runAction = new SimulationAction(selectionTree, model)
        syncMenuBar()
        menuBar = new ULCMenuBar()
        ULCMenu fileMenu = new ULCMenu(getText("File"))
        fileMenu.mnemonic = 'F'
        ULCMenuItem saveItem = new ULCMenuItem(saveAction)
        saveItem.icon = null
        saveItem.mnemonic = 'S'
        saveItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK, false)
        ULCMenuItem saveAllItem = new ULCMenuItem(new SaveAllAction(model))
        ULCMenuItem refreshItem = new ULCMenuItem(refreshAction)
        ULCMenuItem runItem = new ULCMenuItem(runAction)
        ULCMenuItem exportAllItemsNewstVersion = new ULCMenuItem(exportAllNewestVersionAction)
        ULCMenuItem exportAllItems = new ULCMenuItem(exportAllAction)
        ULCMenuItem importAllItems = new ULCMenuItem(importAllAction)


        fileMenu.add(runItem)
        fileMenu.add(refreshItem)
        fileMenu.add(saveItem)
        fileMenu.add(saveAllItem)
        fileMenu.addSeparator()
        fileMenu.add(exportAllItemsNewstVersion)
        fileMenu.add(exportAllItems)
        fileMenu.add(importAllItems)
        fileMenu.addSeparator()
        ULCMenuItem exitItem = new ULCMenuItem(new ExitAction())
        exitItem.mnemonic = 'X'
        fileMenu.add(exitItem)

        ULCMenu helpMenu = new ULCMenu(getText("Help"))
        helpMenu.mnemonic = 'H'
        ULCMenuItem aboutItem = new ULCMenuItem(getText("About"))
        aboutItem.mnemonic = 'A'
        aboutItem.addActionListener([actionPerformed: {event -> openAboutDialog()}] as IActionListener)
        helpMenu.add(aboutItem)

        windowMenu = new ULCMenu(getText("Window"))
        ULCMenuItem settingsMenu = new ULCMenuItem(settingsAction)
        windowMenu.add(settingsMenu)
        windowMenuItemGroup = new ULCButtonGroup()


        menuBar.add(fileMenu)
        menuBar.add(windowMenu)
        menuBar.add(helpMenu)

        toolBar = new ULCToolBar()
        toolBar.floatable = false

        ULCButton saveButton = new ULCButton(saveAction)
        saveButton.text = null
        saveButton.setMargin(new Insets(3, 3, 3, 3));
        saveButton.setBorderPainted(false);

        ULCButton refreshButton = new ULCButton(refreshAction)
        refreshButton.name = "refresh"
        refreshButton.text = null
        refreshButton.toolTipText = "Refresh"
        refreshButton.setMargin(new Insets(3, 3, 3, 3));
        refreshButton.setBorderPainted(false);

        ULCButton runButton = new ULCButton(runAction)
        runButton.text = null
        runButton.setMargin(new Insets(3, 3, 3, 3));
        runButton.setBorderPainted(false);



        toolBar.add(refreshButton)
        toolBar.add(saveButton)
        toolBar.add(runButton)

        rightToolBar = new ULCToolBar()
        rightToolBar.floatable = false

        lockedLabel = new ULCLabel()
        lockedLabel.text = null

        lockedLabel.icon = UIUtils.getIcon("clear.png")

        rightToolBar.add(UIUtils.spaceAround(lockedLabel, 6, 3, 3, 3))
    }

    protected ULCComponent createDetailView(def currentItem, Model simulationModel) {
        throw new RuntimeException("unknown object type: ${currentItem.class}")
    }

    protected ULCComponent createDetailView(Parameterization currentItem, Model simulationModel) {
        ParameterView view = new ParameterView(model.getParameterViewModel(currentItem, simulationModel))
        model.addModelItemChangedListener(view)
        return view.content
    }

    protected ULCComponent createDetailView(ResultConfiguration currentItem, Model simulationModel) {
        ResultConfigurationView view = new ResultConfigurationView(model.getResultConfigurationViewModel(currentItem, simulationModel))
        model.addModelItemChangedListener(view)
        return view.content
    }

    protected ULCComponent createDetailView(Simulation currentItem, Model simulationModel) {
        def view
        if (currentItem.start == null) {
            def simulationConfigurationModel = model.getSimulationConfigurationModel(currentItem, simulationModel)
            view = new SimulationConfigurationView(simulationConfigurationModel)
        } else {
            ResultViewModel resultViewModel = model.getResultViewModel(currentItem, simulationModel)
            view = new StochasticResultView(null)
            view.p1ratModel = model
            view.model = resultViewModel

            resultViewModel.addFunctionListener(view)
        }
        return view.content
    }

    protected ULCComponent createDetailView(BatchRun batchRun, ULCDetachableTabbedPane tabbedPane) {
        if (batchRun.id != null) {
            BatchView view = new BatchView(this.model, batchRun, tabbedPane)
            view.addIP1RATModelListener this
            return view.content
        } else {
            return new NewBatchView(this.model, tabbedPane).content
        }
    }


    protected ULCComponent createCompareView(List simulations, Model simulationModel) {
        compareSimulationsViewModel = model.getCompareSimulationsViewModel(simulations, simulationModel)
        view = new CompareSimulationsView(compareSimulationsViewModel, this)
        return view.content
    }

    protected ULCComponent createCompareParameterizationView(List<Parameterization> parameterizations, Model simulationModel) {
        compareParameterViewModel = model.getCompareParameterViewModel(parameterizations, simulationModel)
        view = new CompareParameterizationsView(compareParameterViewModel)
        return view.content
    }

    protected ULCComponent createCompareView(List simulations, DeterministicModel simulationModel) {
        compareSimulationsViewModel = model.getCompareSimulationsViewModel(simulations, simulationModel)
        view = new CompareDeterministicsView(compareSimulationsViewModel)
        return view.content
    }




    protected ULCComponent createDetailView(Simulation currentItem, DeterministicModel simulationModel) {
        def view
        if (currentItem.start == null) {
            view = new CalculationConfigurationView(model.getSimulationConfigurationModel(currentItem, simulationModel))
        } else {
            ResultViewModel resultViewModel = model.getResultViewModel(currentItem, simulationModel)
            view = new DeterministicResultView(null)
            view.p1ratModel = model
            view.model = resultViewModel

            resultViewModel.addFunctionListener(view)
        }
        return view.content
    }

    private def createOrSelectTab(Model selectedModel, ModellingItem item) {
        ULCDetachableTabbedPane tabbedPane = createOrSelectCard(modelPane, selectedModel)
        String title = createTabTitleForItem(item, selectedModel)
        if (tabExists(tabbedPane, title)) {
            selectTab(tabbedPane, title)
        } else {
            Object detailView = createDetailView(item, selectedModel)
            openItems[detailView] = item
            openModels[detailView] = selectedModel
            tabbedPane.addTab(title, getTabIconForItem(item), detailView)
            int tabIndex = tabbedPane.tabCount - 1
            tabbedPane.selectedIndex = tabIndex
            item.addModellingItemChangeListener new MarkItemAsUnsavedListener(tabbedPane, tabIndex)
        }
        return tabbedPane.getSelectedComponent()
    }

    private def createOrSelectTab(Model selectedModel, List simulations) {
        ULCCloseableTabbedPane tabbedPane = createOrSelectCard(modelPane, selectedModel)
        String title = tabbedPaneManagerHelper.getTabTitle(TabbedPaneManagerHelper.SIMULATION, simulations)
        if (tabExists(tabbedPane, title)) {
            selectTab(tabbedPane, title)
        } else {
            Object detailView = createCompareView(simulations, selectedModel)
            tabbedPane.addTab(title, getTabIconForItem(simulations[0].item), detailView)
            int tabIndex = tabbedPane.tabCount - 1
            tabbedPane.selectedIndex = tabIndex
            tabbedPane.setToolTipTextAt(tabIndex, tabbedPaneManagerHelper.getToolTip(simulations))
        }
        return tabbedPane.getSelectedComponent()
    }

    private def createOrSelectTab(BatchRun batchRun) {
        ULCDetachableTabbedPane tabbedPane = createOrSelectCard(modelPane, batchRun)
        String title = batchRun.name ? batchRun.name : getText("CreateNewBatch")
        if (tabExists(tabbedPane, title)) {
            selectTab(tabbedPane, title)
        } else {
            Object detailView = createDetailView(batchRun, tabbedPane)
            openItems[detailView] = batchRun
            tabbedPane.addTab(title, UIUtils.getIcon("runsimulation-active.png"), detailView)
            int tabIndex = tabbedPane.tabCount - 1
            tabbedPane.selectedIndex = tabIndex
        }
        return tabbedPane.getSelectedComponent()
    }


    private def createCompareParameterizationOrSelectTab(Model selectedModel, List<Parameterization> parameterizations) {
        ULCCloseableTabbedPane tabbedPane = createOrSelectCard(modelPane, selectedModel)
        String title = tabbedPaneManagerHelper.getTabTitle(TabbedPaneManagerHelper.PARAMETERIZATION, parameterizations)
        int tabIndex = getTabIndexForName(tabbedPane, title)
        if (tabIndex >= 0) {
            tabbedPane.selectedIndex = tabIndex
        } else {
            Object detailView = createCompareParameterizationView(parameterizations, selectedModel)
            tabbedPane.addTab(title, UIUtils.getIcon("parametrization-active.png"), detailView)
            tabIndex = tabbedPane.tabCount - 1
            tabbedPane.selectedIndex = tabIndex
            tabbedPane.setToolTipTextAt(tabIndex, tabbedPaneManagerHelper.getToolTip(parameterizations))
        }
        return tabbedPane.getSelectedComponent()
    }

    public def createCompareParameterizationView(Model simulationModel, List<Simulation> simulations) {
        List<Parameterization> parameterizations = ParameterizationUtilities.getParameters(simulations)
        return createCompareParameterizationOrSelectTab(simulationModel, parameterizations)
    }

    private ULCIcon getTabIconForItem(Parameterization parameterization) {
        UIUtils.getIcon("parametrization-active.png")
    }

    private ULCIcon getTabIconForItem(ResultConfiguration resultConfiguration) {
        UIUtils.getIcon("resulttemplate-active.png")
    }

    private ULCIcon getTabIconForItem(Simulation simulation) {
        UIUtils.getIcon("results-active.png")
    }

    private int getTabIndexForName(ULCDetachableTabbedPane tabbedPane, String tabTitle) {
        int tabIndex = -1
        tabbedPane.tabCount.times {
            if (tabbedPane?.getTitleAt(it)?.startsWith(tabTitle)) {
                tabIndex = it
            }
        }
        return tabIndex
    }

    private boolean tabExists(ULCDetachableTabbedPane tabbedPane, String tabTitle) {
        tabbedPane.anyTabContains(tabTitle)
    }

    private void selectTab(ULCDetachableTabbedPane tabbedPane, String tabTitle) {
        ULCDetachableTabbedPane selectedPane = findTabbedPane(tabbedPane, tabTitle)
        selectedPane.selectedIndex = getTabIndexForName(selectedPane, tabTitle)

        ULCWindow containingWindow = UlcUtilities.getWindowAncestor(selectedPane)
        if (containingWindow) {
            containingWindow.toFront()
        }
    }

    private ULCDetachableTabbedPane findTabbedPane(ULCDetachableTabbedPane rootTabbedPane, String tabTitle) {
        int paneId = rootTabbedPane.findFrameID(tabTitle)
        if (paneId < 0) {
            throw new IllegalArgumentException("Tab does not exist")
        }

        ULCDetachableTabbedPane selectedPane = paneId == 0 ? rootTabbedPane : rootTabbedPane.getDependantTabbedPane(paneId - 1)
        return selectedPane
    }

    private ULCCloseableTabbedPane createOrSelectCard(ULCCardPane cardPane, Model selectedModel) {

        if (!(cardPane.getNames() as List).contains(selectedModel.name)) {

            ULCCloseableTabbedPane modelCardContent = createCloseableTabbedPane()

            cardPane.addCard(selectedModel.name, modelCardContent)

            ULCCheckBoxMenuItem item = new ULCCheckBoxMenuItem(new WindowSelectionAction(selectedModel.name, this))
            item.setGroup(windowMenuItemGroup)
            windowMenu.add(item)
            windowMenus[selectedModel.name] = item

            windowTitle = selectedModel.name
        }
        cardPane.selectedName = selectedModel.name // TODO (msh): Ask Dani why setSelected does not trigger the action

        windowMenu.getMenuComponents().each {ULCMenuItem menuItem ->
            if (menuItem instanceof ULCCheckBoxMenuItem) {
                def menuTitle = menuItem.text
                if (menuTitle == selectedModel.name) {
                    menuItem.selected = true
                }
            }
        }
        return (ULCCloseableTabbedPane) cardPane.getSelectedComponent()
    }

    private ULCCloseableTabbedPane createOrSelectCard(ULCCardPane cardPane, BatchRun batchRun) {
        String batchName = batchRun?.name ? batchRun.name : getText("NewBatch")

        if (!(cardPane.getNames() as List).contains(batchName)) {

            ULCCloseableTabbedPane modelCardContent = createCloseableTabbedPane()

            cardPane.addCard(batchName, modelCardContent)

            ULCCheckBoxMenuItem item = new ULCCheckBoxMenuItem(new WindowSelectionAction(batchName, this))
            item.setGroup(windowMenuItemGroup)
            windowMenu.add(item)
            windowMenus[batchName] = item

            windowTitle = batchName
        }
        cardPane.selectedName = batchName // TODO (msh): Ask Dani why setSelected does not trigger the action

        windowMenu.getMenuComponents().each {ULCMenuItem menuItem ->
            if (menuItem instanceof ULCCheckBoxMenuItem) {
                def menuTitle = menuItem.text
                if (menuTitle == batchName) {
                    menuItem.selected = true
                }
            }
        }
        return (ULCCloseableTabbedPane) cardPane.getSelectedComponent()
    }


    public setWindowTitle(String modelName) {
        ULCWindow window = UlcUtilities.getWindowAncestor(this.content)
        window.title = "Risk Analytics - ${modelName}"

    }

    private AboutDialog getAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = new AboutDialog(UlcUtilities.getWindowAncestor(content), {event -> getAboutDialog().visible = false})
        }
        return aboutDialog
    }

    private UserSettingsViewDialog getSettingsViewDialog() {
        if (settingsViewDialog == null) {
            settingsViewDialog = new UserSettingsViewDialog(new UserSettingsViewModel(), UlcUtilities.getWindowAncestor(content))//, {event -> getLoginViewDialog().visible = false})
        }
        return settingsViewDialog
    }

    ULCCloseableTabbedPane createCloseableTabbedPane() {
        ULCCloseableTabbedPane tabbedPane = new ULCDetachableTabbedPane()
        tabbedPane.addTabListener([tabClosing: {TabEvent event ->
            int closingIndex = event.getTabClosingIndex()
            if (closingIndex < 0) closingIndex = 0
            ULCCloseableTabbedPane modelCardContent = event.getClosableTabbedPane()
            ULCComponent currentComponent = modelCardContent.getComponentAt(closingIndex)
            def item = openItems[currentComponent]
            def modelForItem = openModels[currentComponent]
            if (isChanged(item)) {
                boolean closeTab = true
                ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(this.content), "itemChanged")
                alert.addWindowListener([windowClosing: {WindowEvent windowEvent ->
                    def value = windowEvent.source.value
                    if (value.equals(alert.firstButtonLabel)) {
                        item.save()
                    } else if (value.equals(alert.thirdButtonLabel)) {
                        modelCardContent.setSelectedIndex closingIndex
                        closeTab = false
                    } else {
                        item.unload()
                    }
                    if (closeTab) {
                        openItems.remove(currentComponent)
                        def removedModel = openModels.remove(currentComponent)
                        modelCardContent.removeTabAt closingIndex
                        model.closeItem(modelForItem, item)
                        removeCard(modelCardContent)
                    }

                }] as IWindowListener)
                alert.show()
            }
            if (!isChanged(item)) {
                openItems.remove(currentComponent)
                def removedModel = openModels.remove(currentComponent)
                modelCardContent.removeTabAt closingIndex
                model.closeItem(modelForItem, item)
                removeCard(modelCardContent)
            }
        }] as ITabListener)

        Closure syncCurrentItem = {e -> selectCurrentItemFromTab(e.source)}
        tabbedPane.selectionChanged = syncCurrentItem
        tabbedPane.focusGained = syncCurrentItem

/*
        tabbedPane.addSelectionChangedListener([selectionChanged: {SelectionChangedEvent e ->
            selectCurrentItemFromTab(e.source)
        }] as ISelectionChangedListener)
        tabbedPane.addFocusListener([focusGained: {e ->
            selectCurrentItemFromTab(e.source)
        }, focusLost: {e ->}] as IFocusListener)
*/
        return tabbedPane
    }

    void removeCard(ULCCloseableTabbedPane modelCardContent) {
        try {
            String selectedName = modelPane.getSelectedName()
            if (modelCardContent && modelCardContent.getTabCount() == 0) {
                modelPane.removeCard(modelCardContent)
                if (selectedName && windowMenus[selectedName])
                    windowMenu.remove(windowMenus[selectedName])
            }
        } catch (Exception ex) {}
    }

    private boolean isChanged(item) {
        return item && item.properties.containsKey("changed") && item.isChanged()
    }

    private void selectCurrentItemFromTab(ULCCloseableTabbedPane modelCardContent) {
        def item = openItems[modelCardContent.getSelectedComponent()]
        if (!(item instanceof BatchRun))
            model.currentItem = item
    }

    public void openDetailView(Model selectedModel, Object item) {
        item.addModellingItemChangeListener(this)
        createOrSelectTab(selectedModel, item)
    }

    public void openDetailView(Model selectedModel, BatchRun batchRun) {
        createOrSelectTab(batchRun)
    }


    public void openDetailView(Model selectedModel, List items) {
        if (items?.size() > 1 && items.get(0) instanceof SimulationNode) {
            createOrSelectTab(selectedModel, items)
        } else if (items?.size() > 1 && items.get(0) instanceof ParameterizationNode) {
            createCompareParameterizationOrSelectTab(selectedModel, items)
        }
    }

    public void openCompareParameterizationDetailView(Model selectedModel, List<Parameterization> parameterizations) {
        createCompareParameterizationOrSelectTab(selectedModel, parameterizations)
    }

    public void closeDetailView(Model model, Object item) {
        if (item == null) {
            return
        } else {
            if ((modelPane.getNames() as List).contains(model.name)) {
                ULCDetachableTabbedPane modelCardContent = modelPane.getComponentAt(model.name)
                int tabIndex = getTabIndexForName(modelCardContent, createTabTitleForItem(item, model))
                String title = createTabTitleForItem(item, model)
                boolean tabExist = tabExists(modelCardContent, title)
                if (tabIndex >= 0 && tabExist) {
                    ULCDetachableTabbedPane pane = findTabbedPane(modelCardContent, title)
                    pane.closeCloseableTab(tabIndex)
                } else if (tabExist && modelCardContent.dependentFrames) {
                    modelCardContent.dependentFrames.each {ULCFrame frame ->
                        if (frame.title.equals(title)) {
                            ULCCloseableTabbedPane tp = (ULCCloseableTabbedPane) frame.getContentPane().getComponents()[0]
                            tp.closeCloseableTab(0)
                            frame.dispose()
                        }
                    }
                }
            }
            item.removeModellingItemChangeListener(this)
        }

    }

    void openAboutDialog() {
        getAboutDialog().visible = true
    }

    void openSettingsViewDialog() {
        getSettingsViewDialog().visible = true
    }

    public void itemChanged(ModellingItem item) {
        if (item == model.currentItem) {
            syncMenuBar()
        }
        runAction.enabled = selectionTree?.selectionPath?.lastPathComponent != null
    }

    public void itemSaved(ModellingItem item) {

    }



    private boolean syncMenuBar() {
        saveAction.enabled = model.currentItem != null ? model.currentItem.changed : false
        if (model.currentItem) {
            runAction.enabled = !((model.currentItem instanceof Simulation) || (model.currentItem instanceof BatchRun))
            if (model.currentItem instanceof Parameterization || model.currentItem instanceof ResultConfiguration) {
                if (model.currentItem.isUsedInSimulation()) {
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

    private String createTabTitleForItem(ResultConfiguration item, Model selectedModel) {
        "$item.name v${item.versionNumber.toString()}".toString()
    }

    private String createTabTitleForItem(Parameterization item, Model selectedModel) {
        "$item.name v${item.versionNumber.toString()}".toString()
    }

    private String createTabTitleForItem(ConfigObjectBasedModellingItem item, Model selectedModel) {
        "$item.name v${item.versionNumber.toString()}".toString()
    }

    private String createTabTitleForItem(ModellingItem item, Model selectedModel) {
        "$item.name".toString()
    }

    private String createTabTitleForItem(Simulation item, DeterministicModel selectedModel) {
        item.start == null ? "Calculation" : item.name
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("RiskAnalyticsMainView." + key);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.source == model && evt.propertyName == "currentItem") {
            syncMenuBar()
        }
    }


}
