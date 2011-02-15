package com.canoo.ulc.community.locale.client;

import com.ulcjava.base.client.UIProxy;

import java.util.Locale;
import java.util.TimeZone;


public class UIClientTimeZoneSetter extends UIProxy {

    @Override
    protected Object createBasicObject(Object[] arguments) {
        TimeZone timeZone = (TimeZone) arguments[0];
        TimeZone.setDefault(timeZone);
        return null;
    }
}
