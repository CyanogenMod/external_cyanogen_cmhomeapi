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

/**
 * Class representing an image that should be displayed along with a parent
 * {@link org.cyanogenmod.launcher.home.api.cards.CardData}. Any number of CardDataImage objects
 * can be associated with a CardData, and as many as possible will attempt to be displayed with
 * the Card in CM Home.
 *
 * As with {@link org.cyanogenmod.launcher.home.api.cards.CardData},
 * a CardDataImage will be published when
 * {@link org.cyanogenmod.launcher.home.api.cards.CardDataImage#publish(Context)} is called.
 * CardDataImage objects can similarly be removed from view by calling
 * {@link org.cyanogenmod.launcher.home.api.cards.CardDataImage#unpublish(android.content.Context)}.
 */
public class CardDataImage extends PublishableCard {
    private final static String TAG = "CardDataImage";
    /**
     * Store a reference to the Database Contract that represents this object,
     * so that the superclass can figure out what columns to write.
    */
    private final static CmHomeContract.ICmHomeContract sContract =
                                        new CmHomeContract.CardDataImageContract();
    private long                  mCardDataId;
    private CardData              mLinkedCardData;
    private Uri                   mImageUri;
    private String                mInternalId;
    private String                mImageLabel;
    private WeakReference<Bitmap> mImageBitmap;
    private int                   mImageResourceId = 0;

    /**
     * Create a new CardDataImage by passing in a parent {@link CardData} that this
     * CardDataImage will be linked to.
     * @param linkedCardData The CardData that will be the parent of this CardImageData.
     */
    public CardDataImage(CardData linkedCardData) {
        super(sContract);

        mLinkedCardData = linkedCardData;
    }

    /**
     * Construct a new CardDataImage using the given Uri as the image source for this image.
     * @param imageUri A Uri that resolves to the image that this CardDataImage will display.
     * @param linkedCardData The CardData that will be the parent of this CardImageData.
     */
    public CardDataImage(Uri imageUri, CardData linkedCardData) {
        super(sContract);

        mImageUri = imageUri;
        mLinkedCardData = linkedCardData;
    }

    private CardDataImage(long cardDataId, Uri imageUri) {
        super(sContract);

        mCardDataId = cardDataId;
        mImageUri = imageUri;
    }

    /**
     * Sets the ID field that should be used for a unique string that can identify this Card
     * within your application. This field is optional and can be used for internal tracking
     * within this application.
     * @param internalId The String to be used to uniquely identify this card.
     */
    public void setInternalId(String internalId) {
        mInternalId = internalId;
    }

    /**
     * Retrieves the currenly set internal ID.
     * @see org.cyanogenmod.launcher.home.api.cards.CardDataImage#setInternalId(java.lang.String)
     * @return The internal ID String that is currently set for this CardDataImage.
     */
    public String getInternalId() {
        return mInternalId;
    }

    /**
     * Retrieves the ID for this CardDataImage. This is the primary key of this object within
     * this application.
     * @return The ID of this CardDataImage within this application.
     */
    public long getCardDataId() {
        return mCardDataId;
    }

    /**
     * Sets the ID of the {@link org.cyanogenmod.launcher.home.api.cards.CardData} that this
     * image is associated with. This required field tells CM Home which Card this image will be
     * displayed with.
     * @param cardDataId The ID of the Card that this image is a child of, as returned by
     *                   {@link org.cyanogenmod.launcher.home.api.cards.CardData#getId()}.
     */
    private void setCardDataId(long cardDataId) {
        mCardDataId = cardDataId;
    }

    /**
     * Retrieves the Uri that resolves to the image that this CardDataImage will display.
     * @return The Uri that resolves to the image this object represents.
     */
    public Uri getImageUri() {
        return mImageUri;
    }

    /**
     * Sets the Uri that resolves to the image that this CardDataImage will display.
     * @param imageUri A URI to this image (all types, including internet resources, are allowed).
     */
    public void setImage(Uri imageUri) {
        mImageUri = imageUri;

        // Drop all other image sources, only the last one assigned is preserved
        mImageBitmap = null;
        mImageResourceId = 0;
    }

