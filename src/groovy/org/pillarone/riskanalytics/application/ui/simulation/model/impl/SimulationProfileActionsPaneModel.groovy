package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

class SimulationProfileActionsPaneModel {
    DefaultComboBoxModel simulationProfiles
    private final SimulationSettingsPaneModel simulationSettingsPaneModel
    private final Class modelClass

    SimulationProfileActionsPaneModel(SimulationSettingsPaneModel simulationSettingsPaneModel, Class modelClass) {
        this.modelClass = modelClass
        this.simulationSettingsPaneModel = simulationSettingsPaneModel
        simulationProfiles = new DefaultComboBoxModel()
        refreshProfiles()
    }

    void refreshProfiles() {
        simulationProfiles.removeAllElements()
        SimulationProfile.findAllNamesForModelClass(modelClass).each { simulationProfiles.addElement(it) }
        simulationProfiles.selectedItem = null
    }

    void saveCurrentProfile(String name) {
        def template = simulationSettingsPaneModel.createTemplate(name)
        template.save()
        String profileName = template.name
        if (simulationProfiles.getIndexOf(profileName) == -1) {
            simulationProfiles.addElement(profileName)
            simulationProfiles.selectedItem = profileName
        }
    }

    void apply(SimulationProfile item) {
        simulationSettingsPaneModel.applyTemplate(item)
    }

    void delete(SimulationProfile profile) {
        profile.delete()
        simulationProfiles.removeElement(profile.name)
    }
}
