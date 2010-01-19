package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tree.DefaultMutableTreeNode
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode

/**
 * @author fouad jaada
 */

public class BatchRunNode extends ItemNode {

    public BatchRunNode(BatchRun batchRun) {
        super(batchRun, false)
    }
}

class BatchRootNode extends DefaultMutableTreeNode {

    public BatchRootNode(String name) {
        super(name);
    }
}
