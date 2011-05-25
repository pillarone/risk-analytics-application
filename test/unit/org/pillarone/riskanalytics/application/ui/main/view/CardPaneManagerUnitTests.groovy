package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCCardPane
import com.ulcjava.base.application.ULCComponent
import models.application.ApplicationModel
import models.core.CoreModel
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.core.model.Model

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CardPaneManagerUnitTests extends AbstractP1RATTestCase {

    public void testAddCard() {
    }


    @Override
    ULCComponent createContentPane() {
        ULCCardPane cardPane = new ULCCardPane()
        CardPaneManager cardPaneManager = new CardPaneManager(cardPane)
        Model core = new CoreModel()
        Model application = new ApplicationModel()

        assertFalse "pane manager doesn't contain CORE", cardPaneManager.contains(core)

        ITabListener tabListener = [tabClosing: {TabEvent event ->}] as ITabListener
        Closure closeure1 = { event ->  }
        Closure closeure2 = { event ->  }

        //test add card
        cardPaneManager.addCard(core)

        assertEquals "Core", cardPaneManager.cardPane.getSelectedName()

        assertTrue "pane manager must contain CORE", cardPaneManager.contains(core)
        assertFalse "pane manager doesn't contain APPLICATION", cardPaneManager.contains(application)

        cardPaneManager.addCard(application)
        assertTrue "pane manager must contain APPLICATION", cardPaneManager.contains(application)

        assertEquals "Application", cardPaneManager.cardPane.getSelectedName()

        //test select card
        cardPaneManager.selectCard(core)
        assertEquals "Core", cardPaneManager.cardPane.getSelectedName()

        //test remove card
        cardPaneManager.removeCard(core)
        assertFalse cardPaneManager.selectCard(core)
        assertFalse cardPaneManager.contains(core)
        assertTrue cardPaneManager.contains(application)

        //add batch card
        assertEquals 1, cardPane.getComponentCount()
        cardPaneManager.addCard(null)
        assertEquals 2, cardPane.getComponentCount()

        assertEquals CardPaneManager.NO_MODEL, cardPane.getSelectedName()
        cardPaneManager.selectCard(application)
        assertEquals application.name, cardPane.getSelectedName()
        cardPaneManager.selectCard(null)
        assertEquals CardPaneManager.NO_MODEL, cardPane.getSelectedName()


        return new ULCBoxPane()
    }


}
