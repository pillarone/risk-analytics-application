package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tree.DefaultMutableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.ItemNode
import org.pillarone.riskanalytics.core.BatchRun

/**
 * @author fouad jaada
 */

public class BatchRunNode extends ItemNode {

    public BatchRunNode(BatchRun batchRun) {
        super(batchRun, true, true)
    }
}

class BatchRootNode extends DefaultMutableTreeNode {

    public BatchRootNode(String name) {
        super(name);
    }
}
