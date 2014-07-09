package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationClassifierTableTreeNode

public class TableTreeMutator {
    protected static final Log LOG = LogFactory.getLog(TableTreeMutator)

    ITableTreeModel model
    boolean structureChangedAllowed = false

    public TableTreeMutator(model) {
        this.model = model;
    }


    void applyChanges(List nodes, List tableData, int columnOffset = 0) {
        int lastProcessedLine = 0
        List backUp = []

        try {

            tableData.eachWithIndex { List lineData, int lineIndex ->
                lineData.eachWithIndex { def value, int colIndex ->
                    Object node = nodes[lineIndex]
                    int col = columnOffset + colIndex
                    Object oldValue = model.getValueAt(node, col)
                    if (!checkValueChangeAllowed(node, oldValue, value)) {
                        throw new UnsupportedOperationException("Structure change not allowed")
                    }
                    if (!model.isCellEditable(node, col) && validValue(value)) {
                        showReadOnlyAlert()
                        throw new IllegalArgumentException("Attempt to set read-only cell")
                    }
                }
            }

            tableData.eachWithIndex { List lineData, int lineIndex ->
                backUp << []
                lastProcessedLine = lineIndex
                lineData.eachWithIndex { def value, int colIndex ->
                    Object node = nodes[lineIndex]
                    int col = columnOffset + colIndex
                    backUp[lineIndex] << model.getValueAt(node, col)
                    model.setValueAt(value, node, col)
                }
            }
        } catch (Exception error) {
            LOG.error "Error setting value: ${error.message}"
            throw error
        }
    }

    protected void showReadOnlyAlert() {
        new I18NAlert('DoNotPasteInReadOnlyCell').show()
    }

    protected boolean checkValueChangeAllowed(def node, def oldValue, def newValue) {
        return true
    }

    protected boolean checkValueChangeAllowed(ParameterizationClassifierTableTreeNode node,
                                              def oldValue, def newValue) {
        return oldValue == newValue
    }


    private boolean validValue(def value) {
        return value != null
    }

    private boolean validValue(String value) {
        return value.trim().length() > 0
    }

}