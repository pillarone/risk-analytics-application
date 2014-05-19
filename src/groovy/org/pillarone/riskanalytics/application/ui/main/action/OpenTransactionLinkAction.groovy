package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.remoting.TransactionInfo
import org.pillarone.riskanalytics.core.remoting.impl.RemotingUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class OpenTransactionLinkAction extends SelectionTreeAction {
    private TransactionInfo[] transactionInfos;
    static Log LOG = LogFactory.getLog(OpenTransactionLinkAction)

    /**
     * Action is only enabled on single selection on a Parameterization
     * @return
     */
    boolean isEnabled() {
        if (getSelectedUIItems().size() != 1) {
            return false
        }
        Object selectedItem = getSelectedItem()
        if (selectedItem instanceof Parameterization) {
            Long dealId = selectedItem.getDealId()
            return dealId && dealId > 0
        } else {
            return false
        }
    }


    @Override
    void doActionPerformed(ActionEvent event) {
        try {
            Parameterization parameterization = getSelectedItem()
            String url = getTransactionURL(parameterization.getDealId())
            if (url) {
                LOG.debug("Opening external transaction at " + url)
                ClientContext.showDocument(url)
            } else {
                showNoUrlAlert(parameterization)
            }
        } catch (Exception e) {
            LOG.error("Could not get external URL for selected item", e)
        }
    }

    private void showNoUrlAlert(Parameterization parameterization) {
        ULCAlert alert = new ULCAlert("Could not open external link", "<HTML>No external url found for <B>" + parameterization.getName() + "</B> with dealId <B>" + parameterization.getDealId() + "</B></HTML>", "Ok")
        alert.setMessageType(ULCAlert.INFORMATION_MESSAGE)
        alert.show()
    }

    public OpenTransactionLinkAction(ULCTableTree tree) {
        super("OpenExternal", tree)
    }

    //Get the transactionURL for a specific dealId from the TransactionService
    private String getTransactionURL(Long dealId) {
        try {
            if (transactionInfos == null) {
                transactionInfos = RemotingUtils.allTransactions
            }
            TransactionInfo transactionInfo = transactionInfos.find { it.dealId == dealId }
            if (transactionInfo) {
                return transactionInfo.getTransactionUrl()
            }
        } catch (Exception ex) {
            LOG.error("Could not get external url for deal id :" + dealId, ex)
        }
        return null
    }
}
