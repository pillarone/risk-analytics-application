package com.canoo.ulc.community.locale.server;

import com.ulcjava.base.application.ULCProxy;

import java.util.TimeZone;

public class ULCClientTimeZoneSetter extends ULCProxy {

    public static void setDefaultTimeZone(TimeZone timeZone) {
        new ULCClientTimeZoneSetter(timeZone);
    }

    private TimeZone timeZone;

    private ULCClientTimeZoneSetter(TimeZone timeZone) {
        this.timeZone = timeZone;
        upload();
    }

    @Override
    protected void uploadStateUI() {
        super.uploadStateUI();
        createStateUI(new Object[]{timeZone});
    }

    @Override
    protected String typeString() {
        return "com.canoo.ulc.community.locale.client.UIClientTimeZoneSetter";
    }
}
