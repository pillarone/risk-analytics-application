package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNode
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.remoting.TransactionInfo
import org.pillarone.riskanalytics.core.remoting.impl.RemotingUtils
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.core.parameter.comment.ParameterizationCommentDAO
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.parameter.comment.workflow.WorkflowCommentDAO

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public enum EnumModellingTableTreeColumn {

    NAME {
        @Override

        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return node.abstractUIItem.item.name
        }

        @Override
        int getColumnIndex() {
            return 0
        }

    },
    STATE {

        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            if (modellingItem instanceof Parameterization)
                return modellingItem?.status?.getDisplayName()
            return ""
        }

        @Override
        int getColumnIndex() {
            return 1
        }
    },
    TAGS {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            if (!(modellingItem instanceof ResultConfiguration))
                return modellingItem?.tags?.join(",")
            return ""
        }

        @Override
        int getColumnIndex() {
            return 2
        }
    },
    TRANSACTION_NAME {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            if ((modellingItem instanceof Parameterization) && modellingItem.dealId) {
                return getTransactionName(modellingItem.dealId)
            }
            return null
        }

        @Override
        int getColumnIndex() {
            return 3
        }
    },
    QUARTER {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            if ((modellingItem instanceof Parameterization) && modellingItem.dealId && modellingItem.valuationDate) {
                DateFormatUtils.formatDetailed(modellingItem.valuationDate)
            };
            return ""
        }

        @Override
        int getColumnIndex() {
            return 4
        }
    },
    COMMENTS {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            if (modellingItem instanceof Parameterization) return modellingItem.getSize(ParameterizationCommentDAO)
            if (modellingItem instanceof Simulation) return modellingItem.getSize(SimulationRun)
            return 0
        }

        @Override
        int getColumnIndex() {
            return 5
        }
    },
    REVIEW_COMMENT {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            if (modellingItem instanceof Parameterization)
                return modellingItem.getSize(WorkflowCommentDAO)
            return 0
        }

        @Override
        int getColumnIndex() {
            return 6
        }
    },
    OWNER {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return modellingItem?.getCreator()?.username
        }

        @Override
        int getColumnIndex() {
            return 7
        }
    },
    LAST_UPDATER {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return modellingItem?.getLastUpdater()?.username
        }

        @Override
        int getColumnIndex() {
            return 8
        }
    },
    CREATION_DATE {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return DateFormatUtils.formatDetailed(modellingItem.creationDate)
        }

        @Override
        int getColumnIndex() {
            return 9
        }
    },
    LAST_MODIFICATION_DATE {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return DateFormatUtils.formatDetailed(modellingItem.modificationDate)
        }

        @Override
        int getColumnIndex() {
            return 10
        }
    },
    ASSIGNED_TO {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return "---"
        }

        @Override
        int getColumnIndex() {
            return 11
        }
    },
    VISIBILITY {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return "---"
        }

        @Override
        int getColumnIndex() {
            return 12
        }
    },
    UNKNOWN {
        @Override
        Object getValue(ModellingItem modellingItem, ItemNode node) {
            return ""
        }

        @Override
        int getColumnIndex() {
            return -1
        }
    }

    List<TransactionInfo> transactionInfos

    public Object getValue(ModellingItem modellingItem, ItemNode node) {
        return null
    }

    public int getColumnIndex() {
        return 0
    }


    public static EnumModellingTableTreeColumn getEnumModellingTableTreeColumnFor(int desired) {
        for (EnumModellingTableTreeColumn status: values()) {
            if (desired == status.getColumnIndex()) {
                return status;
            }
        }
        return UNKNOWN;
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

}