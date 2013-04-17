package org.pillarone.riskanalytics.application.ui.base.view

import com.ulcjava.base.application.ULCHtmlPane
import groovy.transform.CompileStatic

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
abstract class AbstractLinkPane extends ULCHtmlPane {

    public AbstractLinkPane() {
        setVeto true
        addListener()
    }

    abstract void addListener()

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

}
