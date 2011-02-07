package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.ULCToggleButton
import com.ulcjava.base.application.ULCToolBar
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.base.model.ParameterizationNodeFilter
import org.pillarone.riskanalytics.application.ui.base.model.ParameterizationNodeFilterFactory
import org.pillarone.riskanalytics.application.ui.comment.action.TextFieldFocusListener
import org.pillarone.riskanalytics.application.ui.main.model.ModellingItemSearchBean

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NavigationBarTopPane {
    ULCToolBar toolBar
    ULCToggleButton myStuffButton
    ULCToggleButton assignedToMeButton
    ULCTextField searchTextField
    ModellingItemSearchBean searchBean
    AbstractTableTreeModel tableTreeModel

    public NavigationBarTopPane(ULCToolBar toolBar, AbstractTableTreeModel tableTreeModel) {
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
        myStuffButton = new ULCToggleButton("My stuff")
        myStuffButton.setPreferredSize new Dimension(100, 20)
        myStuffButton.setSelected(false)
        assignedToMeButton = new ULCToggleButton("Assigned To me")
        assignedToMeButton.setPreferredSize new Dimension(100, 20)
        assignedToMeButton.setSelected(false)

        searchTextField = new ULCTextField(name: "searchText")
        searchTextField.setMaximumSize(new Dimension(180, 20))
        searchTextField.setToolTipText "Search Parameterization..."
        searchTextField.setText("Search parameterization...")
        searchTextField.setForeground(Color.gray)
        searchTextField.setPreferredSize(new Dimension(200, 20))
    }

    protected void layoutComponents() {
        toolBar.add(myStuffButton);
        toolBar.add(assignedToMeButton);
        toolBar.add(searchTextField);
    }

    protected void attachListeners() {
        searchTextField.addFocusListener(new TextFieldFocusListener(searchTextField))
        Closure searchClosure = {ActionEvent event ->
            String text = searchTextField.getText()
            if (text) {
                List<String> results = searchBean.performSearch(text)
                println "ui result ${results}"
                ParameterizationNodeFilter filter = ParameterizationNodeFilterFactory.getParameterizationNodeFilter(results)
//                if (filter) {
                println "apply a filter"
                tableTreeModel.filters.clear()
                tableTreeModel.addFilter(filter)
                tableTreeModel.applyFilter()
//                }
            }
        }
        IActionListener action = [actionPerformed: {e -> searchClosure.call()}] as IActionListener
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        searchTextField.registerKeyboardAction(action, enter, ULCComponent.WHEN_FOCUSED);
    }
}
