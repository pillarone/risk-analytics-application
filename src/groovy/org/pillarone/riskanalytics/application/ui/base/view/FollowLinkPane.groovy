package org.pillarone.riskanalytics.application.ui.base.view

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCHtmlPane
import com.ulcjava.base.application.event.HyperlinkEvent
import com.ulcjava.base.application.event.IHyperlinkListener

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class FollowLinkPane extends ULCHtmlPane {

    public FollowLinkPane() {
        setVeto true
        addHyperlinkListener(new OpenLinkListener())
    }

    class OpenLinkListener implements IHyperlinkListener {

        void linkActivated(HyperlinkEvent hyperlinkEvent) {
            String url = null
            try {
                url = hyperlinkEvent.getURL().toExternalForm()
            } catch (NullPointerException ex) {
                url = hyperlinkEvent.getDescription()
            }
            if (url != null)
                ClientContext.showDocument(url, "_new")
        }

        void linkError(HyperlinkEvent hyperlinkEvent) {
        }
    }
} 