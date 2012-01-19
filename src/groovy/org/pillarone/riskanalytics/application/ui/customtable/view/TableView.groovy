package org.pillarone.riskanalytics.application.ui.customtable.view

import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.DefaultComboBoxModel
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCDialog
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.output.CustomTableDAO
import org.pillarone.riskanalytics.application.ui.main.view.AbstractView
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.application.output.result.item.CustomTable
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.customtable.model.DataCellElement

class TableView extends AbstractView {

    ULCBoxPane content
    ULCComboBox savedViews
    ULCButton loadButton
    ULCButton saveButton

    CustomTableView customTableView

    Model model
    Simulation simulation

    TableView(Model model, Simulation simulation) {
        this.simulation = simulation
        this.model = model
        init()
    }



    @Override
    protected void attachListeners() {
        saveButton.addActionListener([actionPerformed: {event ->
            SaveDialog dialog = new SaveDialog(UlcUtilities.getWindowAncestor(content), model)
            dialog.okAction = {
                CustomTable customTable = new CustomTable(dialog.nameInput.text, model.class)
                customTable.tableData = customTableView.customTableModel.data
                savedViews.addItem(customTable.name)
                customTable.save()
            }
            dialog.show()
        }] as IActionListener)

        loadButton.addActionListener([actionPerformed: {
            content.remove(customTableView.content)

            CustomTable table = new CustomTable(savedViews.selectedItem.toString(), model.class)
            table.load()
            final List<DataCellElement> outputElements = table.tableData.flatten().findAll { it instanceof DataCellElement}
            outputElements*.run = simulation.simulationRun

            customTableView = new CustomTableView(table.tableData)

            outputElements*.update(customTableView.customTable.customTableModel)

            content.add(3, ULCBoxPane.BOX_EXPAND_EXPAND, customTableView.content)

        }] as IActionListener)

    }

    @Override
    protected void initComponents() {
        content = new ULCBoxPane(3,2)
        savedViews = new ULCComboBox(new DefaultComboBoxModel(ModellingItemFactory.getCustomTablesForModel(model.class)*.name))
        loadButton = new ULCButton("Load")
        saveButton = new ULCButton("Save")

        customTableView = new CustomTableView()
    }

    @Override
    protected void layoutComponents() {
        content.add(ULCBoxPane.BOX_LEFT_CENTER, savedViews)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, loadButton)
        content.add(ULCBoxPane.BOX_RIGHT_CENTER, saveButton)
        content.add(3, ULCBoxPane.BOX_EXPAND_EXPAND, customTableView.content)

    }

    private static class SaveDialog {

        private ULCWindow parent
        private ULCDialog dialog
        ULCTextField nameInput
        private ULCButton okButton
        private ULCButton cancelButton
        private Model model

        Closure okAction
        String title

        SaveDialog(ULCWindow parent, Model model) {
            this.parent = parent
            this.model = model
            initComponents()
            layoutComponents()
            attachListeners()
        }

        private void initComponents() {
            dialog = new ULCDialog(parent, true)
            dialog.name = 'saveTableDialog'
            nameInput = new ULCTextField()
            nameInput.name = 'newName'
            okButton = new ULCButton(getText("okButton"))
            okButton.name = 'okButton'
            cancelButton = new ULCButton(getText("cancelButton"))

        }

        private void layoutComponents() {
            nameInput.setPreferredSize(new Dimension(200, 20))
            ULCBoxPane content = new ULCBoxPane(rows: 2, columns: 4)
            content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("name") + ":"))
            content.add(3, ULCBoxPane.BOX_EXPAND_CENTER, nameInput)
            content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
            content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, new ULCFiller())
            okButton.setPreferredSize(new Dimension(120, 20))
            content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, okButton)
            cancelButton.setPreferredSize(new Dimension(120, 20))
            content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, cancelButton)

            dialog.add(content)
            dialog.setLocationRelativeTo(parent)
            dialog.pack()
            dialog.resizable = false

        }

        private void attachListeners() {
            IActionListener action = [actionPerformed: {e ->
                if (isUnique()) {
                    okAction.call(); hide()
                } else {
                    I18NAlert alert = new I18NAlert(parent, "UniquesNamesRequired")
                    alert.show()
                }
            }] as IActionListener

            nameInput.addActionListener(action)
            okButton.addActionListener(action)
            cancelButton.addActionListener([actionPerformed: {e -> hide()}] as IActionListener)
        }

        protected boolean isUnique() {
            CustomTableDAO.findByNameAndModelClassName(nameInput.text, model.class.name) == null
        }

        public void show() {
            dialog.title = title
            dialog.visible = true
        }

        public hide() {
            dialog.visible = false
        }

        /**
         * Utility method to get resource bundle entries for this class
         *
         * @param key
         * @return the localized value corresponding to the key
         */
        public String getText(String key) {
            return LocaleResources.getString("NodeNameDialog." + key);
        }
    }


}
