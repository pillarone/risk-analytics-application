package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.util.Dimension
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.TableTreeBuilderUtils
import org.pillarone.riskanalytics.application.ui.extension.ComponentCreator
import org.pillarone.riskanalytics.application.ui.extension.WindowRegistry
import org.pillarone.riskanalytics.application.ui.main.action.CommentsSwitchAction
import org.pillarone.riskanalytics.application.ui.main.action.ToggleSplitPaneAction
import org.pillarone.riskanalytics.application.ui.main.model.IRiskAnalyticsModelListener
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.ulc.server.ULCVerticalToggleButton

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.main.model.IRiskAnalyticsModelListener
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.application.event.KeyEvent

class RiskAnalyticsMainView extends AbstractView implements IRiskAnalyticsModelListener, IModellingItemChangeListener, PropertyChangeListener {

    public static final String DEFAULT_CARD_NAME = "Main"

    private ULCCardPane content
    private ULCBoxPane treePane
    private ULCCardPane modelPane

    //header
    private HeaderView headerView
    //left view
    private SelectionTreeView navigationView
    //content
    private CardPaneManager cardPaneManager

    private RiskAnalyticsMainModel mainModel


    Log LOG = LogFactory.getLog(RiskAnalyticsMainView)

    private ToggleSplitPaneAction navigationSplitPaneAction
    private CommentsSwitchAction validationSplitPaneAction

    RiskAnalyticsMainView(RiskAnalyticsMainModel mainModel) {
        this.mainModel = mainModel
    }

    void initComponents() {
        content = new ULCCardPane()
        treePane = new ULCBoxPane(1, 1)
        modelPane = new ULCCardPane()
        cardPaneManager = new CardPaneManager(modelPane, mainModel)
        navigationView = new SelectionTreeView(mainModel)
        headerView = new HeaderView(navigationView.getSelectionTree(), mainModel)
    }

    void layoutComponents() {
        modelPane.minimumSize = new Dimension(600, 600)
        treePane.add(ULCBoxPane.BOX_EXPAND_EXPAND, navigationView.content)
        ULCSplitPane splitPane = new ULCSplitPane(ULCSplitPane.HORIZONTAL_SPLIT)
        splitPane.oneTouchExpandable = true
        splitPane.setResizeWeight(1)
        splitPane.dividerLocation = 200
        splitPane.dividerSize = 10

        splitPane.setLeftComponent(treePane)
        splitPane.setRightComponent(modelPane)

        ULCBoxPane selectionSwitchPane = new ULCBoxPane(1, 3)
        navigationSplitPaneAction = new ToggleSplitPaneAction(splitPane, UIUtils.getText(this.class, "Navigation"))
        ULCVerticalToggleButton navigationSwitchButton = new ULCVerticalToggleButton(navigationSplitPaneAction)
        navigationSwitchButton.selected = true
        selectionSwitchPane.add(ULCBoxPane.BOX_LEFT_TOP, navigationSwitchButton);

        validationSplitPaneAction = new CommentsSwitchAction(mainModel, UIUtils.getText(this.class, "ValidationsAndComments"), false)
        ULCVerticalToggleButton validationSwitchButton = new ULCVerticalToggleButton(validationSplitPaneAction)
        validationSwitchButton.selected = false
        validationSwitchButton.setEnabled false
        mainModel.switchActions << validationSwitchButton
        selectionSwitchPane.add(ULCBoxPane.BOX_LEFT_TOP, validationSwitchButton);

        ULCBoxPane mainCard = new ULCBoxPane(2,0)

        mainCard.add(2, ULCBoxPane.BOX_EXPAND_TOP, headerView.content)
        mainCard.add(ULCBoxPane.BOX_LEFT_TOP, selectionSwitchPane)
        mainCard.add(ULCBoxPane.BOX_EXPAND_EXPAND, splitPane)

        content.addCard(DEFAULT_CARD_NAME, mainCard)
        headerView.addWindowMenuEntry(DEFAULT_CARD_NAME, content, true)
        WindowRegistry.allWindows.each { String key, ComponentCreator value ->
            content.addCard(key, value.createComponent(mainModel.applicationContext))
            headerView.addWindowMenuEntry(key, content, false)
        }
        headerView.windowMenu.addSeparator()
        content.setSelectedName(DEFAULT_CARD_NAME)
    }

    void attachListeners() {
        content.registerKeyboardAction(navigationSplitPaneAction, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK), ULCComponent.WHEN_IN_FOCUSED_WINDOW)
        content.registerKeyboardAction(validationSplitPaneAction, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK), ULCComponent.WHEN_IN_FOCUSED_WINDOW)
        mainModel.addModelListener(this)
        mainModel.addPropertyChangeListener("currentItem", this)
    }

    void openDetailView(Model model, AbstractUIItem item) {
        item.addModellingItemChangeListener(this)
        cardPaneManager.openItem(model, item)
        //todo notify Enabler instead of syncMenuBar
        headerView.syncMenuBar()
        //update window menu
        modelAdded(model)
        setWindowTitle(item)
    }

    void openDetailView(Model model, ModellingItem item) {
        AbstractUIItem abstractUIItem = TableTreeBuilderUtils.findUIItemForItem(navigationView.root, item)
        if (!abstractUIItem) {
            LOG.error " AbstractUIItem (${item.name}) table tree node not found "
            abstractUIItem = UIItemFactory.createItem(item, model, mainModel)
        }
        if (!abstractUIItem.isLoaded()) abstractUIItem.load(true)
        openDetailView(model, abstractUIItem)
    }

    void closeDetailView(Model model, AbstractUIItem abstractUIItem) {
        TabbedPaneManager tabbedPaneManager = cardPaneManager.getTabbedPaneManager(model)
        if (tabbedPaneManager) {
            tabbedPaneManager.removeTab(abstractUIItem)
            headerView.syncMenuBar()
        }
        setWindowTitle(null)
    }

    void changedDetailView(Model model, AbstractUIItem item) {
        headerView.syncMenuBar()
    }

    void modelAdded(Model model) {
        headerView.modelAdded(model, cardPaneManager)
    }

    public void setWindowTitle(AbstractUIItem abstractUIItem) {
        ULCWindow window = UlcUtilities.getWindowAncestor(this.content)
        window.title = "Risk Analytics - ${abstractUIItem ? abstractUIItem.getWindowTitle() : ''}"
    }

    ULCMenuBar getMenuBar() {
        return headerView.menuBar
    }

    void itemChanged(ModellingItem item) {
        if (item == mainModel?.currentItem?.item) {
            headerView.syncMenuBar()
        }
    }

    void itemSaved(ModellingItem item) {
    }

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.source == mainModel && evt.propertyName == "currentItem") {
            headerView.syncMenuBar()
        }
    }


}
