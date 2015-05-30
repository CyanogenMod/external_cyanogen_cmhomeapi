package org.cyanogenmod.launcher.home.api.cards;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;

/**
 * Created by matt on 5/28/15.
 */
public class LoginCardData extends CardData {
    /**
     * @hide
     */
    static final String KEY_LOGIN_CARD = "login_card";

    public LoginCardData(String title) {
        super(title, new Date());
        setReasonTextInternal(KEY_LOGIN_CARD);
    }

    public LoginCardData(CardData cardData) {
        super(cardData);
        setReasonTextInternal(KEY_LOGIN_CARD);
    }

    public void setContentSourceImage(int resId) {
        setContentSourceImageInternal(resId);
    }

    public void setContentSourceImage(Bitmap bitmap) {
        setContentSourceImageInternal(bitmap);
    }

    public void setContentSourceImage(Uri uri) {
        setContentSourceImageInternal(uri);
    }

    public Uri getContentSourceImageUri() {
        return getContentSourceImageUriInternal();
    }

    public void setAvatarImage(int resId) {
        setAvatarImageInternal(resId);
    }

    public void setAvatarImage(Bitmap bitmap) {
        setAvatarImageInternal(bitmap);
    }

    public void setAvatarImage(Uri uri) {
        setAvatarImageInternal(uri);
    }

    public Uri getAvatarImageUri() {
        return getAvatarImageUriInternal();
    }

    public void setSmallText(String text) {
        setSmallTextInternal(text);
    }

    public String getSmallText() {
        return getSmallTextInternal();
    }
}