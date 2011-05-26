package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.AbstractP1RATTestCase
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class BatchUIItemUnitTests extends AbstractP1RATTestCase {

    public void testView() {
//       Thread.sleep 5000
    }

    @Override
    ULCComponent createContentPane() {
        RiskAnalyticsMainModel model = new RiskAnalyticsMainModel(null)
        BatchUIItem batchUIItem = new BatchUIItem(model, null)
        return batchUIItem.createDetailView()
    }


}
