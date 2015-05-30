package org.cyanogenmod.launcher.home.api.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

/**
 * Created by matt on 5/30/15.
 */
public class PhotoCardData extends CardData {

    public PhotoCardData(String title, Date contentCreatedDate) {
        super(title, contentCreatedDate);
    }

    public PhotoCardData(CardData cardData) {
        super(cardData);
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

    public Uri getAvatarImageUri()  {
        return getAvatarImageUriInternal();
    }

    public void setPhoto(CardDataImage cardDataImage) {
        clearImagesInternal();
        addCardDataImageInternal(cardDataImage);
    }

    public void clearPhoto() {
        clearImagesInternal();
    }

    public CardDataImage getPhoto() {
        if (getImages().size() > 0) {
            return getImages().get(0);
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
        boolean cardValid =
                getContentCreatedDate() != null &&
                (getContentSourceImageUriInternal() != null
                        || getContentSourceImageResourceIdInternal() > 0
                        || getContentSourceImageBitmapInternal() != null) &&
                !TextUtils.isEmpty(getTitle()) &&
                getAvatarImageUriInternal() != null &&
                !getImages().isEmpty();
        if (!cardValid) {
            throw new MissingFieldPublishException("Unable to publish card, not all required "
                                                   + "fields are present.");
        }
        super.publishSynchronous(context);
    }
}
