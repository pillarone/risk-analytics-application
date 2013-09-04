package org.pillarone.riskanalytics.application.ui.base.action

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.event.ActionEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe

abstract public class ExceptionSafeAction extends AbstractAction {

    private Log logger

    public ExceptionSafeAction() {
        super("")
        logger = LogFactory.getLog(this.class)
    }

    public ExceptionSafeAction(String name) {
        super(name)
    }


    final public void actionPerformed(ActionEvent event) {
        ExceptionSafe.protect {
            trace(this.toString())
            doActionPerformed(event)
        }
    }

    protected void trace(String content) {
        logger.debug(content)
    }

    abstract void doActionPerformed(ActionEvent event)


}