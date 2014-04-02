package org.pillarone.riskanalytics.application.ui.main.view
import com.ulcjava.applicationframework.application.ApplicationContext
import com.ulcjava.base.application.*
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
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
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import static com.ulcjava.base.application.ULCComponent.WHEN_IN_FOCUSED_WINDOW
import static com.ulcjava.base.application.ULCSplitPane.HORIZONTAL_SPLIT
import static com.ulcjava.base.application.ULCSplitPane.VERTICAL_SPLIT
import static com.ulcjava.base.application.event.KeyEvent.*
import static com.ulcjava.base.shared.IDefaults.*

@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class RiskAnalyticsMainView implements IRiskAnalyticsModelListener, IModellingItemChangeListener, PropertyChangeListener {

    private static final Log LOG = LogFactory.getLog(RiskAnalyticsMainView)

    static final String DEFAULT_CARD_NAME = 'Main'
    static final String CURRENT_ITEM_PROPERTY = 'currentItem'
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
    private ToggleSplitPaneAction modelIndependentSplitPaneAction
    private CommentsSwitchAction validationSplitPaneAction

    @PostConstruct
    void initialize() {
        layoutComponents()
        attachListeners()
    }

    void layoutComponents() {
        ULCCardPane modelPane = cardPaneManager.cardPane
        modelPane.minimumSize = new Dimension(600, 600)
        ULCBoxPane treePane = new ULCBoxPane(1, 1)
        treePane.add(BOX_EXPAND_EXPAND, selectionTreeView.content)
        ULCSplitPane splitPane = new ULCSplitPane(HORIZONTAL_SPLIT)
        splitPane.oneTouchExpandable = true
        splitPane.resizeWeight = 1
        splitPane.dividerLocation = 200
        splitPane.dividerSize = 10
        splitPane.leftComponent = treePane
        ULCSplitPane splitBetweenModelPaneAndIndependentPane = new ULCSplitPane(VERTICAL_SPLIT)
//        ULCScrollPane scrollPane = new ULCScrollPane(modelPane, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER)
        splitBetweenModelPaneAndIndependentPane.topComponent = modelPane
        splitBetweenModelPaneAndIndependentPane.bottomComponent = modelIndependentDetailView.content
        splitBetweenModelPaneAndIndependentPane.oneTouchExpandable = true
        splitBetweenModelPaneAndIndependentPane.dividerSize = 10
        splitBetweenModelPaneAndIndependentPane.dividerLocation = 500
        splitPane.rightComponent = splitBetweenModelPaneAndIndependentPane

        ULCBoxPane selectionSwitchPane = new ULCBoxPane(1, 3)
        navigationSplitPaneAction = new ToggleSplitPaneAction(splitPane, UIUtils.getText(this.class, "Navigation"))
        ULCVerticalToggleButton navigationSwitchButton = new ULCVerticalToggleButton(navigationSplitPaneAction)
        navigationSwitchButton.selected = true
        selectionSwitchPane.add(BOX_LEFT_TOP, navigationSwitchButton);

        validationSplitPaneAction = new CommentsSwitchAction(riskAnalyticsMainModel, UIUtils.getText(this.class, "ValidationsAndComments"), false)
        ULCVerticalToggleButton validationSwitchButton = new ULCVerticalToggleButton(validationSplitPaneAction)
        validationSwitchButton.selected = false
        validationSwitchButton.enabled = false
        riskAnalyticsMainModel.switchActions << validationSwitchButton
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
            content.addCard(key, value.createComponent(applicationContext))
            headerView.addWindowMenuEntry(key, content, false)
        }
        headerView.windowMenu.addSeparator()
        content.selectedName = DEFAULT_CARD_NAME
    }

    void attachListeners() {
        content.registerKeyboardAction(navigationSplitPaneAction, KeyStroke.getKeyStroke(VK_N, CTRL_DOWN_MASK + SHIFT_DOWN_MASK), WHEN_IN_FOCUSED_WINDOW)
        content.registerKeyboardAction(validationSplitPaneAction, KeyStroke.getKeyStroke(VK_V, CTRL_DOWN_MASK + SHIFT_DOWN_MASK), WHEN_IN_FOCUSED_WINDOW)
        content.registerKeyboardAction(modelIndependentSplitPaneAction, KeyStroke.getKeyStroke(VK_Q, CTRL_DOWN_MASK + SHIFT_DOWN_MASK), WHEN_IN_FOCUSED_WINDOW)
        riskAnalyticsMainModel.addModelListener(this)
        riskAnalyticsMainModel.addPropertyChangeListener(CURRENT_ITEM_PROPERTY, this)
        headerView.navigationBarTopPane.addFilterChangedListener([filterChanged: { FilterDefinition filter ->
            selectionTreeView.filterTree(filter)
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
        AbstractUIItem abstractUIItem = TableTreeBuilderUtils.findUIItemForItem(selectionTreeView.root, item)
        if (!abstractUIItem) {
            LOG.error " AbstractUIItem (${item.name}) table tree node not found "
            abstractUIItem = UIItemFactory.createItem(item, model, riskAnalyticsMainModel)
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

    void setWindowTitle(AbstractUIItem abstractUIItem) {
        ULCFrame window = UlcUtilities.getWindowAncestor(this.content) as ULCFrame
        window.title = "Risk Analytics - ${abstractUIItem ? abstractUIItem.windowTitle : ''}"
    }

    ULCMenuBar getMenuBar() {
        return headerView.menuBar
    }

    void itemChanged(ModellingItem item) {
        if (item == riskAnalyticsMainModel?.currentItem?.item) {
            headerView.syncMenuBar()
        }
    }

    void itemSaved(ModellingItem item) {
    }

    void propertyChange(PropertyChangeEvent evt) {
        if (evt.source == riskAnalyticsMainModel && evt.propertyName == CURRENT_ITEM_PROPERTY) {
            headerView.syncMenuBar()
        }
    }
}
