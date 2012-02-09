package org.pillarone.riskanalytics.application.ui.resource.model

import org.pillarone.riskanalytics.application.ui.base.model.VersionedItemNode
import org.pillarone.riskanalytics.application.ui.main.view.item.ResourceUIItem


class ResourceNode extends VersionedItemNode {

    ResourceNode(ResourceUIItem abstractUIItem) {
        super(abstractUIItem, false)
    }
}
