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

    public void setText(String htmlText) {
        super.setText(addCss(htmlText));
    }

    private String addCss(String htmlText) {
        StringBuilder sb = new StringBuilder("<html>")
        sb.append("<head><style type='text/css'>")
        sb.append("body, a, p, td { font-family:sans-serif;font-size:10px;}")
        sb.append("</style>")
        sb.append("</head><body>")
        sb.append(htmlText)
        sb.append("</body></html>")
        return sb.toString()
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