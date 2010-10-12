package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.ULCWindow
import com.ulcjava.base.application.ULCDialog
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCButton
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.core.remoting.ITransactionService
import org.pillarone.riskanalytics.core.remoting.impl.RemotingUtils
import org.pillarone.riskanalytics.core.remoting.TransactionInfo
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.LocaleResources


class DealLinkDialog {

    private static class DealComboBoxModel extends DefaultComboBoxModel {

        private Map<String, Long> items = [:]

        public DealComboBoxModel() {
            ITransactionService transactionService = RemotingUtils.getTransactionService()
            for (TransactionInfo info in transactionService.allTransactions) {
                addElement(info.name)
                items.put(info.name, info.dealId)
            }
        }

        public long getDealId() {
            return items[getSelectedItem()]
        }
    }


    private ULCWindow parent
    private ULCDialog dialog
    private ULCComboBox dealSelection
    DealComboBoxModel dealSelectionModel
    private ULCButton okButton
    private ULCButton cancelButton

    Closure okAction

    String title

    public DealLinkDialog(ULCWindow parent) {
        this.parent = parent
        initComponents()
        layoutComponents()
        attachListeners()
        title = "Choose deal"
    }

    private void initComponents() {
        dialog = new ULCDialog(parent, true)
        dialog.name = 'dealDialog'
        dealSelectionModel = new DealComboBoxModel()
        dealSelection = new ULCComboBox(dealSelectionModel)
        dealSelection.name = 'dealSelection'
        okButton = new ULCButton(getText("okButton"))
        okButton.name = 'okButton'
        cancelButton = new ULCButton(getText("cancelButton"))

    }

    private void layoutComponents() {
        dealSelection.setPreferredSize(new Dimension(200, 20))
        ULCBoxPane content = new ULCBoxPane(rows: 2, columns: 4)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("Deal") + ":"))
        content.add(3, ULCBoxPane.BOX_EXPAND_CENTER, dealSelection)
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
            okAction.call()
            hide()
        }] as IActionListener

        okButton.addActionListener(action)
        cancelButton.addActionListener([actionPerformed: {e -> hide()}] as IActionListener)
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
    protected String getText(String key) {
        return LocaleResources.getString("DealLinkDialog." + key);
    }
}

