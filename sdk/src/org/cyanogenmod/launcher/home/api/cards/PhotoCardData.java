package org.cyanogenmod.launcher.home.api.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by matt on 5/30/15.
 */
public class PhotoCardData extends CardData {

    public PhotoCardData(String title, Date contentCreatedDate) {
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

    public void setPhoto(CardDataImage cardDataImage) {
        clearImagesInternal();
        addCardDataImageInternal(cardDataImage);
    }

    public void clearPhoto() {
        clearImagesInternal();
    }

    public CardDataImage getPhoto() {
        if (getImagesInternal().size() > 0) {
            return getImagesInternal().get(0);
        } else {
            return null;
        }
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
                            getAvatarImageUriInternal() != null &&
                            !getImagesInternal().isEmpty();
        if (!cardValid) {
            throw new MissingFieldPublishException("Unable to publish card, not all required "
                                                   + "fields are present.");
        }
        super.publishSynchronous(context);
    }
}
