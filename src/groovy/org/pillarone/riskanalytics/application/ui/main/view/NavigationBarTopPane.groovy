package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.search.ModellingItemSearchService
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.ModellingItemNodeFilter
import org.pillarone.riskanalytics.application.ui.base.model.MultiFilteringTableTreeModel
import org.pillarone.riskanalytics.application.ui.comment.action.TextFieldFocusListener
import org.pillarone.riskanalytics.application.ui.main.model.ModellingItemSearchBean
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
class NavigationBarTopPane {
    ULCToolBar toolBar
    ULCToggleButton myStuffButton
    ULCToggleButton assignedToMeButton
    ULCTextField searchTextField
    ULCButton clearButton
    ULCLabel noResults
    ModellingItemSearchBean searchBean
    ModellingItemSearchService modellingItemSearchService = ModellingItemSearchService.getInstance()
    MultiFilteringTableTreeModel tableTreeModel
    Log LOG = LogFactory.getLog(NavigationBarTopPane)

    public NavigationBarTopPane(ULCToolBar toolBar, MultiFilteringTableTreeModel tableTreeModel) {
        this.toolBar = toolBar
        this.searchBean = new ModellingItemSearchBean()
        this.tableTreeModel = tableTreeModel
    }

    public void init() {
        initComponents()
        layoutComponents()
        attachListeners()
    }


    protected void initComponents() {
        myStuffButton = new ULCToggleButton(UIUtils.getText(this.class, "MyStuff"))
        myStuffButton.name = "myStuffButton"
        myStuffButton.setPreferredSize new Dimension(100, 20)
        myStuffButton.setSelected(false)
        assignedToMeButton = new ULCToggleButton(UIUtils.getText(this.class, "assignedToMe"))
        assignedToMeButton.setPreferredSize new Dimension(100, 20)
        assignedToMeButton.setSelected(false)
        assignedToMeButton.setEnabled(false)

        searchTextField = new ULCTextField(name: "searchText")
        searchTextField.setMaximumSize(new Dimension(300, 20))
        searchTextField.setToolTipText UIUtils.getText(this.class, "searchText")
        searchTextField.setText(UIUtils.getText(this.class, "searchText"))
        searchTextField.setForeground(Color.gray)
        searchTextField.setPreferredSize(new Dimension(250, 20))

        clearButton = new ULCButton(UIUtils.getIcon("delete-active.png"))
        clearButton.name = "clearButton"
        clearButton.setToolTipText UIUtils.getText(this.class, "clear")

        noResults = new ULCLabel("")
        noResults.setForeground Color.red
    }

    protected void layoutComponents() {
        if (UserContext.hasCurrentUser()) {
            toolBar.add(myStuffButton);
            toolBar.addSeparator()
            toolBar.add(assignedToMeButton);
            toolBar.addSeparator()
        }
        toolBar.add(searchTextField);
        toolBar.add(clearButton)
        toolBar.add(noResults);
    }

    protected void attachListeners() {
        myStuffButton.addActionListener([actionPerformed: {ActionEvent event ->
            ModellingItemNodeFilter filter
            if (loggedUser && myStuffButton.isSelected()) {
                filter = new ModellingItemNodeFilter([loggedUser], ModellingInformationTableTreeModel.OWNER)
            } else {
                filter = new ModellingItemNodeFilter([], ModellingInformationTableTreeModel.OWNER)
            }
            tableTreeModel?.applyFilter(filter)
        }] as IActionListener)
        searchTextField.addFocusListener(new TextFieldFocusListener(searchTextField))
        Closure searchClosure = {
            String text = searchTextField.getText()
            if (text) {
                List results
                try {
                    results  = modellingItemSearchService.search("* $text*").collect{ModellingItem item -> item.nameAndVersion}
                } catch (Exception ex) {
                    LOG.error "${ex}"
                    results = []
                }
                boolean isNoResult = false
                if (results.size() == 0) {
                    results = ["no_result_found"]
                    isNoResult = true
                }
                ModellingItemNodeFilter filter = new ModellingItemNodeFilter(results, ModellingInformationTableTreeModel.NAME)

                tableTreeModel.applyFilter(filter)
                noResults.setText(isNoResult ? UIUtils.getText(this.class, "noResults") : "")
            }
        }
        IActionListener action = [actionPerformed: {ActionEvent e -> searchClosure.call()}] as IActionListener
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        searchTextField.registerKeyboardAction(action, enter, ULCComponent.WHEN_FOCUSED);
        clearButton.addActionListener([actionPerformed: {ActionEvent event ->
            searchTextField.setText(UIUtils.getText(this.class, "searchText"))
            searchTextField.setForeground Color.gray
            ModellingItemNodeFilter filter = new ModellingItemNodeFilter([], ModellingInformationTableTreeModel.NAME)
            tableTreeModel?.applyFilter(filter)
            noResults.setText("")
            myStuffButton.setSelected false
            assignedToMeButton.setSelected false
        }] as IActionListener)
    }

    private static String getLoggedUser() {
        return UserContext.getCurrentUser()?.getUsername()
    }

}
