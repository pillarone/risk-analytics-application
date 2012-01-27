package org.pillarone.riskanalytics.application.ui.resultnavigator.model;

import com.ulcjava.base.application.DefaultComboBoxModel;
import com.ulcjava.base.application.IComboBoxModel;
import com.ulcjava.base.application.event.IListDataListener;
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ResultAccess;
import org.pillarone.riskanalytics.core.output.SimulationRun;

import java.util.*;

/**
 * Model used for the selection of the simulation runs.
 * It accesses the database through ResultAccess to the db from which the available
 * simulation runs are retrieved. Then, the simulation runs are grouped according to
 * the model the simulation result has been generated with. For each of these groups
 * a combo box model is prepared that can be used thereafter for subsequent selection
 * of a) the model and b) the simulation run.
 *
 */
public class SimulationRunsModel {

    private List<String> models = null; // long names
    private String selectedModel = null; // long name
    private Map<String, Map<String,SimulationRun>> runsLookup = null; // simulation run name --> simulation run
    private Map<String,IComboBoxModel> runsComboBoxModels = null; // model name long name --> combo box model
    private IComboBoxModel modelsComboBoxModel = null;
    private String selectedRunName = null;

    public static String getShortName(String modelName) {
        return  modelName.substring(modelName.lastIndexOf(".")+1);
    }

    public SimulationRunsModel() {
        // get all the runs
        List<SimulationRun> runs = ResultAccess.getSimulationRuns();

        // initialize the collections that keep the model names, simulation runs
        models = new ArrayList<String>();
        Map<String, List<SimulationRun>> simulationRuns = new HashMap<String, List<SimulationRun>>();
        runsLookup = new HashMap<String, Map<String,SimulationRun>>();

        // Iterate through all the runs and organize them
        if (runs != null && runs.size() > 0) {
            for (SimulationRun run : runs) {
                String modelName = run.getModel();
                if (!simulationRuns.containsKey(modelName)) {
                    simulationRuns.put(modelName, new ArrayList<SimulationRun>());
                    models.add(modelName);
                }
                simulationRuns.get(modelName).add(run);
                String runName = run.getName();
                if (!runsLookup.containsKey(modelName)) {
                    runsLookup.put(modelName, new HashMap<String,SimulationRun>());
                }
                Map<String,SimulationRun> runsForModel = runsLookup.get(modelName);
                if (!runsForModel.containsKey(runName)) {
                    runsForModel.put(runName, run);
                }
            }
        }

        // create the combo box model for the model selection
        Collections.sort(models);
        modelsComboBoxModel = new DefaultComboBoxModel(models);

        // create the combo box models (one for all the runs for a given model)
        runsComboBoxModels = new HashMap<String,IComboBoxModel>();
        for (String modelName : models) {
            List<String> runsForModel = new ArrayList<String>();
            for (SimulationRun run : simulationRuns.get(modelName)) {
                runsForModel.add(run.getName());
            }
            Collections.sort(runsForModel);
            runsComboBoxModels.put(modelName, new DefaultComboBoxModel(runsForModel));
        }

        // initialize the values shown at the beginning
        selectedModel = models.get(0);
        modelsComboBoxModel.setSelectedItem(selectedModel);
        selectedRunName = simulationRuns.get(selectedModel).get(0).getName();
        runsComboBoxModels.get(selectedModel).setSelectedItem(selectedRunName);
    }

    public SimulationRun getSelectedRun() {
        return runsLookup.get(selectedModel).get(selectedRunName);
    }

    public String getSelectedModelName() {
        return selectedModel;
    }

    public void setSelectedModel(String modelName) {
        selectedModel = modelName;
        modelsComboBoxModel.setSelectedItem(selectedModel);
    }

    public void setSelectedRun(String runName) {
        selectedRunName = runName;
        runsComboBoxModels.get(selectedModel).setSelectedItem(runName);
    }

    public IComboBoxModel getModelComboBoxModel() {
        return modelsComboBoxModel;
    }

    public IComboBoxModel getSimulationRunsComboBoxModel() {
        return runsComboBoxModels.get(selectedModel);
    }

}
