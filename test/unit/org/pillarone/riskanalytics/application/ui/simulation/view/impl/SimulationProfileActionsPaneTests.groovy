package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.testframework.operator.ComponentByNameChooser
import com.ulcjava.testframework.operator.ULCButtonOperator
import com.ulcjava.testframework.operator.ULCComboBoxOperator
import com.ulcjava.testframework.operator.ULCFrameOperator
import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import models.core.CoreModel
import org.junit.Test
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.ISimulationProfileApplicable
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.ProfilesComboBoxModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationProfileActionsPaneModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

import static org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationProfileActionsPane.*

class SimulationProfileActionsPaneTests extends AbstractSimpleStandaloneTestCase {

    private SimulationProfile appliedProfile

    @Override
    protected void setUp() throws Exception {
        LocaleResources.testMode = true
        super.setUp()
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LocaleResources.testMode = false
    }

    @Override
    void start() {
        ULCFrame frame = new ULCFrame('test')
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.contentPane = new ULCBoxPane()
        frame.contentPane.add(new SimulationProfileActionsPane(new TestModel(new Applicable())).content)
        frame.visible = true
    }

    ULCFrameOperator getFrame() {
        new ULCFrameOperator('test')
    }

    @Test
    void testInitialState() {
        ULCComboBoxOperator comboBox = profilesComboBox
        assert comboBox.itemCount == 3
        assert comboBox.selectedItem == null
        assert comboBox.getItemAt(0) == 'profile1'
        assert comboBox.getItemAt(1) == 'profile2 (public)'
        assert comboBox.getItemAt(2) == 'profile3'

        assert !applyButton.enabled
        assert saveButton.enabled
        assert !deleteButton.enabled
    }

    void testDeleteButtonEnablingState() {
        def box = profilesComboBox
        box.selectItem('profile1')
        assert applyButton.enabled
        assert saveButton.enabled
        assert deleteButton.enabled

        box.selectItem('profile2 (public)')
        assert applyButton.enabled
        assert saveButton.enabled
        assert !deleteButton.enabled

        box.selectItem('profile3')
        assert applyButton.enabled
        assert saveButton.enabled
        assert !deleteButton.enabled
    }

    void testPressApplyButton() {
        appliedProfile == null
        def box = profilesComboBox
        box.selectItem('profile1')
        applyButton.clickMouse()
        assert appliedProfile.name == 'profile1'

        box.selectItem('profile2 (public)')
        applyButton.clickMouse()
        assert appliedProfile.name == 'profile2'
        box.selectItem('profile3')
        applyButton.clickMouse()
        assert appliedProfile.name == 'profile3'
    }

    private ULCComboBoxOperator getProfilesComboBox() {
        new ULCComboBoxOperator(frame, new ComponentByNameChooser(PROFILES_COMBO_BOX))
    }

    private ULCButtonOperator getDeleteButton() {
        new ULCButtonOperator(frame, new ComponentByNameChooser(DELETE_BUTTON))
    }

    private ULCButtonOperator getSaveButton() {
        new ULCButtonOperator(frame, new ComponentByNameChooser(SAVE_BUTTON))
    }

    private ULCButtonOperator getApplyButton() {
        new ULCButtonOperator(frame, new ComponentByNameChooser(APPLY_BUTTON))
    }

    private class TestProfileComboBoxModel extends ProfilesComboBoxModel {
        TestProfileComboBoxModel() {
            super(CoreModel)
        }

        @Override
        protected List<SimulationProfile> findProfiles() {
            [
                    new SimulationProfile('profile1', CoreModel),
                    new SimulationProfile('profile2', CoreModel).with {
                        it.forPublic = true
                        it
                    },
                    new SimulationProfile('profile3', CoreModel)
            ]
        }
    }

    private class TestModel extends SimulationProfileActionsPaneModel {
        TestModel(ISimulationProfileApplicable simulationProfileApplicable) {
            super(simulationProfileApplicable, CoreModel)
        }

        @Override
        boolean isCurrentAllowedToDelete() {
            if (!selectedProfile) {
                return false
            }
            switch (selectedProfile.name) {
                case ('profile1'): return true
                default: return false
            }
        }

        @Override
        boolean isCurrentAllowedToApply() {
            true
        }

        @Override
        protected ProfilesComboBoxModel createSimulationProfilesModel(Class modelClass) {
            new TestProfileComboBoxModel()
        }
    }

    private class Applicable implements ISimulationProfileApplicable {

        @Override
        SimulationProfile createProfile(String name) {
        }

        @Override
        void applyProfile(SimulationProfile profile) {
            appliedProfile = profile
        }
    }
}
