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
public class NewsCardData extends CardData {

    public NewsCardData(String title, Date contentCreatedDate) {
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

    public void setThumbnailImage(CardDataImage cardDataImage) {
        clearImagesInternal();
        addCardDataImageInternal(cardDataImage);
    }

    public void removeThumbnailImage() {
        clearImagesInternal();
    }

    public CardDataImage getThumbnailImage() {
        if (getImagesInternal().size() > 0) {
            return getImagesInternal().get(0);
        } else {
            return null;
        }
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
                            !TextUtils.isEmpty(getSmallTextInternal());
        if (!cardValid) {
            throw new MissingFieldPublishException("Unable to publish card, not all required "
                                                   + "fields are present.");
        }
        super.publishSynchronous(context);
    }
}
