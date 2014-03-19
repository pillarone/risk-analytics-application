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
        findProfiles().each { addElement(it) }
        selectedItem = null
    }

    protected List<SimulationProfile> findProfiles() {
        SimulationProfileDAO.withCreatorOrForPublic(currentUser()).withModelClass(modelClass).list(order: 'name').collect {
            def profile = new SimulationProfile(it.name, modelClass)
            profile.load()
            profile
        }
    }

    @Override
    void addElement(Object item) {
        SimulationProfile profile = item as SimulationProfile
        checkModelClass(profile)
        String name = displayName(profile)
        profilesMap[name] = profile
        if (getIndexOf(name) == -1) {
            super.addElement(name)
        }
        selectedItem = name
    }

    private void checkModelClass(SimulationProfile profile) {
        if (profile.modelClass != modelClass) {
            throw new IllegalStateException("profile $profile ahas wrong modelClass: ${profile.modelClass}! Expceted: $modelClass")
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
