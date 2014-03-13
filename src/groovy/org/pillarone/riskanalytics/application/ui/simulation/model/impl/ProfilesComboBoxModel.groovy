package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.core.SimulationProfileDAO
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile
import org.pillarone.riskanalytics.core.user.UserManagement


class ProfilesComboBoxModel extends DefaultComboBoxModel {
    private Map<String, SimulationProfile> profilesMap = [:]
    private final Class modelClass

    ProfilesComboBoxModel(Class modelClass) {
        this.modelClass = modelClass
        refreshProfiles()
    }

    void refreshProfiles() {
        removeAllElements()
        profilesMap.clear()
        SimulationProfileDAO.withCreatorOrForPublic(currentUser()).withModelClass(modelClass).list(order: 'name').each {
            def profile = new SimulationProfile(it.name, modelClass)
            profile.load()
            profilesMap[displayName(profile)] = profile
            addElement(profile)
        }
        selectedItem = null
    }

    @Override
    void addElement(Object item) {
        SimulationProfile profile = item as SimulationProfile
        String name = displayName(profile)
        if (getIndexOf(name) == -1) {
            super.addElement(name)
            profilesMap[name] = profile
            selectedItem = name
        }
    }

    @Override
    void removeElement(Object item) {
        String name = displayName(item as SimulationProfile)
        super.removeElement(name)

    }

    @Override
    void removeElementAt(int index) {
        super.removeElementAt(index)
        profilesMap.remove(index)
    }

    private String displayName(SimulationProfile profile) {
        profile.forPublic ? "$profile.name (public)" : profile.name
    }

    protected currentUser() {
        UserManagement.currentUser
    }

    SimulationProfile getSelectedProfile() {
        profilesMap[selectedItem as String]
    }

    String getSelectedProfileName() {
        selectedProfile?.name
    }

}
