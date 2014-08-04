package org.cyanogenmod.launcher.home.api.cards;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataCardImage extends PublishableCard {
    private final static String   TAG = "DataCardImage";
    private final static CmHomeContract.ICmHomeContract sContract
                                       = new CmHomeContract.DataCardImage();
    public final static  String   IMAGE_FILE_CACHE_DIR = "DataCardImage";
    private long                  mDataCardId;
    private DataCard              mLinkedDataCard;
    private Uri                   mImageUri;
    private String                mInternalId;
    private String                mImageLabel;
    private WeakReference<Bitmap> mImageBitmap;

    public DataCardImage(DataCard linkedDataCard, Uri imageUri) {
        super(sContract);

        mLinkedDataCard = linkedDataCard;
        mImageUri = imageUri;
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
    }

    /**
     * Sets the image bitmap to the given bitmap.
     *
     * @param bitmap The Bitmap to save for this DataCardImage
     */
    public void setImage(Bitmap bitmap) {
        if (bitmap == null) return;
        mImageBitmap = new WeakReference<Bitmap>(bitmap);
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

    private void storeBitmapInCache(Bitmap bitmap, Context context) {
        String filename = UUID.randomUUID().toString() + ".png";
        FileOutputStream outputStream = null;
        try {
            // Create a file in the cache subdirectory
            File imageDir = new File(context.getFilesDir(), IMAGE_FILE_CACHE_DIR);
            imageDir.mkdirs();
            File imageFile = new File(imageDir, filename);
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            Uri imageUri = Uri.withAppendedPath(CmHomeContract.ImageFile.CONTENT_URI,
                                                filename);

            // Set the image URI, which will actually be stored in the database.
            setImage(imageUri);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Unable to save bitmap to temporary file.");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Unable to save bitmap to temporary file.");
                }
            }
        }
    }

    @Override
    public void publish(Context context){
        // Store the current id of the linked DataCard, in case it has
        // changed before publish.
        if (mLinkedDataCard != null) {
            mDataCardId = mLinkedDataCard.getId();
        }

        if (mImageBitmap.get() != null) {
            storeBitmapInCache(mImageBitmap.get(), context);
        }

        super.publish(context);
    }
}

