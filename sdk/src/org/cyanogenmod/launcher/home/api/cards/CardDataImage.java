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

public class CardDataImage extends PublishableCard {
    private final static String                         TAG = "CardDataImage";
    private final static CmHomeContract.ICmHomeContract sContract
                                                            = new CmHomeContract.CardDataImageContract();
    private long                  mCardDataId;
    private CardData              mLinkedCardData;
    private Uri                   mImageUri;
    private String                mInternalId;
    private String                mImageLabel;
    private WeakReference<Bitmap> mImageBitmap;
    private int mImageResourceId = 0;

    public CardDataImage(CardData linkedCardData) {
        super(sContract);

        mLinkedCardData = linkedCardData;
    }

    private CardDataImage(long cardDataId, Uri imageUri) {
        super(sContract);

        mCardDataId = cardDataId;
        mImageUri = imageUri;
    }

    public void setInternalId(String internalId) {
        mInternalId = internalId;
    }

    public String getInternalId() {
        return mInternalId;
    }

    public long getCardDataId() {
        return mCardDataId;
    }

    public void setCardDataId(long cardDataId) {
        mCardDataId = cardDataId;
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
     * @param bitmap The Bitmap to save for this CardDataImage
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

        values.put(CmHomeContract.CardDataImageContract.INTERNAL_ID_COL, getInternalId());
        values.put(CmHomeContract.CardDataImageContract.CARD_DATA_ID_COL, getCardDataId());
        values.put(CmHomeContract.CardDataImageContract.IMAGE_LABEL_COL, getImageLabel());

        if (getImageUri() != null) {
            values.put(CmHomeContract.CardDataImageContract.IMAGE_URI_COL, getImageUri().toString());
        }

        return values;
    }

    public static List<CardDataImage> getAllPublishedCardDataImages(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(CmHomeContract.CardDataImageContract.CONTENT_URI,
                                           CmHomeContract.CardDataImageContract.PROJECTION_ALL,
                                           null,
                                           null,
                                           null);
            // Catching all Exceptions, since we can't be sure what the extension will do.
        } catch (Exception e) {
            Log.e(TAG, "Error querying for CardDatas, ContentProvider threw an exception for uri:" +
                       " " + CmHomeContract.CardDataImageContract.CONTENT_URI, e);
        }

        List<CardDataImage> allImages = getAllCardDataImagesFromCursor(cursor);
        cursor.close();
        return allImages;
    }

    private static List<CardDataImage> getAllCardDataImagesFromCursor(Cursor cursor) {
        List<CardDataImage> allImages = new ArrayList<CardDataImage>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int cardDataId = cursor.getInt(
                        cursor.getColumnIndex(CmHomeContract.CardDataImageContract.CARD_DATA_ID_COL));
                String imageUriString = cursor.getString(
                        cursor.getColumnIndex(CmHomeContract.CardDataImageContract.IMAGE_URI_COL));
                int imageId = cursor
                        .getInt(cursor.getColumnIndex(CmHomeContract.CardDataImageContract._ID));
                String internalId = cursor.getString(
                        cursor.getColumnIndex(CmHomeContract.CardDataImageContract.INTERNAL_ID_COL));
                String imageLabel = cursor.getString(
                        cursor.getColumnIndex(CmHomeContract.CardDataImageContract.IMAGE_LABEL_COL));

                if (!TextUtils.isEmpty(imageUriString)) {
                    CardDataImage image = new CardDataImage(cardDataId, Uri.parse(imageUriString));
                    image.setId(imageId);
                    image.setInternalId(internalId);
                    image.setImageLabel(imageLabel);
                    allImages.add(image);
                }
            }
        }
        return allImages;
    }

    public static List<CardDataImage> getPublishedCardDataImagesForCardDataId(Context context,
                                                                              long cardDataId) {
        return getPublishedCardDataImagesForCardDataId(context,
                                                       CmHomeContract.CardDataImageContract.CONTENT_URI,
                                                       cardDataId);
    }

    public static List<CardDataImage> getPublishedCardDataImagesForCardDataId(Context context,
                                                                              Uri contentUri,
                                                                              long cardDataId) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(contentUri,
                                           CmHomeContract.CardDataImageContract.PROJECTION_ALL,
                                           CmHomeContract.CardDataImageContract.CARD_DATA_ID_COL + " = ?",
                                           new String[]{Long.toString(cardDataId)},
                                           null);
            // Catching all Exceptions, since we can't be sure what the extension will do.
        } catch (Exception e) {
            Log.e(TAG, "Error querying for CardDatas, ContentProvider threw an exception for uri:" +
                       " " + contentUri, e);
        }

        List<CardDataImage> allImages = getAllCardDataImagesFromCursor(cursor);
        cursor.close();
        return allImages;
    }


    protected boolean hasValidContent() {
        return mImageResourceId > 0 || mImageUri != null ||
                (mImageBitmap != null && mImageBitmap.get() != null);
    }

    @Override
    protected void publishSynchronous(Context context){
        // Store the current id of the linked CardData, in case it has
        // changed before publish.
        if (mLinkedCardData != null) {
            mCardDataId = mLinkedCardData.getId();
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

