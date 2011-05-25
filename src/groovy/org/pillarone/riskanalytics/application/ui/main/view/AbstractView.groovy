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

    abstract void initComponents()

    abstract void layoutComponents()

    abstract void attachListeners()
}
