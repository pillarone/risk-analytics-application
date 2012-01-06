package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.remoting.TransactionInfo
import org.pillarone.riskanalytics.core.remoting.impl.RemotingUtils
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.core.parameter.comment.ParameterizationCommentDAO
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.parameter.comment.workflow.WorkflowCommentDAO
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
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return node.abstractUIItem.getNameAndVersion()
        }
    }

    class StateColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            if (modellingItem instanceof Parameterization)
                return modellingItem?.status?.getDisplayName()
            return ""
        }
    }

    class TagsColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
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

    class QuarterColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            if ((modellingItem instanceof Parameterization) && modellingItem.dealId && modellingItem.valuationDate) {
                return DateFormatUtils.formatDetailed(modellingItem.valuationDate)
            };
            return ""
        }

    }

    class CommentsColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            int count = 0
            if (modellingItem instanceof Parameterization) count = modellingItem.getSize(ParameterizationCommentDAO)
            if (modellingItem instanceof Simulation) count = modellingItem.getSize(SimulationRun)
            return count
        }
    }

    class ReviewCommentsColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            if (modellingItem instanceof Parameterization)
                return modellingItem.getSize(WorkflowCommentDAO)
            return 0
        }
    }

    class OwnerColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return modellingItem?.getCreator()?.username
        }
    }

    class LastUpdateColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return modellingItem?.getLastUpdater()?.username
        }
    }

    class CreationDateColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return DateFormatUtils.formatDetailed(modellingItem.creationDate)
        }
    }

    class LastModificationDateColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return DateFormatUtils.formatDetailed(modellingItem.modificationDate)
        }
    }

    class AssignedToColumn extends ModellingTableTreeColumn {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return "---"
        }

    }

    class VisibilityColumn extends ModellingTableTreeColumn {

        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return "---"
        }

    }

    class UknownColumn extends ModellingTableTreeColumn {
        
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return ""
        }

    }

    Object getValue(ModellingItem modellingItem, ItemNode node) {
        return null
    }

    public ModellingTableTreeColumn getEnumModellingTableTreeColumnFor(int desired) {
        getInstances().get(desired)
    }

    private String getTransactionName(Long dealId) {
        try {
            if (transactionInfos == null) {
                transactionInfos = RemotingUtils.getTransactionService().allTransactions
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

    Map getInstances() {
        if (!instances) {
            instances = [:]
            instances[ModellingInformationTableTreeModel.NAME] = new NameColumn()
            instances[ModellingInformationTableTreeModel.STATE] = new StateColumn()
            instances[ModellingInformationTableTreeModel.TAGS] = new TagsColumn()
            instances[ModellingInformationTableTreeModel.TRANSACTION_NAME] = new TransactionColumn()
            instances[ModellingInformationTableTreeModel.QUARTER] = new QuarterColumn()
            instances[ModellingInformationTableTreeModel.COMMENTS] = new CommentsColumn()
            instances[ModellingInformationTableTreeModel.REVIEW_COMMENT] = new ReviewCommentsColumn()
            instances[ModellingInformationTableTreeModel.OWNER] = new OwnerColumn()
            instances[ModellingInformationTableTreeModel.LAST_UPDATER] = new LastUpdateColumn()
            instances[ModellingInformationTableTreeModel.CREATION_DATE] = new CreationDateColumn()
            instances[ModellingInformationTableTreeModel.LAST_MODIFICATION_DATE] = new LastModificationDateColumn()
            instances[ModellingInformationTableTreeModel.ASSIGNED_TO] = new AssignedToColumn()
            instances[ModellingInformationTableTreeModel.VISIBILITY] = new VisibilityColumn()
            instances[-1] = new UknownColumn()

        }
        return instances
    }

}