package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
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
            LogFactory.getLog(this.class).debug(this)
            doActionPerformed(event)
        }
    }

    abstract void doActionPerformed(ActionEvent event)


}