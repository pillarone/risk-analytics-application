package org.pillarone.riskanalytics.application.ui.resultnavigator.model;

import com.ulcjava.applicationframework.application.form.model.FormModel;
import org.pillarone.riskanalytics.core.output.SimulationRun;

/**
 * @author martin.melchior
 */
public class SimulationRunFormModel extends FormModel<NameBean> {

    SimulationRunComboBoxModel comboBoxModel;

    public SimulationRunFormModel(NameBean bean) {
        super(bean);
        comboBoxModel = new SimulationRunComboBoxModel();
    }

    public SimulationRunComboBoxModel getSimulationRunsModel() {
        return comboBoxModel;
    }

    public SimulationRun getSelected() {
        return comboBoxModel.getSelectedRun();
    }
}
