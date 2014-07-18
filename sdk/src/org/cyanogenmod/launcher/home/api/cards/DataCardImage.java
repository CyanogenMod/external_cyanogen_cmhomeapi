package org.cyanogenmod.launcher.home.api.cards;

import android.net.Uri;

public class DataCardImage {
    private int mDataCardId;
    private Uri mImageUri;

    public DataCardImage(int dataCardId, Uri imageUri) {
        mDataCardId = dataCardId;
        mImageUri = imageUri;
    }

    public int getDataCardId() {
        return mDataCardId;
    }

    public void setDataCardId(int dataCardId) {
        mDataCardId = dataCardId;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public void setImageUri(Uri imageUri) {
        mImageUri = imageUri;
    }
}
