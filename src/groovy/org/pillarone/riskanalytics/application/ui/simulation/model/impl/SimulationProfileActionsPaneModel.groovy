package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.user.UserManagement

class SimulationProfileActionsPaneModel {
    ProfilesComboBoxModel simulationProfiles
    final Class modelClass
    private final ISimulationProfileApplicable simulationProfileApplicable

    SimulationProfileActionsPaneModel(ISimulationProfileApplicable simulationProfileApplicable, Class modelClass) {
        this.modelClass = modelClass
        this.simulationProfileApplicable = simulationProfileApplicable
        simulationProfiles = createSimulationProfilesModel(modelClass)
    }

    protected ProfilesComboBoxModel createSimulationProfilesModel(Class modelClass) {
        new ProfilesComboBoxModel(modelClass)
    }

    boolean saveCurrentProfile(String name) {
        def profile = simulationProfileApplicable.createProfile(name)
        if (isAllowedToSave(profile)) {
            def id = profile.save()
            if (id) {
                simulationProfiles.addElement(profile)
            }
            return id
        }
        return false
    }

    boolean deleteCurrentProfile() {
        if (currentAllowedToDelete) {
            def id = selectedProfile.delete()
            if (id) {
                simulationProfiles.removeElement(selectedProfile)
            }
            return id
        }
        return false
    }

    boolean applyCurrentProfile() {
        if (currentAllowedToApply) {
            simulationProfileApplicable.applyProfile(selectedProfile)
            return true
        }
        return false
    }

    boolean isCurrentAllowedToDelete() {
        if (!selectedProfile || !selectedProfile.id) {
            return false
        }
        if (!selectedProfile.creator) {
            return true
        }
        selectedProfile.creator.username == currentUser()?.username
    }

    boolean isCurrentAllowedToApply() {
        selectedProfile?.id
    }

    protected boolean isAllowedToSave(SimulationProfile profile) {
        if (!profile) {
            return false
        }
        if (!(profile.id && profile.creator)) {
            return true
        }
        profile.creator.username == currentUser()?.username
    }

    protected SimulationProfile getSelectedProfile() {
        simulationProfiles.selectedProfile
    }

    Person currentUser() {
        UserManagement.currentUser
    }

    String getSelectedProfileName() {
        selectedProfile?.name
    }
}
