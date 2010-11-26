package com.canoo.ulc.community.locale.server;

import com.ulcjava.base.application.ULCProxy;

import java.util.Locale;

/**
 * User: Matthias Ansorge
 * Date: Nov 22, 2010
 * Time: 3:15:10 PM
 */
public class ULCClientLocaleSetter extends ULCProxy {
    public static void setDefaultLocale(Locale locale) {
        new ULCClientLocaleSetter(locale);
    }

    private Locale defaultLocale;

    private ULCClientLocaleSetter(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
        upload();
    }

    @Override
    protected void uploadStateUI() {
        super.uploadStateUI();
        createStateUI(new Object[]{defaultLocale.getLanguage(), defaultLocale.getCountry(), defaultLocale.getVariant()});
    }

    @Override
    protected String typeString() {
        return "com.canoo.ulc.community.locale.client.UIClientLocaleSetter";
    }
}