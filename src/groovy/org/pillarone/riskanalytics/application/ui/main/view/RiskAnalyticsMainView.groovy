package org.pillarone.riskanalytics.application.ui.main.view

import com.google.common.eventbus.Subscribe
import com.ulcjava.applicationframework.application.ApplicationContext
import com.ulcjava.base.application.*
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.FilterDefinition
import org.pillarone.riskanalytics.application.ui.extension.ComponentCreator
import org.pillarone.riskanalytics.application.ui.extension.WindowRegistry
import org.pillarone.riskanalytics.application.ui.main.action.CommentsSwitchAction
import org.pillarone.riskanalytics.application.ui.main.action.ToggleSplitPaneAction
import org.pillarone.riskanalytics.application.ui.main.model.IRiskAnalyticsModelListener
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.ParameterizationUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.SimulationResultUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.search.ModellingItemEvent
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.search.CacheItemEvent
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.ulc.server.ULCVerticalToggleButton
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

import static com.ulcjava.base.application.ULCComponent.WHEN_IN_FOCUSED_WINDOW
import static com.ulcjava.base.application.ULCSplitPane.HORIZONTAL_SPLIT
import static com.ulcjava.base.application.ULCSplitPane.VERTICAL_SPLIT
import static com.ulcjava.base.application.event.KeyEvent.*
import static com.ulcjava.base.shared.IDefaults.*

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class RiskAnalyticsMainView implements IRiskAnalyticsModelListener, IModellingItemChangeListener {

    private static final Log LOG = LogFactory.getLog(RiskAnalyticsMainView)

    static final String DEFAULT_CARD_NAME = 'Main'
    static final String CURRENT_ITEM_PROPERTY = 'currentItem'

    private static int SIM_QUEUE_HEIGHT     = 300;  //Sorry, actually the height of the pane above the sim queue
    private static int TOPRIGHT_PANE_HEIGHT = 600;
    private static int TOPRIGHT_PANE_WIDTH = 600;

    // Avoid building whole app just to tweak these settings
    //
    static {
        TOPRIGHT_PANE_HEIGHT = setIntFromSystemProperty("GUI_TOPRIGHT_PANE_HEIGHT", 600)
        TOPRIGHT_PANE_WIDTH  = setIntFromSystemProperty("GUI_TOPRIGHT_PANE_WIDTH",  600)
        SIM_QUEUE_HEIGHT     = setIntFromSystemProperty("GUI_SIM_QUEUE_HEIGHT",     300)
    }
    static int setIntFromSystemProperty( String key, int defaultValue ){
        try{
            return Integer.parseInt( System.getProperty(key, ""+defaultValue) )
        } catch( NumberFormatException e){ // Typo in configs
            LOG.warn("System property '$key' NOT an int, defaulting to $defaultValue")
            return defaultValue
        }
    }

    final ULCCardPane content = new ULCCardPane()

    //all views and main model are autowired
    @Resource
    CardPaneManager cardPaneManager
    @Resource
    SelectionTreeView selectionTreeView
    @Resource
    HeaderView headerView
    @Resource
    ModelIndependentDetailView modelIndependentDetailView
    @Resource
    RiskAnalyticsMainModel riskAnalyticsMainModel
    @Resource(name = 'ulcApplicationContext')
    ApplicationContext applicationContext

    private ToggleSplitPaneAction navigationSplitPaneAction
    private CommentsSwitchAction validationSplitPaneAction
    private ULCVerticalToggleButton validationSwitchButton

    @PostConstruct
    void initialize() {
        layoutComponents()
        attachListeners()
    }

    @PreDestroy
    void close() {
        riskAnalyticsMainModel.unregister(this)
    }



    void layoutComponents() {
        ULCCardPane modelPane = cardPaneManager.cardPane
        modelPane.preferredSize = new Dimension(TOPRIGHT_PANE_HEIGHT, TOPRIGHT_PANE_WIDTH)
        ULCBoxPane treePane = new ULCBoxPane(1, 1)
        treePane.add(BOX_EXPAND_EXPAND, selectionTreeView.content)
        ULCSplitPane splitPane = new ULCSplitPane(HORIZONTAL_SPLIT)
        splitPane.oneTouchExpandable = true
        splitPane.resizeWeight = 1
        splitPane.dividerLocation = 400
        splitPane.dividerSize = 10
        splitPane.leftComponent = treePane
        ULCSplitPane splitBetweenModelPaneAndIndependentPane = new ULCSplitPane(VERTICAL_SPLIT)
        splitBetweenModelPaneAndIndependentPane.topComponent = modelPane
        splitBetweenModelPaneAndIndependentPane.bottomComponent = modelIndependentDetailView.content
        splitBetweenModelPaneAndIndependentPane.oneTouchExpandable = true
        splitBetweenModelPaneAndIndependentPane.dividerSize = 10
        splitBetweenModelPaneAndIndependentPane.dividerLocation = SIM_QUEUE_HEIGHT
        splitPane.rightComponent = splitBetweenModelPaneAndIndependentPane

        ULCBoxPane selectionSwitchPane = new ULCBoxPane(1, 3)
        navigationSplitPaneAction = new ToggleSplitPaneAction(splitPane, UIUtils.getText(this.class, "Navigation"))
        ULCVerticalToggleButton navigationSwitchButton = new ULCVerticalToggleButton(navigationSplitPaneAction)
        navigationSwitchButton.selected = true
        selectionSwitchPane.add(BOX_LEFT_TOP, navigationSwitchButton);

        validationSplitPaneAction = new CommentsSwitchAction(riskAnalyticsMainModel, UIUtils.getText(this.class, "ValidationsAndComments"))
        validationSwitchButton = new ULCVerticalToggleButton(validationSplitPaneAction)
        validationSwitchButton.selected = false
        validationSwitchButton.enabled = false
        selectionSwitchPane.add(BOX_LEFT_TOP, validationSwitchButton);

        ULCBoxPane mainCard = new ULCBoxPane(2, 0)

        mainCard.add(2, BOX_EXPAND_TOP, headerView.content)
        mainCard.add(BOX_LEFT_TOP, selectionSwitchPane)
        mainCard.add(BOX_EXPAND_EXPAND, splitPane)

        content.addCard(DEFAULT_CARD_NAME, mainCard)
        headerView.addWindowMenuEntry(DEFAULT_CARD_NAME, content, true)
        WindowRegistry.allWindows.each { String key, ComponentCreator value ->
            content.addCard(key, value.createComponent(applicationContext))
            headerView.addWindowMenuEntry(key, content, false)
        }
        headerView.windowMenu.addSeparator()
        content.selectedName = DEFAULT_CARD_NAME
    }

    void attachListeners() {
        content.registerKeyboardAction(navigationSplitPaneAction, KeyStroke.getKeyStroke(VK_N, CTRL_DOWN_MASK + SHIFT_DOWN_MASK), WHEN_IN_FOCUSED_WINDOW)
        content.registerKeyboardAction(validationSplitPaneAction, KeyStroke.getKeyStroke(VK_V, CTRL_DOWN_MASK + SHIFT_DOWN_MASK), WHEN_IN_FOCUSED_WINDOW)
        riskAnalyticsMainModel.addModelListener(this)
        riskAnalyticsMainModel.register(this)
        headerView.navigationBarTopPane.addFilterChangedListener([filterChanged: { FilterDefinition filter ->
            selectionTreeView.filterTree(filter)
        }] as IFilterChangedListener)
    }

    void openDetailView(AbstractUIItem item) {
        cardPaneManager.openItem(item)
        //todo notify Enabler instead of syncMenuBar
        headerView.syncMenuBar()
        //update window menu
        modelAdded(item.model)
        currentItem = item
    }

    @Subscribe
    void closeDetailViewIfItemRemoved(ModellingItemEvent event) {
        if (event.eventType == CacheItemEvent.EventType.REMOVED) {
            closeDetailView(UIItemFactory.createItem(event.modellingItem))
        }
    }

    void closeDetailView(AbstractUIItem abstractUIItem) {
        TabbedPaneManager tabbedPaneManager = cardPaneManager.getTabbedPaneManager(abstractUIItem.model)
        if (tabbedPaneManager) {
            tabbedPaneManager.removeTab(abstractUIItem)
            headerView.syncMenuBar()
        }
    }

    void changedDetailView(AbstractUIItem item) {
        headerView.syncMenuBar()
        currentItem = item
    }

    void modelAdded(Model model) {
        headerView.modelAdded(model, cardPaneManager)
    }

    void setWindowTitle(String windowTitle) {
        ULCFrame window = UlcUtilities.getWindowAncestor(this.content) as ULCFrame
        window.title = "Risk Analytics - ${(windowTitle ?: '')}"
    }

    ULCMenuBar getMenuBar() {
        return headerView.menuBar
    }

    void itemChanged(ModellingItem item) {
        AbstractUIItem currentItem = riskAnalyticsMainModel.currentItem
        if (currentItem && (currentItem instanceof ModellingUIItem) && currentItem.item == item) {
            headerView.syncMenuBar()
        }
    }

    void itemSaved(ModellingItem item) {
    }

    private setCurrentItem(AbstractUIItem newIem) {
        if (riskAnalyticsMainModel.currentItem != newIem) {
            riskAnalyticsMainModel.currentItem = newIem
            updateValidationSwitchButton()
            headerView.syncMenuBar()
            windowTitle = riskAnalyticsMainModel.currentItem?.windowTitle
        }
    }

    private void updateValidationSwitchButton() {
        AbstractUIItem currentItem = riskAnalyticsMainModel.currentItem
        boolean shouldToggle = (currentItem instanceof ParameterizationUIItem) || (currentItem instanceof SimulationResultUIItem)
        validationSwitchButton.enabled = shouldToggle
        validationSwitchButton.selected = shouldToggle
    }
}
