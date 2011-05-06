package org.pillarone.riskanalytics.application.ui.main.view.item

import org.pillarone.riskanalytics.core.model.Model
import com.ulcjava.base.application.ULCComponent


class ModelUIItem implements IUIItem {

    private Model model

    ModelUIItem(Model model) {
        this.model = model
    }

    String createTitle() {
        return model.class.simpleName
    }

    ULCComponent createDetailView() {
        throw new IllegalStateException("A model does not have a detail view.")
    }

    void close() {

    }


}
