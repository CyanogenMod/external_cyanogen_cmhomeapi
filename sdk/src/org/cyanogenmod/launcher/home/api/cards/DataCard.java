package org.cyanogenmod.launcher.home.api.cards;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContract;

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
    private Uri    mAvatarImageUri;
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
        DataCardImage image = new DataCardImage(getId(), uri);
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

    public void setContentSourceImageUri(Uri contentSourceImageUri) {
        this.mContentSourceImageUri = contentSourceImageUri;
    }

    public Uri getAvatarImageUri() {
        return mAvatarImageUri;
    }

    public void setAvatarImageUri(Uri avatarImageUri) {
        this.mAvatarImageUri = avatarImageUri;
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

    public Intent getCardClickIntent() {
        return mCardClickIntent;
    }

    public void setCardClickIntent(Intent cardClickIntent) {
        mCardClickIntent = cardClickIntent;
    }

    public String getAction1Text() {
        return mAction1Text;
    }

    public void setAction1Text(String action1Text) {
        this.mAction1Text = action1Text;
    }

    public Intent getAction1Intent() {
        return mAction1Intent;
    }

    public void setAction1Intent(Intent action1Intent) {
        this.mAction1Intent = action1Intent;
    }

    public String getAction2Text() {
        return mAction2Text;
    }

    public void setAction2Text(String action2Text) {
        this.mAction2Text = action2Text;
    }

    public Intent getAction2Intent() {
        return mAction2Intent;
    }

    public void setAction2Intent(Intent action2Intent) {
        this.mAction2Intent = action2Intent;
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

    @Override
    public boolean publish(Context context) {
        boolean updated = super.publish(context);

        if (!updated) {
            // Initialize the created date and modified date to now.
            mCreatedDate = new Date();
            mLastModifiedDate = new Date();
        }

        for (DataCardImage image : mImages) {
            image.publish(context);
        }

        return updated;
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
                image.publish(context);
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
        boolean deleted = super.unpublish(context);
        if (deleted) {
            // Delete all associated images as well
            for (DataCardImage image : mImages) {
                image.unpublish(context);
            }
        }

        return deleted;
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

        if (getAction1Intent() != null) {
            values.put(CmHomeContract.DataCard.ACTION_1_URI_COL,
                       getAction1Intent().toUri(Intent.URI_INTENT_SCHEME).toString());
        }

        values.put(CmHomeContract.DataCard.ACTION_2_TEXT_COL,
                   getAction2Text());

        if (getAction2Intent() != null) {
            values.put(CmHomeContract.DataCard.ACTION_2_URI_COL,
                       getAction2Intent().toUri(Intent.URI_INTENT_SCHEME).toString());
        }

        values.put(CmHomeContract.DataCard.PRIORITY_COL,
                   getPriorityAsInt());

        if (getCardClickIntent() != null) {
            values.put(CmHomeContract.DataCard.CARD_CLICK_URI_COL,
                       getCardClickIntent().toUri(Intent.URI_INTENT_SCHEME).toString());
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
            dataCard.setContentSourceImageUri(Uri.parse(contentSourceUriString));
        }

        String avatarImageUriString =
                cursor.getString(cursor.getColumnIndex(
                        CmHomeContract.DataCard.AVATAR_IMAGE_URI_COL));
        if (!TextUtils.isEmpty(avatarImageUriString)) {
            dataCard.setAvatarImageUri(Uri.parse(avatarImageUriString));
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
                dataCard.setCardClickIntent(cardClickIntent);
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to parse uri to Intent: " + clickActionUriString);
            }
        }

        String action1UriString = cursor.getString(
                cursor.getColumnIndex(CmHomeContract.DataCard.ACTION_1_URI_COL));
        if (!TextUtils.isEmpty(action1UriString)) {
            try {
                dataCard.setAction1Intent(Intent.parseUri(action1UriString,
                                                          Intent.URI_INTENT_SCHEME));
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
                dataCard.setAction2Intent(Intent.parseUri(action2UriString,
                                                          Intent.URI_INTENT_SCHEME));
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to parse uri to Intent: " + action2UriString);
            }
        }

        int priority = cursor.getInt(cursor.getColumnIndex(CmHomeContract.DataCard
                                                                        .PRIORITY_COL));
        dataCard.setPriority(priority);

        return dataCard;
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
}
