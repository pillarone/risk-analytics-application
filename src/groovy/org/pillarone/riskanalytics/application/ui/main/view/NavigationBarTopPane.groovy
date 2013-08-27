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
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.FilterDefinition
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.comment.action.TextFieldFocusListener
import org.pillarone.riskanalytics.application.ui.util.UIUtils

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
    ModellingInformationTableTreeModel tableTreeModel
    Log LOG = LogFactory.getLog(NavigationBarTopPane)

    private List<IFilterChangedListener> filterChangedListeners = []

    public NavigationBarTopPane(ULCToolBar toolBar, ModellingInformationTableTreeModel tableTreeModel) {
        this.toolBar = toolBar
        this.tableTreeModel = tableTreeModel
    }

    void addFilterChangedListener(IFilterChangedListener listener) {
        filterChangedListeners << listener
    }

    void removeFilterChangedListener(IFilterChangedListener listener) {
        filterChangedListeners.remove(listener)
    }

    void fireFilterChanged(FilterDefinition newFilter) {
        filterChangedListeners*.filterChanged(newFilter)
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
    }

    protected void attachListeners() {
        myStuffButton.addActionListener([actionPerformed: { ActionEvent event ->
            FilterDefinition filter = tableTreeModel.currentFilter
            filter.ownerFilter.active = myStuffButton.isSelected()
            fireFilterChanged(filter)
        }] as IActionListener)
        searchTextField.addFocusListener(new TextFieldFocusListener(searchTextField))
        Closure searchClosure = {
            String text = searchTextField.getText()
            FilterDefinition filter = tableTreeModel.currentFilter
            filter.allFieldsFilter.query = text
            fireFilterChanged(filter)
        }
        IActionListener action = [actionPerformed: { ActionEvent e -> searchClosure.call() }] as IActionListener
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        searchTextField.registerKeyboardAction(action, enter, ULCComponent.WHEN_FOCUSED);
        clearButton.addActionListener([actionPerformed: { ActionEvent event ->
            searchTextField.setText(UIUtils.getText(this.class, "searchText"))
            searchTextField.setForeground Color.gray
            FilterDefinition filter = tableTreeModel.currentFilter
            filter.allFieldsFilter.query = ""
            filter.ownerFilter.active = false
            fireFilterChanged(filter)
            myStuffButton.setSelected false
            assignedToMeButton.setSelected false
        }] as IActionListener)
    }

    private static String getLoggedUser() {
        return UserContext.getCurrentUser()?.getUsername()
    }

}
