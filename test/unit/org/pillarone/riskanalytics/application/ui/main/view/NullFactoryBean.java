package org.pillarone.riskanalytics.application.ui.main.view;

import org.springframework.beans.factory.FactoryBean;

public class NullFactoryBean implements FactoryBean {
    private final Class<?> objectType;

    public NullFactoryBean(Class<?> objectType) {
        this.objectType = objectType;
    }

    @Override
    public Object getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return objectType;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}