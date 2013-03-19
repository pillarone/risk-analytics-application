package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.chart.model.CriteriaViewModel
import org.pillarone.riskanalytics.application.ui.chart.model.QueryPaneModel
import org.pillarone.riskanalytics.application.ui.chart.view.CriteriaView
import org.pillarone.riskanalytics.application.util.LocaleResources

class QueryPane implements IModelChangedListener {

    ULCBoxPane content
    QueryPaneModel model
    private int defaultButtonWidth = 180
    private int defaultButtonHeigth = 25

    public QueryPane(QueryPaneModel model) {
        this.@model = model
        content = new ULCBoxPane(1, 2)
        model.addModelChangedListener this
        layoutComponents()
    }

    void layoutComponents() {
        content.removeAll()


        model.getCriteriaGroupCount().times {int groupIndex ->
            ULCBoxPane criteriaGroup = new ULCBoxPane(2, 0)
            criteriaGroup.name = "groupPane$groupIndex"
            criteriaGroup.border = BorderFactory.createTitledBorder(getText("cirteriaGroup"))

            model.getCriteriaGroup(groupIndex).eachWithIndex {CriteriaViewModel model, int criteriaIndex ->
                ULCBoxPane criteriaPane = new CriteriaView(model).content
                criteriaPane.name = "criteriaPaneG$groupIndex$criteriaIndex"
                criteriaGroup.add(2, ULCBoxPane.BOX_EXPAND_TOP, criteriaPane)
            }
            ULCButton addCriteriaButton = new ULCButton(getText("addCriteria"))
            ULCButton removeCriteriaGroupButton = new ULCButton(getText("removeGroup"))

            addCriteriaButton.preferredSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)
            removeCriteriaGroupButton.preferredSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)
            addCriteriaButton.minimumSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)
            removeCriteriaGroupButton.minimumSize = new Dimension(defaultButtonWidth, defaultButtonHeigth)

            addCriteriaButton.addActionListener([actionPerformed: { model.addCriteria(groupIndex) }] as IActionListener)
            removeCriteriaGroupButton.addActionListener([actionPerformed: { model.removeCriteriaGroup groupIndex }] as IActionListener)

            criteriaGroup.add(ULCBoxPane.BOX_LEFT_TOP, addCriteriaButton)
            criteriaGroup.add(ULCBoxPane.BOX_LEFT_TOP, removeCriteriaGroupButton)
            content.add(ULCBoxPane.BOX_EXPAND_TOP, criteriaGroup)
        }
    }

    public void modelChanged() {
        layoutComponents()
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("QueryPane." + key);
    }

}