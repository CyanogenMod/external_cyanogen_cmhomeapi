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
public class NewsCardData extends CardData {

    public NewsCardData(String title, Date contentCreatedDate) {
        super(title, contentCreatedDate);
    }

    public NewsCardData(CardData cardData) {
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

    public void setBodyText(String message) {
        setBodyTextInternal(message);
    }

    public String getBodyText() {
        return getBodyTextInternal();
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


    @Override
    protected boolean isValid() {
        boolean cardValid =
                getContentCreatedDate() != null &&
                (getContentSourceImageUriInternal() != null
                        || getContentSourceImageResourceIdInternal() > 0
                        || getContentSourceImageBitmapInternal() != null) &&
                !TextUtils.isEmpty(getTitle()) &&
                !TextUtils.isEmpty(getSmallTextInternal());
        return cardValid;
    }
}
