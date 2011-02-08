package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class NavigationBarTopPane {
    ULCToolBar toolBar
    ULCToggleButton myStuffButton
    ULCToggleButton assignedToMeButton
    ULCLabel filterLabel
    ULCComboBox filterComboBox
    ULCTextField searchTextField
    DefaultComboBoxModel filterComboBoxModel

    public NavigationBarTopPane(ULCToolBar toolBar) {
        this.toolBar = toolBar
    }

    public void init() {
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void initComponents() {
        myStuffButton = new ULCToggleButton("My Staff")
        myStuffButton.setPreferredSize new Dimension(100, 20)
        myStuffButton.setSelected(false)
        assignedToMeButton = new ULCToggleButton("Assigned To me")
        assignedToMeButton.setPreferredSize new Dimension(100, 20)
        assignedToMeButton.setSelected(false)
        filterLabel = new ULCLabel("State Filter")
        filterComboBoxModel = new DefaultComboBoxModel(["All"])
        filterComboBox = new ULCComboBox(filterComboBoxModel)
        filterComboBox.setPreferredSize new Dimension(100, 20)
        filterComboBox.setMaximumSize new Dimension(100, 20)
        searchTextField = new ULCTextField(name: "searchText")
        searchTextField.setMaximumSize(new Dimension(180, 20))
        searchTextField.setToolTipText "Search Parameterization..."//UIUtils.getText(this.class, "initialText")
        searchTextField.setText("Search Parameterization...")//UIUtils.getText(this.class, "initialText"))
        searchTextField.setForeground(Color.gray)
        searchTextField.setPreferredSize(new Dimension(200, 20))
    }

    protected void layoutComponents() {
        toolBar.add(myStuffButton);
        toolBar.add(assignedToMeButton);
        toolBar.add(filterLabel);
        toolBar.add(filterComboBox);
        toolBar.add(searchTextField);
    }

    protected void attachListeners() {

    }
}
