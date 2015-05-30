package org.cyanogenmod.launcher.home.api.cards;

import java.util.Date;

/**
 * Created by matt on 5/28/15.
 */
public class LoginCardData extends CardData {
    private static final String KEY_LOGIN_CARD = "login_card";

    public LoginCardData(String title) {
        super(title, new Date());
        setReasonTextInternal(KEY_LOGIN_CARD);
    }

    public void setContentSourceImage(int resId) {
        setContentSourceImageInternal(resId);
    }
}