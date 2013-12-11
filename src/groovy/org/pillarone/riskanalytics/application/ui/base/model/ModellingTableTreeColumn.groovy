package org.pillarone.riskanalytics.application.ui.base.model

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
        Object getValue(ModellingItem modellingItem, ItemNode node) {
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
        try {
            if (transactionInfos == null) {
                transactionInfos = RemotingUtils.allTransactions
            }
            TransactionInfo transactionInfo = transactionInfos.find {it.dealId == dealId}
            if (transactionInfo)
                return transactionInfo.getName()
        } catch (Exception ex) {
            if (dealId)
                return String.valueOf(dealId)
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