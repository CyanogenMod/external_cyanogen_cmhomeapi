package org.cyanogenmod.launcher.home.api.cards;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContentProvider;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContract;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>CardData contains data representing a single card that will appear in CM Home. This class
 * allows an application to create new cards, publish them to CM Home, remove them,
 * and receive callbacks when events related to cards occur.</p>
 *
 * <p>CardData includes many possible fields, and not all of them are required. The idea is that
 * an extension application can publish whatever data or content they have available,
 * and CM Home will do it's very best to find the best Card UI for the data available. At
 * minimum, a card should have a ContentCreatedDate and a Title.
 *
 * More Card types will be created in the future, so publish as much data as you have,
 * so that they will be used in rich ways within CM Home.
 * </p>
 *
 * <p>To publish a new Card to CM Home, just create a new CardData object,
 * set the desired fields, and call {@link #publish(android.content.Context)}. This publishes
 * your data in the ContentProvider backing CardData and notifies CM Home that a new card is
 * available.
 *
 * To query the currently published cards at any time, use
 * {@link #getAllPublishedCardDatas(android.content.Context)} to retrieve a list of CardData objects.
 *
 * To remove any card that is currently published, call
 * {@link #unpublish(android.content.Context)} on that card.</p>
 *
 * <p>CardData also contains a list of related images, retrievable in {@link #getImages()}. Any
 * images that have been added will be published when
 * {@link #publish(android.content.Context)} is called on a CardData object.
 *
 * @see org.cyanogenmod.launcher.home.api.cards.CardDataImage
 * </p>
 */
public class CardData extends PublishableCard {
    private static final String                         TAG       = "CardData";
    /**
     * Store a reference to the Database Contract that represents this object,
     * so that the superclass can figure out what columns to write.
     */
    private static final CmHomeContract.ICmHomeContract sContract =
            new CmHomeContract.CardDataContract();

    private String mInternalId;
    private String mReasonText;
    private Date   mContentCreatedDate;
    private Date   mCreatedDate;
    private Date   mLastModifiedDate;

    // Only one of these fields will be assigned at publish time
    private Uri                   mContentSourceImageUri;
    private WeakReference<Bitmap> mContentSourceImageBitmap;
    private int                   mContentSourceImageResourceId;

    private Uri                   mAvatarImageUri;
    private WeakReference<Bitmap> mAvatarImageBitmap;
    private int                   mAvatarImageResourceId;

    private String mTitle;
    private String mSmallText;
    private String mBodyText;
    private Intent mCardClickIntent;
    private String mAction1Text;
    private Intent mAction1Intent;
    private String mAction2Text;
    private Intent mAction2Intent;
    private Priority mPriority = Priority.MID;

    /**
     * The priority of this Card. Applications can report the priority of a card to hint to CM
     * Home where it should appear in the list of cards. There are three possibilities for
     * priority: low, medium and high.
     */
    public enum Priority {
        /**
         * The highest priority. This hints that this card should appear as high on the list as
         * possible.
         */
        HIGH(0),
        /**
         * Medium priority. This card will appear above low priority cards but below low priority
         * cards.
         */
        MID(1),
        /**
         * Low priority. This card will be shown at the bottom of the list.
         */
        LOW(2);

        private final int mValue;

        private Priority(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }

        public static Priority getModeForValue(int value) {
            switch (value) {
                case 0:
                    return HIGH;
                case 1:
                    return MID;
                case 2:
                    return LOW;
                default:
                    return MID;
            }
        }
    }

    /**
     * All associated CardDataImages for this CardData.
     */
    private List<CardDataImage> mImages = new ArrayList<CardDataImage>();

    private CardData() {
        super(sContract);
    }

    /**
     * Creates a new card that has only a title and content created date.
     * @param title The title string to display on the card.
     * @param contentCreatedDate The date that the content this card represents was created.
     */
    public CardData(String title, Date contentCreatedDate) {
        super(sContract);

        setTitle(title);
        setContentCreatedDate(contentCreatedDate);
    }

    /**
     * Add a related CardDataImage object to this CardData. Any images added will be considered
     * for display in CM Home as part of this card.
     * @param uri A URI to this image (all types, including internet resources, are allowed).
     */
    public void addCardDataImage(Uri uri) {
        CardDataImage image = new CardDataImage(this);
        image.setImage(uri);
        mImages.add(image);
    }

    /**
     * Adds a CardDataImage to this DataCard, if this CardDataImage is not already associated
     * with this image. If a CardDataImage is already associated with this CardData,
     * that instance is removed and replaced with this one.
     * @param newImage The CardDataImage instance to add or update.
     */
    public void addOrUpdateCardDataImage(CardDataImage newImage) {
        CardDataImage matchingImage = null;
        for (CardDataImage image : mImages) {
            if (image.getGlobalId().equals(newImage.getGlobalId())) {
                matchingImage = image;
                break;
            }
        }

        // Remove the old one, if it was found, it will be replaced
        if (matchingImage != null) {
            mImages.remove(matchingImage);
        }

        mImages.add(newImage);
    }

    /**
     * Set an internal ID for use in tracking the card. This should be a unique ID per card,
     * and can be any String. To be used by the app developer to track a Card,
     * as it relates to their own internal data. (e.g. a primary key in a third party database
     * table).
     * @param internalId  A string with which to identify this Card, unique among other CardData
     *                    instances.
     */
    public void setInternalId(String internalId) {
        mInternalId = internalId;
    }

    /**
     * Retrieves the currently set internal ID for this CardData.
     * @see org.cyanogenmod.launcher.home.api.cards.CardData#setInternalId(String)
     * @return The internal ID string for this CardData.
     */
    public String getInternalId() {
        return mInternalId;
    }

    /**
     * Sets the created date for this CardData. To be used internally as the actual creation date
     * should be enforced here.
     * @param date The actual date that this CardData was created.
     */
    private void setCreatedDate(Date date) {
        mCreatedDate = date;
    }

    /**
     * Sets the modified date for this CardData. To be used internally as the most recent
     * modified date as reported by the SQLite Database. A database trigger will set this for all
     * modified rows, and this value will be passed here.
     * @param date The actual date that this CardData was created.
     */
    private void setLastModifiedDate(Date date) {
        mLastModifiedDate = date;
    }

    /**
     * <p>Adds a new CardDataImage to this CardData. A CardData can be associated with any
     * number of images. When the Card is generated in CM Home, a suitable number of images will
     * be shown, depending on what is supported. For example, you can attach images related to a
     * news article or social media event.</p>
     *
     * <p>When this card is published, all added images will be published as well.</p>
     * @param image A CardDataImage to add to this CardData.
     */
    public void addCardDataImage(CardDataImage image) {
        mImages.add(image);
    }

    /**
     * Removes all associated images and unpublishes them.
     */
    public void clearImages() {
        mImages.clear();
    }

    public void removeCardDataImage(CardDataImage image) {
        mImages.remove(image);
    }

    public List<CardDataImage> getImages() {
        return mImages;
    }

    public Date getCreatedDate() {
        return mCreatedDate;
    }

    public String getReasonText() {
        return mReasonText;
    }

    public void setReasonText(String reason) {
        this.mReasonText = reason;
    }

    public Date getContentCreatedDate() {
        return mContentCreatedDate;
    }

    public void setContentCreatedDate(Date contentCreatedDate) {
        this.mContentCreatedDate = contentCreatedDate;
    }

    /**
     * Gets the modified date for this CardData. This will be the modified date as reported by the
     * SQLite Database. A database trigger will set this for all modified rows,
     * and this value will be passed here.
     */
    public Date getLastModifiedDate() {
        return mLastModifiedDate;
    }

    /**
     * Retrieve the Uri that points to the Content Source image.
     * @return A {@link android.net.Uri} object that will resolve to the image file for the
     * Content Source image.
     */
    public Uri getContentSourceImageUri() {
        return mContentSourceImageUri;
    }

    public void setContentSourceImage(Uri contentSourceImageUri) {
        this.mContentSourceImageUri = contentSourceImageUri;

        mContentSourceImageResourceId = 0;
        mContentSourceImageBitmap = null;
    }

    public void setContentSourceImage(Bitmap bitmap) {
        mContentSourceImageBitmap = new WeakReference<Bitmap>(bitmap);

        mAvatarImageResourceId = 0;
        mAvatarImageUri = null;
    }

    public void setContentSourceImage(int resourceId) {
        mContentSourceImageResourceId = resourceId;

        mContentSourceImageBitmap = null;
        mContentSourceImageUri = null;
    }

    public Uri getAvatarImageUri() {
        return mAvatarImageUri;
    }

    public void setAvatarImage(Uri avatarImageUri) {
        this.mAvatarImageUri = avatarImageUri;

        mAvatarImageResourceId = 0;
        mAvatarImageBitmap = null;
    }

    public void setAvatarImage(Bitmap bitmap) {
        mAvatarImageBitmap = new WeakReference<Bitmap>(bitmap);

        mAvatarImageResourceId = 0;
        mAvatarImageUri = null;
    }

    public void setAvatarImage(int resourceId) {
        mAvatarImageResourceId = resourceId;

        mAvatarImageBitmap = null;
        mAvatarImageUri = null;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getSmallText() {
        return mSmallText;
    }

    public void setSmallText(String smallText) {
        this.mSmallText = smallText;
    }

    public String getBodyText() {
        return mBodyText;
    }

    public void setBodyText(String bodyText) {
        this.mBodyText = bodyText;
    }

    public CardDataIntentInfo getCardClickIntentInfo() {
        return getCardDataIntentInfoForIntent(mCardClickIntent);
    }

    public void setCardClickIntent(Intent cardClickIntent, boolean isBroadcast) {
        mCardClickIntent = cardClickIntent;
        mCardClickIntent
                .putExtra(CmHomeContract.CardDataContract.IS_BROADCAST_INTENT_EXTRA, isBroadcast);
    }

    public String getAction1Text() {
        return mAction1Text;
    }

    public void setAction1Text(String action1Text) {
        this.mAction1Text = action1Text;
    }

    public CardDataIntentInfo getAction1IntentInfo() {
        return getCardDataIntentInfoForIntent(mAction1Intent);
    }

    public void setAction1Intent(Intent action1Intent, boolean isBroadcast) {
        this.mAction1Intent = action1Intent;
        mAction1Intent.putExtra(CmHomeContract.CardDataContract.IS_BROADCAST_INTENT_EXTRA, isBroadcast);
    }

    public String getAction2Text() {
        return mAction2Text;
    }

    public void setAction2Text(String action2Text) {
        this.mAction2Text = action2Text;
    }

    public CardDataIntentInfo getAction2IntentInfo() {
        return getCardDataIntentInfoForIntent(mAction2Intent);
    }

    public void setAction2Intent(Intent action2Intent, boolean isBroadcast) {
        this.mAction2Intent = action2Intent;
        mAction1Intent.putExtra(CmHomeContract.CardDataContract.IS_BROADCAST_INTENT_EXTRA, isBroadcast);
    }

    public Priority getPriority() {
        return mPriority;
    }

    private int getPriorityAsInt() {
        return mPriority.getValue();
    }

    private void setPriority(int value) {
        mPriority = Priority.getModeForValue(value);
    }

    public void setPriority(Priority priority) {
        this.mPriority = priority;
    }

    private CardDataIntentInfo getCardDataIntentInfoForIntent(Intent intent) {
        CardDataIntentInfo cardDataIntentInfo = null;
        if (intent != null) {
            boolean isBroadcast = isIntentBroadcast(intent);
            cardDataIntentInfo = new CardDataIntentInfo(isBroadcast, intent);
        }

        return cardDataIntentInfo;
    }

    @Override
    protected void publishSynchronous(Context context) {
        if (!isPublished(context)) {
            // Initialize the created date and modified date to now.
            mCreatedDate = new Date();
            mLastModifiedDate = new Date();
        }

        if (mContentSourceImageResourceId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                                                         mContentSourceImageResourceId);
            setContentSourceImage(bitmap);
        }

        if (mContentSourceImageBitmap != null && mContentSourceImageBitmap.get() != null) {
            Uri uri = CmHomeContentProvider.storeBitmapInCache(mContentSourceImageBitmap.get(),
                                                               context);
            if (uri != null) {
                setContentSourceImage(uri);
            }
        }

        if (mAvatarImageResourceId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                                                         mAvatarImageResourceId);
            setAvatarImage(bitmap);
        }

        if (mAvatarImageBitmap != null && mAvatarImageBitmap.get() != null) {
            Uri uri = CmHomeContentProvider.storeBitmapInCache(mAvatarImageBitmap.get(),
                                                               context);
            if (uri != null) {
                setAvatarImage(uri);
            }
        }

        super.publishSynchronous(context);

        for (CardDataImage image : mImages) {
            if (image.hasValidContent()) {
                image.publish(context);
            } else {
                Log.e(TAG, "Invalid CardDataImage. At least uri or bitmap must be specified");
            }
        }
    }

    /**
     * Updates an existing row in the ContentProvider that represents this card.
     * This will update every column at once.
     * @param context A Context object to retrieve the ContentResolver
     * @return true if the update successfully updates a row, false otherwise.
     */
    protected boolean update(Context context) {
        boolean updated = super.update(context);
        if (updated) {
            // Update all associated images as well
            for (CardDataImage image : mImages) {
                if (image.hasValidContent()) {
                    image.publish(context);
                } else {
                    Log.e(TAG, "Invalid CardDataImage. At least uri or bitmap must be specified");
                }
            }
        }

        return updated;
    }

    /**
     * Removes this CardData from the feed, so that it is no longer visible to the user.
     * @param context The context of the publishing application.
     * @return True if the card was successfully unpublished, false otherwise.
     */
    @Override
    public boolean unpublish(Context context) {
        // Delete all associated images first
        for (CardDataImage image : mImages) {
            image.unpublish(context);
        }
        return super.unpublish(context);
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(CmHomeContract.CardDataContract.INTERNAL_ID_COL, getInternalId());
        values.put(CmHomeContract.CardDataContract.REASON_COL, getReasonText());

        if (getContentCreatedDate() != null) {
            values.put(CmHomeContract.CardDataContract.DATE_CONTENT_CREATED_COL,
                       getContentCreatedDate().getTime());
        }

        if (getContentSourceImageUri() != null) {
            values.put(CmHomeContract.CardDataContract.CONTENT_SOURCE_IMAGE_URI_COL,
                       getContentSourceImageUri().toString());
        }

        if (getAvatarImageUri() != null) {
            values.put(CmHomeContract.CardDataContract.AVATAR_IMAGE_URI_COL,
                       getAvatarImageUri().toString());
        }

        values.put(CmHomeContract.CardDataContract.TITLE_TEXT_COL,
                   getTitle());
        values.put(CmHomeContract.CardDataContract.SMALL_TEXT_COL,
                   getSmallText());
        values.put(CmHomeContract.CardDataContract.BODY_TEXT_COL,
                   getBodyText());
        values.put(CmHomeContract.CardDataContract.ACTION_1_TEXT_COL,
                   getAction1Text());

        if (getAction1IntentInfo() != null) {
            values.put(CmHomeContract.CardDataContract.ACTION_1_URI_COL,
                       getAction1IntentInfo().getIntent().toUri(Intent.URI_INTENT_SCHEME)
                                             .toString());
        }

        values.put(CmHomeContract.CardDataContract.ACTION_2_TEXT_COL,
                   getAction2Text());

        if (getAction2IntentInfo() != null) {
            values.put(CmHomeContract.CardDataContract.ACTION_2_URI_COL,
                       getAction2IntentInfo().getIntent().
                               toUri(Intent.URI_INTENT_SCHEME).toString());
        }

        values.put(CmHomeContract.CardDataContract.PRIORITY_COL,
                   getPriorityAsInt());

        if (getCardClickIntentInfo() != null) {
            values.put(CmHomeContract.CardDataContract.CARD_CLICK_URI_COL,
                       getCardClickIntentInfo().getIntent().
                               toUri(Intent.URI_INTENT_SCHEME).toString());
        }

        return values;
    }

    public static List<CardData> getAllPublishedCardDatas(Context context) {
        return getAllPublishedCardDatas(context,
                                        CmHomeContract.CardDataContract.CONTENT_URI,
                                        CmHomeContract.CardDataImageContract.CONTENT_URI);
    }

    public static CardData createFromCurrentCursorRow(Cursor cursor, String authority) {
        CardData card = createFromCurrentCursorRow(cursor);
        card.setAuthority(authority);
        return card;
    }

    public static CardData createFromCurrentCursorRow(Cursor cursor) {
        CardData cardData = new CardData();

        cardData.setId(cursor.getInt(cursor.getColumnIndex(CmHomeContract.CardDataContract._ID)));
        cardData.setInternalId(cursor.getString(cursor.getColumnIndex(
                CmHomeContract.CardDataContract
                                                                      .INTERNAL_ID_COL)));
        long createdTime = cursor.getLong(cursor.getColumnIndex(CmHomeContract.CardDataContract
                                                                .DATE_CREATED_COL));
        cardData.setCreatedDate(new Date(createdTime));
        long modifiedTime = cursor.getLong(cursor.getColumnIndex(CmHomeContract.CardDataContract
                                                                 .LAST_MODIFIED_COL));
        cardData.setLastModifiedDate(new Date(modifiedTime));
        long contentCreatedTime = cursor.getLong(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.DATE_CONTENT_CREATED_COL));
        cardData.setContentCreatedDate(new Date(contentCreatedTime));
        cardData.setReasonText(cursor.getString(cursor.getColumnIndex(
                CmHomeContract.CardDataContract
                                                                           .REASON_COL)));
        String contentSourceUriString =
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.CardDataContract.CONTENT_SOURCE_IMAGE_URI_COL));

        if (!TextUtils.isEmpty(contentSourceUriString)) {
            cardData.setContentSourceImage(Uri.parse(contentSourceUriString));
        }

        String avatarImageUriString =
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.CardDataContract.AVATAR_IMAGE_URI_COL));
        if (!TextUtils.isEmpty(avatarImageUriString)) {
            cardData.setAvatarImage(Uri.parse(avatarImageUriString));
        }

        cardData.setTitle(cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.TITLE_TEXT_COL)));
        cardData.setSmallText(
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.CardDataContract.SMALL_TEXT_COL)));
        cardData.setBodyText(cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.BODY_TEXT_COL)));
        cardData.setAction1Text(
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.CardDataContract.ACTION_1_TEXT_COL)));

        String clickActionUriString = cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.CARD_CLICK_URI_COL));
        if (!TextUtils.isEmpty(clickActionUriString)) {
            try {
                Intent cardClickIntent = Intent.parseUri(clickActionUriString,
                                                         Intent.URI_INTENT_SCHEME);
                cardData.setCardClickIntent(cardClickIntent, isIntentBroadcast(cardClickIntent));
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to parse uri to Intent: " + clickActionUriString);
            }
        }

        String action1UriString = cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.ACTION_1_URI_COL));
        if (!TextUtils.isEmpty(action1UriString)) {
            try {
                Intent action1Intent = Intent.parseUri(action1UriString,
                                                          Intent.URI_INTENT_SCHEME);
                cardData.setAction1Intent(action1Intent,
                                          isIntentBroadcast(action1Intent));
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to parse uri to Intent: " + action1UriString);
            }
        }

        cardData.setAction2Text(cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.ACTION_2_TEXT_COL)));

        String action2UriString = cursor.getString(
                cursor.getColumnIndex(CmHomeContract.CardDataContract.ACTION_2_URI_COL));
        if (!TextUtils.isEmpty(action2UriString)) {
            try {
                Intent action2Intent = Intent.parseUri(action2UriString,
                                                          Intent.URI_INTENT_SCHEME);
                cardData.setAction2Intent(action2Intent,
                                          isIntentBroadcast(action2Intent));
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to parse uri to Intent: " + action2UriString);
            }
        }

        int priority = cursor.getInt(cursor.getColumnIndex(CmHomeContract.CardDataContract
                                                                        .PRIORITY_COL));
        cardData.setPriority(priority);

        return cardData;
    }

    private static boolean isIntentBroadcast(Intent intent) {
        boolean isBroadcast = false;
        if (intent != null) {
            isBroadcast = intent.getBooleanExtra(CmHomeContract.CardDataContract.IS_BROADCAST_INTENT_EXTRA,
                                                 false);
        }
        return isBroadcast;
    }

    public static List<CardData> getAllPublishedCardDatas(Context context,
                                                          Uri cardDataContentUri,
                                                          Uri cardDataImageContentUri) {
        ContentResolver contentResolver = context.getContentResolver();
        List<CardData> allCards = new ArrayList<CardData>();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(cardDataContentUri,
                                           CmHomeContract.CardDataContract.PROJECTION_ALL,
                                           null,
                                           null,
                                           CmHomeContract.CardDataContract.DATE_CREATED_COL);
        // Catching all Exceptions, since we can't be sure what the extension will do.
        } catch (Exception e) {
            Log.e(TAG, "Error querying for CardDatas, ContentProvider threw an exception for uri:" +
                       " " + cardDataContentUri, e);
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                CardData cardData = createFromCurrentCursorRow(cursor,
                                                               cardDataContentUri.getAuthority());
                allCards.add(cardData);
            }

            cursor.close();
        }


        // Retrieve all CardDataImages for each CardData.
        // Doing this in a separate loop since each iteration
        // will also be querying the ContentProvider.
        for (CardData card : allCards) {
            List<CardDataImage> images = CardDataImage
                    .getPublishedCardDataImagesForCardDataId(context,
                                                             cardDataImageContentUri,
                                                             card.getId());
            for (CardDataImage image : images) {
                card.addCardDataImage(image);
            }
        }

        return allCards;
    }

    /**
     * A wrapper class that contains information about a CardData related intent,
     * as well as the Intent itself.
     */
    public class CardDataIntentInfo {
        private boolean mIsBroadcast;
        private Intent mIntent;

        public CardDataIntentInfo(boolean isBroadcast, Intent theIntent) {
            mIsBroadcast = isBroadcast;
            mIntent = theIntent;
        }

        public boolean isBroadcast() {
            return mIsBroadcast;
        }

        public Intent getIntent() {
            return mIntent;
        }
    }

    public static final class CardDeletedInfo implements Parcelable {
        private long   mId;
        private String mInternalId;
        private String mGlobalId;
        private String mAuthority;

        public CardDeletedInfo(long id, String internalId, String globalId, String authority) {
            setId(id);
            setInternalId(internalId);
            setGlobalId(globalId);
            setAuthority(authority);
        }

        private CardDeletedInfo(Parcel in) {
            mId = in.readLong();
            mInternalId = in.readString();
            mGlobalId = in.readString();
            mAuthority = in.readString();
        }

        protected void setId(long id) {
            mId = id;
        }

        public long getId() {
            return mId;
        }

        protected void setInternalId(String internalId) {
            mInternalId = internalId;
        }

        public String getInternalId() {
            return mInternalId;
        }

        protected void setGlobalId(String globalId) {
            mGlobalId = globalId;
        }

        public String getGlobalId() {
            return mGlobalId;
        }

        protected void setAuthority(String authority) {
            mAuthority = authority;
        }

        public String getAuthority() {
            return mAuthority;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(mId);
            parcel.writeString(mInternalId);
            parcel.writeString(mGlobalId);
            parcel.writeString(mAuthority);
        }

        public static final Creator<CardDeletedInfo> CREATOR =
                new Creator<CardDeletedInfo>() {
                    public CardDeletedInfo createFromParcel(Parcel in) {
                        return new CardDeletedInfo(in);
                    }

                    public CardDeletedInfo[] newArray(int size) {
                        return new CardDeletedInfo[size];
                    }
        };
    }
}
