package org.pillarone.riskanalytics.application.ui.main.action.workflow

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.remoting.ITransactionService
import org.pillarone.riskanalytics.core.remoting.TransactionInfo
import org.pillarone.riskanalytics.core.remoting.impl.RemotingUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import com.ulcjava.base.application.*
import org.joda.time.DateTime

class DealLinkDialog {
    ITransactionService transactionService

    private static class DealComboBoxModel extends DefaultComboBoxModel {

        private Map<String, Long> items = [:]

        public DealComboBoxModel() {
            String none = UIUtils.getText(DealLinkDialog.class, "none")
            addElement(none)
            items.put(none, null)
            for (TransactionInfo info in getAllTransactions()) {
                addElement(info.name)
                items.put(info.name, info.dealId)
            }
        }

        public Long getDealId() {
            return items[getSelectedItem()]
        }
    }


    private ULCWindow parent
    private ULCDialog dialog
    private ULCComboBox dealSelection
    DealComboBoxModel dealSelectionModel
    private ULCButton okButton
    private ULCButton cancelButton
    private ValuationDatePane valuationDatePane
    ValuationDatePaneModel valuationDatePaneModel

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
        valuationDatePaneModel = new ValuationDatePaneModel()
        valuationDatePane = new ValuationDatePane(valuationDatePaneModel)

    }

    private void layoutComponents() {
        dealSelection.setPreferredSize(new Dimension(200, 20))
        ULCBoxPane content = new ULCBoxPane(rows: 2, columns: 4)
        content.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("Deal") + ":"))
        content.add(3, ULCBoxPane.BOX_EXPAND_CENTER, dealSelection)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("Date") + ":"))
        content.add(3, ULCBoxPane.BOX_EXPAND_CENTER, valuationDatePane.content)
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

    public void selectDeal(Parameterization parameterization) {
        if (!parameterization.isLoaded()) {
            parameterization.load()
        }
        if (parameterization.valuationDate != null) {
            valuationDatePaneModel.setDate(new DateTime(parameterization.valuationDate))
        }
        if (parameterization.dealId == null) return

        for (TransactionInfo info in getAllTransactions()) {
            if (info.dealId == parameterization.dealId) {
                dealSelection.setSelectedItem(info.name)
            }
        }
    }

    private static List<TransactionInfo> getAllTransactions() {
        ITransactionService transactionService = RemotingUtils.getTransactionService()
        return transactionService.allTransactions
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

