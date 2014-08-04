package org.cyanogenmod.launcher.home.api.cards;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContentProvider;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContract;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DataCardImage extends PublishableCard {
    private final static String   TAG = "DataCardImage";
    private final static CmHomeContract.ICmHomeContract sContract
                                       = new CmHomeContract.DataCardImage();
    private long                  mDataCardId;
    private DataCard              mLinkedDataCard;
    private Uri                   mImageUri;
    private String                mInternalId;
    private String                mImageLabel;
    private WeakReference<Bitmap> mImageBitmap;
    private int                   mImageResourceId = 0;

    public DataCardImage(DataCard linkedDataCard) {
        super(sContract);

        mLinkedDataCard = linkedDataCard;
    }

    private DataCardImage(long dataCardId, Uri imageUri) {
        super(sContract);

        mDataCardId = dataCardId;
        mImageUri = imageUri;
    }

    public void setInternalId(String internalId) {
        mInternalId = internalId;
    }

    public String getInternalId() {
        return mInternalId;
    }

    public long getDataCardId() {
        return mDataCardId;
    }

    public void setDataCardId(long dataCardId) {
        mDataCardId = dataCardId;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public void setImage(Uri imageUri) {
        mImageUri = imageUri;

        // Drop all other image sources, only the last one assigned is preserved
        mImageBitmap = null;
        mImageResourceId = 0;
    }

    /**
     * Sets the image bitmap to the given bitmap.
     *
     * @param bitmap The Bitmap to save for this DataCardImage
     */
    public void setImage(Bitmap bitmap) {
        if (bitmap == null) return;
        mImageBitmap = new WeakReference<Bitmap>(bitmap);

        // Drop all other image sources, only the last one assigned is preserved
        mImageResourceId = 0;
        mImageUri = null;
    }

    public void setImage(int resource) {
        mImageResourceId = resource;

        // Drop all other image sources, only the last one assigned is preserved
        mImageBitmap = null;
        mImageUri = null;
    }

    public void setImageLabel(String imageLabel) {
        mImageLabel = imageLabel;
    }

    public String getImageLabel() {
        return mImageLabel;
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(CmHomeContract.DataCardImage.INTERNAL_ID_COL, getInternalId());
        values.put(CmHomeContract.DataCardImage.DATA_CARD_ID_COL, getDataCardId());
        values.put(CmHomeContract.DataCardImage.IMAGE_LABEL_COL, getImageLabel());

        if (getImageUri() != null) {
            values.put(CmHomeContract.DataCardImage.IMAGE_URI_COL, getImageUri().toString());
        }

        return values;
    }

    public static List<DataCardImage> getAllPublishedDataCardImages(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(CmHomeContract.DataCardImage.CONTENT_URI,
                                           CmHomeContract.DataCardImage.PROJECTION_ALL,
                                           null,
                                           null,
                                           null);
            // Catching all Exceptions, since we can't be sure what the extension will do.
        } catch (Exception e) {
            Log.e(TAG, "Error querying for DataCards, ContentProvider threw an exception for uri:" +
                       " " + CmHomeContract.DataCardImage.CONTENT_URI, e);
        }

        List<DataCardImage> allImages = getAllDataCardImagesFromCursor(cursor);
        cursor.close();
        return allImages;
    }

    private static List<DataCardImage> getAllDataCardImagesFromCursor(Cursor cursor) {
        List<DataCardImage> allImages = new ArrayList<DataCardImage>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int dataCardId = cursor.getInt(
                        cursor.getColumnIndex(CmHomeContract.DataCardImage.DATA_CARD_ID_COL));
                String imageUriString = cursor.getString(
                        cursor.getColumnIndex(CmHomeContract.DataCardImage.IMAGE_URI_COL));
                int imageId = cursor
                        .getInt(cursor.getColumnIndex(CmHomeContract.DataCardImage._ID));
                String internalId = cursor.getString(
                        cursor.getColumnIndex(CmHomeContract.DataCardImage.INTERNAL_ID_COL));
                String imageLabel = cursor.getString(
                        cursor.getColumnIndex(CmHomeContract.DataCardImage.IMAGE_LABEL_COL));

                if (!TextUtils.isEmpty(imageUriString)) {
                    DataCardImage image = new DataCardImage(dataCardId, Uri.parse(imageUriString));
                    image.setId(imageId);
                    image.setInternalId(internalId);
                    image.setImageLabel(imageLabel);
                    allImages.add(image);
                }
            }
        }
        return allImages;
    }

    public static List<DataCardImage> getPublishedDataCardImagesForDataCardId(Context context,
                                                                              long dataCardId) {
        return getPublishedDataCardImagesForDataCardId(context,
                                                       CmHomeContract.DataCardImage.CONTENT_URI,
                                                       dataCardId);
    }

    public static List<DataCardImage> getPublishedDataCardImagesForDataCardId(Context context,
                                                                              Uri contentUri,
                                                                              long dataCardId) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(contentUri,
                                           CmHomeContract.DataCardImage.PROJECTION_ALL,
                                           CmHomeContract.DataCardImage.DATA_CARD_ID_COL + " = ?",
                                           new String[]{Long.toString(dataCardId)},
                                           null);
            // Catching all Exceptions, since we can't be sure what the extension will do.
        } catch (Exception e) {
            Log.e(TAG, "Error querying for DataCards, ContentProvider threw an exception for uri:" +
                       " " + contentUri, e);
        }

        List<DataCardImage> allImages = getAllDataCardImagesFromCursor(cursor);
        cursor.close();
        return allImages;
    }


    protected boolean hasValidContent() {
        return mImageResourceId > 0 || mImageUri != null ||
                (mImageBitmap != null && mImageBitmap.get() != null);
    }

    @Override
    protected void publishSynchronous(Context context){
        // Store the current id of the linked DataCard, in case it has
        // changed before publish.
        if (mLinkedDataCard != null) {
            mDataCardId = mLinkedDataCard.getId();
        }

        if (mImageResourceId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), mImageResourceId);
            setImage(bitmap);
        }

        if (mImageBitmap != null && mImageBitmap.get() != null) {
            Uri uri = CmHomeContentProvider.storeBitmapInCache(mImageBitmap.get(), context);
            if (uri != null) {
                setImage(uri);
            }
        }

        super.publishSynchronous(context);
    }
}

