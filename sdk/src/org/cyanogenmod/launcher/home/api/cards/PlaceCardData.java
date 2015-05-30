package org.cyanogenmod.launcher.home.api.cards;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;

/**
 * Created by matt on 5/28/15.
 */
public class PlaceCardData extends CardData {
    /**
     * @hide
     */
    static final String KEY_PLACE_CARD = "place_card";

    public PlaceCardData(String title, Date contentCreatedDate) {
        super(title, contentCreatedDate);
        setInternalId(KEY_PLACE_CARD);
    }

    public PlaceCardData(CardData cardData) {
        super(cardData);
        setInternalId(KEY_PLACE_CARD);
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

    public void setReasonText(String reasonText) {
        setReasonTextInternal(reasonText);
    }

    public String getReasonText() {
        return getReasonTextInternal();
    }

    public void setBodyText(String bodyText) {
        setBodyTextInternal(bodyText);
    }

    public String getBodyText() {
        return getBodyTextInternal();
    }

    public void setThumbnailImage(CardDataImage cardDataImage) {
        clearImagesInternal();
        addCardDataImageInternal(cardDataImage);
    }

    public void removeThumbnailImage() {
        clearImagesInternal();
    }

    public CardDataImage getThumbnailImage() {
        if (getImages().size() > 0) {
            return getImages().get(0);
        } else {
            return null;
        }
    }

    @Override
    public void publishSynchronous(Context context) {
        // Override the internal ID, a place card must use this value.
        setInternalId(KEY_PLACE_CARD);
        super.publishSynchronous(context);
    }
}