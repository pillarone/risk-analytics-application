package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.*
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.TableTreeBuilderUtils
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.FilterDefinition
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

import static com.ulcjava.base.application.ULCComponent.WHEN_IN_FOCUSED_WINDOW
import static com.ulcjava.base.application.ULCScrollPane.*
import static com.ulcjava.base.application.ULCSplitPane.HORIZONTAL_SPLIT
import static com.ulcjava.base.application.ULCSplitPane.VERTICAL_SPLIT
import static com.ulcjava.base.application.event.KeyEvent.*
import static com.ulcjava.base.shared.IDefaults.*

class RiskAnalyticsMainView extends AbstractView implements IRiskAnalyticsModelListener, IModellingItemChangeListener, PropertyChangeListener {

    private static final Log LOG = LogFactory.getLog(RiskAnalyticsMainView)

    public static final String DEFAULT_CARD_NAME = 'Main'
    public static final String CURRENT_ITEM_PROPERTY = 'currentItem'
    ULCCardPane content
    private ULCBoxPane treePane
    private ULCCardPane modelPane

    //header
    private HeaderView headerView
    //left view
    private SelectionTreeView navigationView
    //content
    private CardPaneManager cardPaneManager

    //model independent area below the modelPane
    private ModelIndependentDetailView modelIndependentDetailView

