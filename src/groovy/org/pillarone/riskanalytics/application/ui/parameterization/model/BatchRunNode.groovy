package org.pillarone.riskanalytics.application.ui.parameterization.model

import org.pillarone.riskanalytics.core.BatchRun

import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode

/**
 * @author fouad jaada
 */

public class BatchRunNode extends ItemNode {

    public BatchRunNode(BatchRun batchRun) {
        super(batchRun, true, true)
    }
}

class BatchRootNode extends DefaultMutableTableTreeNode {

    public BatchRootNode(String name) {
        super([name] as Object[]);
    }
}
