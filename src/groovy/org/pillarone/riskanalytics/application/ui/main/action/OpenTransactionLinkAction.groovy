package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.remoting.TransactionInfo
import org.pillarone.riskanalytics.core.remoting.impl.RemotingUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCAlert


class OpenTransactionLinkAction extends SelectionTreeAction {
    private TransactionInfo[] transactionInfos;
    static Log LOG = LogFactory.getLog(OpenTransactionLinkAction)

    /**
     * Action is only enabled if
     * @return
     */
    public boolean isEnabled() {
        //Only enable on single select
        if (getSelectedObjects(Parameterization.class).size() != 1) {
            return false
        }
        try {
            Parameterization param = getSelectedItem()
            //Enable only if dealId exists..
            return param.getDealId() != null
        } catch (Exception e) {
            LOG.error("OpenTransactionLinkAction error, could not set enabled", e)
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

    public OpenTransactionLinkAction(ULCTableTree tree, RiskAnalyticsMainModel model) {
        super("OpenExternal", tree, model)
    }

    //Get the transactionURL for a specific dealId from the TransactionService
    private String getTransactionURL(Long dealId) {
        try {
            if (transactionInfos == null) {
                transactionInfos = RemotingUtils.getTransactionService().allTransactions
            }
            TransactionInfo transactionInfo = transactionInfos.find {it.dealId == dealId}
            if (transactionInfo) {
                return transactionInfo.getTransactionUrl()
            }
        } catch (Exception ex) {
            LOG.error("Could not get external url for deal id :" + dealId, ex)
        }
        return null
    }
}
