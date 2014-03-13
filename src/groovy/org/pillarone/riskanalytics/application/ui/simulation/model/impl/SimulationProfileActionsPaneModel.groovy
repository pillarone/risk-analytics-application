package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement

class SimulationProfileActionsPaneModel {
    final ProfilesComboBoxModel simulationProfiles
    final Class modelClass
    private final SimulationSettingsPaneModel simulationSettingsPaneModel

    SimulationProfileActionsPaneModel(SimulationSettingsPaneModel simulationSettingsPaneModel, Class modelClass) {
        this.modelClass = modelClass
        this.simulationSettingsPaneModel = simulationSettingsPaneModel
        simulationProfiles = new ProfilesComboBoxModel(modelClass)
    }

    boolean saveCurrentProfile(String name) {
        def profile = simulationSettingsPaneModel.createProfile(name)
        if (!isAllowed(profile)) {
            return false
        }
        def id = profile.save()
        if (id) {
            simulationProfiles.addElement(profile)
        }
        id
    }

    void apply(SimulationProfile item) {
        simulationSettingsPaneModel.applyProfile(item)
    }

    boolean delete(SimulationProfile profile) {
        if (!isAllowed(profile)) {
            return false
        }
        def id = profile.delete()
        if (id) {
            simulationProfiles.removeElement(profile)
        }
        id
    }

    boolean isAllowed(SimulationProfile profile) {
        if (!profile) {
            return false
        }
        profile.creator?.username == currentUser()?.username
    }

    SimulationProfile loadSelectedProfile() {
        def profile = simulationProfiles.selectedProfile
        if (profile && !profile.loaded) {
            profile.load()
        }
        profile
    }

    Person currentUser() {
        UserManagement.currentUser
    }
}
