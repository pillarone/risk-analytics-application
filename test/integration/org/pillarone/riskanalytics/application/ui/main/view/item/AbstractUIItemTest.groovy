package org.pillarone.riskanalytics.application.ui.main.view.item

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.ULCFrame
import org.pillarone.riskanalytics.application.AbstractSimpleFunctionalTest
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractUIItemTest extends AbstractSimpleFunctionalTest {

    @Override protected void doStart() {
        ULCFrame frame = new ULCFrame("test")
        frame.defaultCloseOperation = ULCFrame.TERMINATE_ON_CLOSE
        frame.name = "test"

        AbstractUIItem item = createUIItem()
        frame.setContentPane(item.createDetailView())
        ULCClipboard.install()
        UIUtils.setRootPane(frame)
        frame.visible = true
    }

    abstract AbstractUIItem createUIItem()

}
