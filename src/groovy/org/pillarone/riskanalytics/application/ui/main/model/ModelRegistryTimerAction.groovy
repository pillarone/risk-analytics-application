package org.pillarone.riskanalytics.application.ui.main.model

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.core.model.Model
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel


class ModelRegistryTimerAction implements IActionListener {

    private List<Model> newModels = []

    AbstractTableTreeModel selectionTreeModel

    synchronized void actionPerformed(ActionEvent actionEvent) {
        for (Model model in newModels) {
            selectionTreeModel.addNodeForItem(model)
        }
        newModels.clear()
    }

    synchronized void addModel(Model model) {
        newModels << model
    }


}
