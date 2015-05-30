package org.cyanogenmod.launcher.home.api.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Date;

/**
 * Created by matt on 5/30/15.
 */
public class MessageCardData extends CardData {

    public MessageCardData(String title, Date contentCreatedDate) {
        super(title, contentCreatedDate);
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

    public void setAvatarImage(int resId) {
        setAvatarImageInternal(resId);
    }

    public void setAvatarImage(Bitmap bitmap) {
        setAvatarImageInternal(bitmap);
    }

    public void setAvatarImage(Uri uri) {
        setAvatarImageInternal(uri);
    }

    public void setBodyText(String message) {
        setBodyTextInternal(message);
    }

    public void setSmallText(String text) {
        setSmallTextInternal(text);
    }

    public String getSmallText() {
        return getSmallTextInternal();
    }

    @Override
    public void publishSynchronous(Context context) {
        boolean cardValid = getContentCreatedDateInternal() != null &&
                            getContentSourceImageUriInternal() != null &&
                            !TextUtils.isEmpty(getTitle()) &&
                            !TextUtils.isEmpty(getBodyTextInternal()) &&
                            getAvatarImageUriInternal() != null;
        if (!cardValid) {
            throw new MissingFieldPublishException("Unable to publish card, not all required "
                                                   + "fields are present.");
        }
        super.publishSynchronous(context);
    }
}
