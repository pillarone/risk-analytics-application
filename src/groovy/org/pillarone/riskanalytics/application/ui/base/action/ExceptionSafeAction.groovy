package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.event.ActionEvent
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe

abstract public class ExceptionSafeAction extends AbstractAction {

    public ExceptionSafeAction() {
        super("")
    }

    public ExceptionSafeAction(String name) {
        super(name)
    }


    final public void actionPerformed(ActionEvent event) {
        ExceptionSafe.protect {
            doActionPerformed(event)
        }
    }

    abstract void doActionPerformed(ActionEvent event)


}