package org.pillarone.riskanalytics.application.ui.base.model

import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import com.ulcjava.base.application.tabletree.DefaultMutableTableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.Resource

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ItemComparator {

    public static boolean isEqual(def item1, ItemNode node) {
        return isEqual(item1, node.abstractUIItem)
    }

    public static boolean isEqual(def item1, DefaultMutableTableTreeNode node) {
        return false
    }

    public static boolean isEqual(Resource item1, Resource item2) {
        return item1 != null && item2 != null && item1.name == item2.name && item1.versionNumber.toString() == item2.versionNumber.toString() && item1.modelClass == item2.modelClass
    }

    public static boolean isEqual(Parameterization item1, Parameterization item2) {
        return item1 != null && item2 != null && item1.name == item2.name && item1.versionNumber.toString() == item2.versionNumber.toString() && item1.modelClass == item2.modelClass
    }

    public static boolean isEqual(ResultConfiguration item1, ResultConfiguration item2) {
        return item1 != null && item2 != null && item1.name == item2.name && item1.versionNumber.toString() == item2.versionNumber.toString() && item1.modelClass == item2.modelClass
    }

    public static boolean isEqual(Simulation item1, Simulation item2) {
        return item1 != null && item2 != null && item1.name == item2.name && item1.modelClass == item2.modelClass
    }

    public static boolean isEqual(BatchRun item1, BatchRun item2) {
        return item1 != null && item2 != null && item1.name == item2.name
    }

    public static boolean isEqual(AbstractUIItem item1, AbstractUIItem item2) {
        return isEqual(item1.item, item2.item)
    }

    public static boolean isEqual(ModellingItem item1, AbstractUIItem item2) {
        return isEqual(item1, item2.item)
    }

    public static boolean isEqual(BatchRun item1, AbstractUIItem item2) {
        return isEqual(item1, item2.item)
    }

    public static boolean isEqual(def item1, def item2) {
        return false
    }
}