    private RiskAnalyticsMainModel mainModel
    private ToggleSplitPaneAction navigationSplitPaneAction
    private ToggleSplitPaneAction modelIndependentSplitPaneAction
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
        headerView = new HeaderView(navigationView.selectionTree, mainModel)
        modelIndependentDetailView = new ModelIndependentDetailView()
    }

    void layoutComponents() {
        modelPane.minimumSize = new Dimension(600, 600)
        treePane.add(BOX_EXPAND_EXPAND, navigationView.content)
        ULCSplitPane splitPane = new ULCSplitPane(HORIZONTAL_SPLIT)
        splitPane.oneTouchExpandable = true
        splitPane.resizeWeight = 1
        splitPane.dividerLocation = 200
        splitPane.dividerSize = 10

        splitPane.leftComponent = treePane
        ULCSplitPane splitBetweenModelPaneAndIndependentPane = new ULCSplitPane(VERTICAL_SPLIT)
        ULCScrollPane scrollPane = new ULCScrollPane(modelPane, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER)

        splitBetweenModelPaneAndIndependentPane.topComponent = scrollPane
        splitBetweenModelPaneAndIndependentPane.bottomComponent = modelIndependentDetailView.content
        splitBetweenModelPaneAndIndependentPane.oneTouchExpandable = true
        splitBetweenModelPaneAndIndependentPane.resizeWeight = 1
        splitBetweenModelPaneAndIndependentPane.dividerLocationAnimationEnabled
        splitBetweenModelPaneAndIndependentPane.dividerSize = 10
        splitPane.rightComponent = splitBetweenModelPaneAndIndependentPane

        ULCBoxPane selectionSwitchPane = new ULCBoxPane(1, 3)
        navigationSplitPaneAction = new ToggleSplitPaneAction(splitPane, UIUtils.getText(this.class, "Navigation"))
        ULCVerticalToggleButton navigationSwitchButton = new ULCVerticalToggleButton(navigationSplitPaneAction)
        navigationSwitchButton.selected = true
        selectionSwitchPane.add(BOX_LEFT_TOP, navigationSwitchButton);

        validationSplitPaneAction = new CommentsSwitchAction(mainModel, UIUtils.getText(this.class, "ValidationsAndComments"), false)
        ULCVerticalToggleButton validationSwitchButton = new ULCVerticalToggleButton(validationSplitPaneAction)
        validationSwitchButton.selected = false
        validationSwitchButton.enabled = false
        mainModel.switchActions << validationSwitchButton
        selectionSwitchPane.add(BOX_LEFT_TOP, validationSwitchButton);

        modelIndependentSplitPaneAction = new ToggleSplitPaneAction(splitBetweenModelPaneAndIndependentPane, UIUtils.getText(this.class, "ModelIndependent"), 1)
        ULCVerticalToggleButton modelIndependentSwitchButton = new ULCVerticalToggleButton(modelIndependentSplitPaneAction)
        modelIndependentSwitchButton.selected = false
        selectionSwitchPane.add(BOX_LEFT_TOP, modelIndependentSwitchButton);

        ULCBoxPane mainCard = new ULCBoxPane(2, 0)

        mainCard.add(2, BOX_EXPAND_TOP, headerView.content)
        mainCard.add(BOX_LEFT_TOP, selectionSwitchPane)
        mainCard.add(BOX_EXPAND_EXPAND, splitPane)

        content.addCard(DEFAULT_CARD_NAME, mainCard)
        headerView.addWindowMenuEntry(DEFAULT_CARD_NAME, content, true)
        WindowRegistry.allWindows.each { String key, ComponentCreator value ->
            content.addCard(key, value.createComponent(mainModel.applicationContext))
            headerView.addWindowMenuEntry(key, content, false)
        }
        headerView.windowMenu.addSeparator()
        content.selectedName = DEFAULT_CARD_NAME
    }

    void attachListeners() {
        content.registerKeyboardAction(navigationSplitPaneAction, KeyStroke.getKeyStroke(VK_N, CTRL_DOWN_MASK + SHIFT_DOWN_MASK), WHEN_IN_FOCUSED_WINDOW)
        content.registerKeyboardAction(validationSplitPaneAction, KeyStroke.getKeyStroke(VK_V, CTRL_DOWN_MASK + SHIFT_DOWN_MASK), WHEN_IN_FOCUSED_WINDOW)
        content.registerKeyboardAction(modelIndependentSplitPaneAction, KeyStroke.getKeyStroke(VK_Q, CTRL_DOWN_MASK + SHIFT_DOWN_MASK), WHEN_IN_FOCUSED_WINDOW)
        mainModel.addModelListener(this)
        mainModel.addPropertyChangeListener(CURRENT_ITEM_PROPERTY, this)
        headerView.navigationBarTopPane.addFilterChangedListener([filterChanged: { FilterDefinition filter ->
            navigationView.filterTree(filter)
        }] as IFilterChangedListener)
    }

    void openDetailView(Model model, AbstractUIItem item) {
        item.addModellingItemChangeListener(this)
        cardPaneManager.openItem(model, item)
        //todo notify Enabler instead of syncMenuBar
        headerView.syncMenuBar()
        //update window menu
        modelAdded(model)
        windowTitle = item
    }

    void openDetailView(Model model, ModellingItem item) {
        AbstractUIItem abstractUIItem = TableTreeBuilderUtils.findUIItemForItem(navigationView.root, item)
        if (!abstractUIItem) {
            LOG.error " AbstractUIItem (${item.name}) table tree node not found "
            abstractUIItem = UIItemFactory.createItem(item, model, mainModel)
        }
        if (!abstractUIItem.loaded) abstractUIItem.load(true)
        openDetailView(model, abstractUIItem)
    }

    void closeDetailView(Model model, AbstractUIItem abstractUIItem) {
        TabbedPaneManager tabbedPaneManager = cardPaneManager.getTabbedPaneManager(model)
        if (tabbedPaneManager) {
            tabbedPaneManager.removeTab(abstractUIItem)
            headerView.syncMenuBar()
        }
        windowTitle = null
    }

    void changedDetailView(Model model, AbstractUIItem item) {
        headerView.syncMenuBar()
    }

    void modelAdded(Model model) {
        headerView.modelAdded(model, cardPaneManager)
    }

    public void setWindowTitle(AbstractUIItem abstractUIItem) {
        ULCFrame window = UlcUtilities.getWindowAncestor(this.content) as ULCFrame
        window.title = "Risk Analytics - ${abstractUIItem ? abstractUIItem.windowTitle : ''}"
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
        if (evt.source == mainModel && evt.propertyName == CURRENT_ITEM_PROPERTY) {
            headerView.syncMenuBar()
        }
    }


}
