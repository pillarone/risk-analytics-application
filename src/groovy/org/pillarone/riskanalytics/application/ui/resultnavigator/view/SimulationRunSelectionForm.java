package org.pillarone.riskanalytics.application.ui.resultnavigator.view;

import com.ulcjava.applicationframework.application.form.AbstractFormBuilder;
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.SimulationRunFormModel;

public class SimulationRunSelectionForm extends AbstractFormBuilder<SimulationRunFormModel> {

    public SimulationRunSelectionForm(SimulationRunFormModel formModel) {
        super(formModel);

    }

    public void refresh() {

    }

    @Override
    protected void initForm() {
        setColumnWeights(0f, 0f, 1f);
        addComboBox("name", super.getModel().getSimulationRunsModel());
    }
}