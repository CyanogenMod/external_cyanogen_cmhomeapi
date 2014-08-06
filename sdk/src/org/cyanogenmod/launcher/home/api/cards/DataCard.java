package org.cyanogenmod.launcher.home.api.cards;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContentProvider;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContract;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataCard extends PublishableCard {
    private static final String TAG = "DataCard";
    private static final CmHomeContract.ICmHomeContract sContract =
            new CmHomeContract.DataCard();

    private String mInternalId;
    private String mReasonText;
    private Date   mContentCreatedDate;
    private Date   mCreatedDate;
    private Date   mLastModifiedDate;

    private Uri    mContentSourceImageUri;
    private WeakReference<Bitmap> mContentSourceImageBitmap;
    private int    mContentSourceImageResourceId;

    private Uri    mAvatarImageUri;
    private WeakReference<Bitmap> mAvatarImageBitmap;
    private int    mAvatarImageResourceId;

    private String mTitle;
    private String mSmallText;
    private String mBodyText;
    private Intent mCardClickIntent;
    private String mAction1Text;
    private Intent mAction1Intent;
    private String mAction2Text;
    private Intent mAction2Intent;
    private Priority mPriority = Priority.MID;

    public enum Priority {
        HIGH(0),
        MID(1),
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

    private List<DataCardImage> mImages = new ArrayList<DataCardImage>();

    private DataCard() {
        super(sContract);
    }

    public DataCard(String title, Date contentCreatedDate) {
        super(sContract);

        setTitle(title);
        setContentCreatedDate(contentCreatedDate);
    }

    public void addDataCardImage(Uri uri) {
        DataCardImage image = new DataCardImage(this);
        image.setImage(uri);
        mImages.add(image);
    }

    public void setInternalId(String internalId) {
        mInternalId = internalId;
    }

    public String getInternalId() {
        return mInternalId;
    }

    private void setCreatedDate(Date date) {
        mCreatedDate = date;
    }

    private void setLastModifiedDate(Date date) {
        mLastModifiedDate = date;
    }

    public void addDataCardImage(DataCardImage image) {
        mImages.add(image);
    }

    public void clearImages() {
        mImages.clear();
    }

    public void removeDataCardImage(DataCardImage image) {
        mImages.remove(image);
    }

    public List<DataCardImage> getImages() {
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

    public Date getLastModifiedDate() {
        return mLastModifiedDate;
    }

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

    public DataCardIntentInfo getCardClickIntentInfo() {
        return getDataCardIntentInfoForIntent(mCardClickIntent);
    }

    public void setCardClickIntent(Intent cardClickIntent, boolean isBroadcast) {
        mCardClickIntent = cardClickIntent;
        mCardClickIntent.putExtra(CmHomeContract.DataCard.IS_BROADCAST_INTENT_EXTRA, isBroadcast);
    }

    public String getAction1Text() {
        return mAction1Text;
    }

    public void setAction1Text(String action1Text) {
        this.mAction1Text = action1Text;
    }

    public DataCardIntentInfo getAction1IntentInfo() {
        return getDataCardIntentInfoForIntent(mAction1Intent);
    }

    public void setAction1Intent(Intent action1Intent, boolean isBroadcast) {
        this.mAction1Intent = action1Intent;
        mAction1Intent.putExtra(CmHomeContract.DataCard.IS_BROADCAST_INTENT_EXTRA, isBroadcast);
    }

    public String getAction2Text() {
        return mAction2Text;
    }

    public void setAction2Text(String action2Text) {
        this.mAction2Text = action2Text;
    }

    public DataCardIntentInfo getAction2IntentInfo() {
        return getDataCardIntentInfoForIntent(mAction2Intent);
    }

    public void setAction2Intent(Intent action2Intent, boolean isBroadcast) {
        this.mAction2Intent = action2Intent;
        mAction1Intent.putExtra(CmHomeContract.DataCard.IS_BROADCAST_INTENT_EXTRA, isBroadcast);
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

    private DataCardIntentInfo getDataCardIntentInfoForIntent(Intent intent) {
        DataCardIntentInfo dataCardIntentInfo = null;
        if (intent != null) {
            boolean isBroadcast = isIntentBroadcast(intent);
            dataCardIntentInfo = new DataCardIntentInfo(isBroadcast, intent);
        }

        return dataCardIntentInfo;
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

        for (DataCardImage image : mImages) {
            if (image.hasValidContent()) {
                image.publish(context);
            } else {
                Log.e(TAG, "Invalid DataCardImage. At least uri or bitmap must be specified");
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
            for (DataCardImage image : mImages) {
                if (image.hasValidContent()) {
                    image.publish(context);
                } else {
                    Log.e(TAG, "Invalid DataCardImage. At least uri or bitmap must be specified");
                }
            }
        }

        return updated;
    }

    /**
     * Removes this DataCard from the feed, so that it is no longer visible to the user.
     * @param context The context of the publishing application.
     * @return True if the card was successfully unpublished, false otherwise.
     */
    @Override
    public boolean unpublish(Context context) {
        // Delete all associated images as well
        for (DataCardImage image : mImages) {
            image.unpublish(context);
        }

        Uri deleteUri = sContract.getContentUri();
        Uri.Builder builder = deleteUri.buildUpon();
        builder.appendQueryParameter(CardDeletedInfo.ID_QUERY_PARAM,
                                     Long.toString(getId()));
        builder.appendQueryParameter(CardDeletedInfo.INTERNAL_ID_QUERY_PARAM,
                                     getInternalId());
        builder.appendQueryParameter(CardDeletedInfo.GLOBAL_ID_QUERY_PARAM,
                                     getGlobalId());
        builder.appendQueryParameter(CardDeletedInfo.AUTHORITY_QUERY_PARAM,
                                     getAuthority());

        return super.unpublish(context, builder.build());
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(CmHomeContract.DataCard.INTERNAL_ID_COL, getInternalId());
        values.put(CmHomeContract.DataCard.REASON_COL, getReasonText());

        if (getContentCreatedDate() != null) {
            values.put(CmHomeContract.DataCard.DATE_CONTENT_CREATED_COL,
                       getContentCreatedDate().getTime());
        }

        if (getContentSourceImageUri() != null) {
            values.put(CmHomeContract.DataCard.CONTENT_SOURCE_IMAGE_URI_COL,
                       getContentSourceImageUri().toString());
        }

        if (getAvatarImageUri() != null) {
            values.put(CmHomeContract.DataCard.AVATAR_IMAGE_URI_COL,
                       getAvatarImageUri().toString());
        }

        values.put(CmHomeContract.DataCard.TITLE_TEXT_COL,
                   getTitle());
        values.put(CmHomeContract.DataCard.SMALL_TEXT_COL,
                   getSmallText());
        values.put(CmHomeContract.DataCard.BODY_TEXT_COL,
                   getBodyText());
        values.put(CmHomeContract.DataCard.ACTION_1_TEXT_COL,
                   getAction1Text());

        if (getAction1IntentInfo() != null) {
            values.put(CmHomeContract.DataCard.ACTION_1_URI_COL,
                       getAction1IntentInfo().getIntent().toUri(Intent.URI_INTENT_SCHEME)
                                             .toString());
        }

        values.put(CmHomeContract.DataCard.ACTION_2_TEXT_COL,
                   getAction2Text());

        if (getAction2IntentInfo() != null) {
            values.put(CmHomeContract.DataCard.ACTION_2_URI_COL,
                       getAction2IntentInfo().getIntent().
                               toUri(Intent.URI_INTENT_SCHEME).toString());
        }

        values.put(CmHomeContract.DataCard.PRIORITY_COL,
                   getPriorityAsInt());

        if (getCardClickIntentInfo() != null) {
            values.put(CmHomeContract.DataCard.CARD_CLICK_URI_COL,
                       getCardClickIntentInfo().getIntent().
                               toUri(Intent.URI_INTENT_SCHEME).toString());
        }

        return values;
    }

    public static List<DataCard> getAllPublishedDataCards(Context context) {
        return getAllPublishedDataCards(context,
                                        CmHomeContract.DataCard.CONTENT_URI,
                                        CmHomeContract.DataCardImage.CONTENT_URI);
    }

    public static DataCard createFromCurrentCursorRow(Cursor cursor, String authority) {
        DataCard card = createFromCurrentCursorRow(cursor);
        card.setAuthority(authority);
        return card;
    }

    public static DataCard createFromCurrentCursorRow(Cursor cursor) {
        DataCard dataCard = new DataCard();

        dataCard.setId(cursor.getInt(cursor.getColumnIndex(CmHomeContract.DataCard._ID)));
        dataCard.setInternalId(cursor.getString(cursor.getColumnIndex(CmHomeContract.DataCard
                                                                      .INTERNAL_ID_COL)));
        long createdTime = cursor.getLong(cursor.getColumnIndex(CmHomeContract.DataCard
                                                                .DATE_CREATED_COL));
        dataCard.setCreatedDate(new Date(createdTime));
        long modifiedTime = cursor.getLong(cursor.getColumnIndex(CmHomeContract.DataCard
                                                                 .LAST_MODIFIED_COL));
        dataCard.setLastModifiedDate(new Date(modifiedTime));
        long contentCreatedTime = cursor.getLong(
                cursor.getColumnIndex(CmHomeContract.DataCard.DATE_CONTENT_CREATED_COL));
        dataCard.setContentCreatedDate(new Date(contentCreatedTime));
        dataCard.setReasonText(cursor.getString(cursor.getColumnIndex(CmHomeContract.DataCard
                                                                           .REASON_COL)));
        String contentSourceUriString =
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.DataCard.CONTENT_SOURCE_IMAGE_URI_COL));

        if (!TextUtils.isEmpty(contentSourceUriString)) {
            dataCard.setContentSourceImage(Uri.parse(contentSourceUriString));
        }

        String avatarImageUriString =
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.DataCard.AVATAR_IMAGE_URI_COL));
        if (!TextUtils.isEmpty(avatarImageUriString)) {
            dataCard.setAvatarImage(Uri.parse(avatarImageUriString));
        }

        dataCard.setTitle(cursor.getString(
                cursor.getColumnIndex(CmHomeContract.DataCard.TITLE_TEXT_COL)));
        dataCard.setSmallText(
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.DataCard.SMALL_TEXT_COL)));
        dataCard.setBodyText(cursor.getString(
                cursor.getColumnIndex(CmHomeContract.DataCard.BODY_TEXT_COL)));
        dataCard.setAction1Text(
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.DataCard.ACTION_1_TEXT_COL)));

        String clickActionUriString = cursor.getString(
                cursor.getColumnIndex(CmHomeContract.DataCard.CARD_CLICK_URI_COL));
        if (!TextUtils.isEmpty(clickActionUriString)) {
            try {
                Intent cardClickIntent = Intent.parseUri(clickActionUriString,
                                                         Intent.URI_INTENT_SCHEME);
                dataCard.setCardClickIntent(cardClickIntent, isIntentBroadcast(cardClickIntent));
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to parse uri to Intent: " + clickActionUriString);
            }
        }

        String action1UriString = cursor.getString(
                cursor.getColumnIndex(CmHomeContract.DataCard.ACTION_1_URI_COL));
        if (!TextUtils.isEmpty(action1UriString)) {
            try {
                Intent action1Intent = Intent.parseUri(action1UriString,
                                                          Intent.URI_INTENT_SCHEME);
                dataCard.setAction1Intent(action1Intent,
                                          isIntentBroadcast(action1Intent));
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to parse uri to Intent: " + action1UriString);
            }
        }

        dataCard.setAction2Text(cursor.getString(
                cursor.getColumnIndex(CmHomeContract.DataCard.ACTION_2_TEXT_COL)));

        String action2UriString = cursor.getString(
                cursor.getColumnIndex(CmHomeContract.DataCard.ACTION_2_URI_COL));
        if (!TextUtils.isEmpty(action2UriString)) {
            try {
                Intent action2Intent = Intent.parseUri(action2UriString,
                                                          Intent.URI_INTENT_SCHEME);
                dataCard.setAction2Intent(action2Intent,
                                          isIntentBroadcast(action2Intent));
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to parse uri to Intent: " + action2UriString);
            }
        }

        int priority = cursor.getInt(cursor.getColumnIndex(CmHomeContract.DataCard
                                                                        .PRIORITY_COL));
        dataCard.setPriority(priority);

        return dataCard;
    }

    private static boolean isIntentBroadcast(Intent intent) {
        boolean isBroadcast = false;
        if (intent != null) {
            isBroadcast = intent.getBooleanExtra(CmHomeContract.DataCard.IS_BROADCAST_INTENT_EXTRA,
                                                 false);
        }
        return isBroadcast;
    }

    public static List<DataCard> getAllPublishedDataCards(Context context,
                                                          Uri dataCardContentUri,
                                                          Uri dataCardImageContentUri) {
        ContentResolver contentResolver = context.getContentResolver();
        List<DataCard> allCards = new ArrayList<DataCard>();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(dataCardContentUri,
                                           CmHomeContract.DataCard.PROJECTION_ALL,
                                           null,
                                           null,
                                           CmHomeContract.DataCard.DATE_CREATED_COL);
        // Catching all Exceptions, since we can't be sure what the extension will do.
        } catch (Exception e) {
            Log.e(TAG, "Error querying for DataCards, ContentProvider threw an exception for uri:" +
                       " " + dataCardContentUri, e);
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                DataCard dataCard = createFromCurrentCursorRow(cursor,
                                                               dataCardContentUri.getAuthority());
                allCards.add(dataCard);
            }

            cursor.close();
        }


        // Retrieve all DataCardImages for each DataCard.
        // Doing this in a separate loop since each iteration
        // will also be querying the ContentProvider.
        for (DataCard card : allCards) {
            List<DataCardImage> images = DataCardImage
                    .getPublishedDataCardImagesForDataCardId(context,
                                                             dataCardImageContentUri,
                                                             card.getId());
            for (DataCardImage image : images) {
                card.addDataCardImage(image);
            }
        }

        return allCards;
    }

    public void setOnDeleteListener(Context context,
                                    final OnCardDeleteListener onCardDeleteListener) {
        // Don't supply a handler to run the observer on, just pass null
        ContentObserver observer = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);

                CardDeletedInfo deleteInfo = new CardDeletedInfo();

                String authority = uri.getQueryParameter(CardDeletedInfo.AUTHORITY_QUERY_PARAM);
                if (authority != null) {
                    deleteInfo.setAuthority(authority);
                }

                String id = uri.getQueryParameter(CardDeletedInfo.ID_QUERY_PARAM);
                if (id != null) {
                    deleteInfo.setId(Long.parseLong(id));
                }

                String internalId = uri.getQueryParameter(CardDeletedInfo.INTERNAL_ID_QUERY_PARAM);
                if (internalId != null) {
                    deleteInfo.setInternalId(internalId);
                }

                String globalId = uri.getQueryParameter(CardDeletedInfo.GLOBAL_ID_QUERY_PARAM);
                if (globalId != null) {
                    deleteInfo.setGlobalId(globalId);
                }

                // Notify the listener
                onCardDeleteListener.onCardDelete(deleteInfo);
            }

        };

        Uri deleteCardUri = Uri.withAppendedPath(CmHomeContract.CONTENT_URI,
                                        CmHomeContract.DataCard.SINGLE_ROW_DELETE_URI_PATH);
        context.getContentResolver().registerContentObserver(deleteCardUri, true, observer);
    }

    /**
     * A wrapper class that contains information about a DataCard related intent,
     * as well as the Intent itself.
     */
    public class DataCardIntentInfo {
        private boolean mIsBroadcast;
        private Intent mIntent;

        public DataCardIntentInfo(boolean isBroadcast, Intent theIntent) {
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

    public interface OnCardDeleteListener {
        public void onCardDelete(CardDeletedInfo cardDeletedInfo);
    }

    public static class CardDeletedInfo {
        protected final static String ID_QUERY_PARAM = "id";
        protected final static String INTERNAL_ID_QUERY_PARAM = "internalId";
        protected final static String GLOBAL_ID_QUERY_PARAM = "globalId";
        protected final static String AUTHORITY_QUERY_PARAM = "authority";

        private long mId;
        private String mInternalId;
        private String mGlobalId;
        private String mAuthority;

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

        protected void setAuthority(String authority) {
            mAuthority = authority;
        }

        public String getAuthority() {
            return mAuthority;
        }
    }
}
