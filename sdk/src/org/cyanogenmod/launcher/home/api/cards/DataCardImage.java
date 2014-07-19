package org.cyanogenmod.launcher.home.api.cards;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContract;

public class DataCardImage {
    private int mId;
    private int mDataCardId;
    private Uri mImageUri;

    public DataCardImage(int dataCardId, Uri imageUri) {
        mDataCardId = dataCardId;
        mImageUri = imageUri;
    }

    private void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
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

    public void publish(Context context) {
        ContentResolver contentResolver  = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CmHomeContract.DataCardImage.DATA_CARD_ID_COL, getDataCardId());
        values.put(CmHomeContract.DataCardImage.IMAGE_URI_COL,
                   getImageUri().toString());
        Uri result = contentResolver.insert(CmHomeContract.DataCardImage.CONTENT_URI, values);
        // Store the resulting ID
        setId(Integer.parseInt(result.getLastPathSegment()));
    }
}
