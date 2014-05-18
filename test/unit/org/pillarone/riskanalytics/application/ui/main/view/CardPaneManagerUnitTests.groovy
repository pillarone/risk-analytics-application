package org.pillarone.riskanalytics.application.ui.main.view
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import models.application.ApplicationModel
import models.core.CoreModel
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.core.model.Model

class CardPaneManagerUnitTests extends AbstractP1RATTestCase {

    CardPaneManager cardPaneManager

    @Override
    ULCComponent createContentPane() {
        RiskAnalyticsMainModel mainModel = new RiskAnalyticsMainModel()
        cardPaneManager = new CardPaneManager(riskAnalyticsMainModel: mainModel)
        def content = new ULCBoxPane()
        content.add(cardPaneManager.cardPane)
        Model core = new CoreModel()
        Model application = new ApplicationModel()

        assertFalse "pane manager doesn't contain CORE", cardPaneManager.contains(core)

        //test add card
        cardPaneManager.addCard(core)

        assertEquals "Core", cardPaneManager.cardPane.selectedName

        assertTrue "pane manager must contain CORE", cardPaneManager.contains(core)
        assertFalse "pane manager doesn't contain APPLICATION", cardPaneManager.contains(application)

        cardPaneManager.addCard(application)
        assertTrue "pane manager must contain APPLICATION", cardPaneManager.contains(application)

        assertEquals "Application", cardPaneManager.cardPane.selectedName

        //test select card
        cardPaneManager.selectCard(core)
        assertEquals "Core", cardPaneManager.cardPane.selectedName

        //test remove card
        cardPaneManager.removeCard(core)
        assertFalse cardPaneManager.selectCard(core)
        assertFalse cardPaneManager.contains(core)
        assertTrue cardPaneManager.contains(application)

        //add batch card
        assertEquals 1, cardPaneManager.cardPane.componentCount
        cardPaneManager.addCard(null)
        assertEquals 2, cardPaneManager.cardPane.componentCount

        assertEquals CardPaneManager.NO_MODEL, cardPaneManager.cardPane.selectedName
        cardPaneManager.selectCard(application)
        assertEquals application.name, cardPaneManager.cardPane.selectedName
        cardPaneManager.selectCard(null)
        assertEquals CardPaneManager.NO_MODEL, cardPaneManager.cardPane.selectedName
        return content
    }

    void testAddCard() {
    }
}
