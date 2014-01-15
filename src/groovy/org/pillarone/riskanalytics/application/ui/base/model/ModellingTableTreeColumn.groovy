package org.pillarone.riskanalytics.application.ui.base.model

import org.apache.commons.lang.ObjectUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.remoting.TransactionInfo
import org.pillarone.riskanalytics.core.remoting.impl.RemotingUtils
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public class ModellingTableTreeColumn {
    //Can disable optimisation by setting -DskipNameLookupForNegativeDealIds=false
    //
    private static boolean skipNameLookupForNegativeDealIds =
        System.getProperty("skipNameLookupForNegativeDealIds","true").equalsIgnoreCase("true");

    protected static Log LOG = LogFactory.getLog(ModellingTableTreeColumn)

    List<TransactionInfo> transactionInfos
    DateTimeFormatter simpleDateFormat = DateTimeFormat.forPattern("dd.MM.yyyy, HH:mm")
    Map instances

    class NameColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem) {
            return modellingItem.getNameAndVersion()
        }

        @Override
        Object getValue(Model model) {
            return model.name
        }
    }

    class StateColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem) {
            if (modellingItem instanceof Parameterization)
                return modellingItem?.status?.getDisplayName()
            return ""
        }
    }

    class TagsColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem) {
            if (!(modellingItem instanceof ResultConfiguration))
                return modellingItem?.tags?.join(",")
            return ""
        }

    }

    class TransactionColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem) {//PMO-2680
            if ((modellingItem instanceof Parameterization) && modellingItem.dealId) {
                return getTransactionName(modellingItem.dealId)
            }
            return null
        }

    }

    class OwnerColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem) {
            return modellingItem?.creator?.username
        }
    }

    class LastUpdateColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem) {
            return modellingItem?.lastUpdater?.username
        }
    }

    class CreationDateColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem) {
            return DateFormatUtils.formatDetailed(modellingItem.creationDate)
        }
    }

    class LastModificationDateColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem) {
            return DateFormatUtils.formatDetailed(modellingItem.modificationDate)
        }
    }

    Object getValue(ModellingItem modellingItem) {
        return null
    }

    Object getValue(Model model) {
        return null
    }

    public ModellingTableTreeColumn getEnumModellingTableTreeColumnFor(int desired) {
        getInstances().get(desired)
    }

    private String getTransactionName(Long dealId) {
        if(dealId == null){
            LOG.warn("getTransactionName() called with null dealId")
            return ""
        }
        if(skipNameLookupForNegativeDealIds && dealId.longValue() < 0){
            return ""
        }
        try {
            //Note: Once list is built, adding new deals will require Application restart.
            if (transactionInfos == null) {
                transactionInfos = RemotingUtils.allTransactions
            }
            //Note: Potential optimisation: A (dealId -> TransactionInfo) lookup table
            TransactionInfo transactionInfo = transactionInfos.find {it.dealId == dealId}
            if (transactionInfo){
                return transactionInfo.getName()
            }
        } catch (Exception ex) {
            LOG.warn("Exception looking up transaction for dealId:"+dealId,ex)
            return ""+dealId
        }
        return ""
    }

    private Map getInstances() {
        if (!instances) {
            instances = [:]
            instances[ModellingInformationTableTreeModel.NAME] = new NameColumn()
            instances[ModellingInformationTableTreeModel.STATE] = new StateColumn()
            instances[ModellingInformationTableTreeModel.TAGS] = new TagsColumn()
            instances[ModellingInformationTableTreeModel.TRANSACTION_NAME] = new TransactionColumn()
            instances[ModellingInformationTableTreeModel.OWNER] = new OwnerColumn()
            instances[ModellingInformationTableTreeModel.LAST_UPDATER] = new LastUpdateColumn()
            instances[ModellingInformationTableTreeModel.CREATION_DATE] = new CreationDateColumn()
            instances[ModellingInformationTableTreeModel.LAST_MODIFICATION_DATE] = new LastModificationDateColumn()

        }
        return instances
    }

}