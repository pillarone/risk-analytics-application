package org.pillarone.riskanalytics.application.ui.main.view

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractView {

    public void init() {
        initComponents()
        layoutComponents()
        attachListeners()
    }

    abstract protected void initComponents()

    abstract protected void layoutComponents()

    abstract protected void attachListeners()
}