    /**
     * Sets the image bitmap to the given bitmap.
     *
     * <p>When {@link org.cyanogenmod.launcher.home.api.cards.CardDataImage#publish(Context)} is
     * called on this CardDataImage, this bitmap is saved to a cache within the internal storage for
     * this application, to be accessed only by CM Home through a ContentProvider. If the same
     * Bitmap is passed for any other image, the same cached image will be used. </p>
     *
     * @param bitmap The Bitmap to save for this CardDataImage.
     */
    public void setImage(Bitmap bitmap) {
        if (bitmap == null) return;
        mImageBitmap = new WeakReference<Bitmap>(bitmap);

        // Drop all other image sources, only the last one assigned is preserved
        mImageResourceId = 0;
        mImageUri = null;
    }

    /**
     * Sets the image to the image that the given resource ID resolves to.
     *
     * <p>When {@link org.cyanogenmod.launcher.home.api.cards.CardDataImage#publish(Context)} is
     * called on this CardDataImage, this image is saved to a cache within the internal storage for
     * this application, to be accessed only by CM Home through a ContentProvider. If the same
     * Bitmap is passed for any other image, the same cached image will be used. </p>
     *
     * @param resource The resource to save for this CardDataImage.
     */
    public void setImage(int resource) {
        mImageResourceId = resource;

        // Drop all other image sources, only the last one assigned is preserved
        mImageBitmap = null;
        mImageUri = null;
    }

    /**
     * Sets a String that will be used to identify this image.
     *
     * This may be used for a caption or title for this image.
     * @param imageLabel A String that identifies this image.
     */
    public void setImageLabel(String imageLabel) {
        mImageLabel = imageLabel;
    }

    /**
     * Retrieves the image label for this image.
     *
     * @see org.cyanogenmod.launcher.home.api.cards.CardDataImage#setImageLabel(java.lang.String)
     * @return The currently set Image label String.
     */
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

    /**
     * Retrieves all currently published CardDataImages for this application.
     * @param context A Context of the application that published the CardDataImages originally.
     * @return A list of all currently live and published CardDataImages for this application only.
     */
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
                CardDataImage image = createFromCurrentCursorRow(cursor);
                if (image != null) {
                    allImages.add(image);
                }
            }
        }
        return allImages;
    }

    /**
     * @hide
     *
     * Creates a new CardDataImage from the data row that the input cursor currently points to.
     *
     * <b>This is intended to be an internal method. Please use one of the helper methods to
     * retrieve CardDataImages.</b>
     * @param cursor A Cursor object that currently points to a row with CardDataImage data.
     * @param authority The authority of the ContentProvider that hosts the ContentProvider being
     *                  read from.
     * @return A CardDataImage containing the data from the input cursor row.
     */
    public static CardDataImage createFromCurrentCursorRow(Cursor cursor, String authority) {
        CardDataImage cardImage = createFromCurrentCursorRow(cursor);
        cardImage.setAuthority(authority);
        return cardImage;
    }

    private static CardDataImage createFromCurrentCursorRow(Cursor cursor) {
        // Can't work with a cursor in this state
        if (cursor.isClosed() || cursor.isAfterLast()) return null;

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
            return image;
        }

        return null;
    }

    /**
     * Retrieve a list of all currently published CardDataImages that have the {@link CardData}
     * given by cardDataId as a parent.
     * @param context The Context of the application that published the CardDataImages originally.
     * @param cardDataId The ID of the {@link CardData} that is the parent of the CardDataImages
     *                   being queried for.
     * @return A list of CardDataImages that have the given {@link CardData} as a parent.
     */
    public static List<CardDataImage> getPublishedCardDataImagesForCardDataId(Context context,
                                                                              long cardDataId) {
        return getPublishedCardDataImagesForCardDataId(context,
                                                       CmHomeContract.CardDataImageContract.CONTENT_URI,
                                                       cardDataId);
    }

    /**
     * @hide
     *
     * Retrieve a list of currently published DataCardImages that have the CardData
     * given by cardDataId as a parent.
     *
     * <b>This is intended to be an internal method. Please use one of the helper methods to
     * retrieve CardDataImages.</b>
     * @param context The Context of the application that published the CardDataImages originally.
     * @param contentUri The ContentUri of the images being queried for.
     * @param cardDataId The ID of the {@link CardData} that is the parent of the CardDataImages
     *                   being queried for.
     * @return A list of CardDataImages that have the given {@link CardData} as a parent.
     */
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

