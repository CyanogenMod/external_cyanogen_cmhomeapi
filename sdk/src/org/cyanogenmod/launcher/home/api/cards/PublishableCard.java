package org.cyanogenmod.launcher.home.api.cards;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContract;

/**
 * Represents any card that can be published to the host application,
 * to be displayed to the user.
 */
public abstract class PublishableCard {
    private String TAG = "PublishableCard";
    private long   mId = -1;
    /**
     * An instance of the database contract class to be used when publishing. To be set by
     * subclasses.
     */
    protected CmHomeContract.ICmHomeContract mICmHomeContract;
    private String mAuthority;

    public PublishableCard(CmHomeContract.ICmHomeContract contract) {
        mICmHomeContract = contract;
    }

    public long getId() {
        return mId;
    }

    protected void setId(long id) {
        mId = id;
    }

    public void setAuthority(String authority) {
        mAuthority = authority;
    }

    public String getAuthority() {
        return mAuthority;
    }

    /**
     * Retrieve a global identifier that is unique among all CardData objects on the device.
     * @return A global ID String for this PublishableCard.
     */
    public String getGlobalId() {
        return mAuthority + "/" + mId;
    }

    /**
     * Publish this PublishableCard to CM Home, to be immediately be displayed to the user.
     * This operation is completed asynchronously. If this card has been previously published,
     * it will be updated in place.
     * @param context The context of the publishing application.
     */
    public void publish(Context context) {
        new PublishCardTask(this, context).execute();
    }

    public void publishAndWait(Context context) {
        boolean updated = false;
        // If we have an ID, try to update that row first.
        if (getId() != -1) {
            updated = update(context);
        }

        // If the update could not succeed, either this card never existed,
        // or was deleted. Either way, create a new row for this card.
        if (!updated) {
            ContentResolver contentResolver = context.getContentResolver();

            ContentValues values = getContentValues();

            Uri result = null;
            try {
                result = contentResolver.insert(getBaseUri(), values);
            // Catching all Exceptions, since we can't be sure what the extension will do.
            } catch (Exception e) {
                Log.e(TAG,
                      "Error publishing PublishableCard, ContentProvider threw an exception for " +
                      "uri:" +
                      " " + getBaseUri(), e);
            }

            if (result != null) {
                // Store the resulting ID
                setId(Long.parseLong(result.getLastPathSegment()));
            }
        }
    }

    protected abstract ContentValues getContentValues();

    protected boolean update(Context context) {
        if (getId() == -1) {
            return false;
        }

        ContentResolver contentResolver = context.getContentResolver();
        int rows = 0;
        try {
            rows = contentResolver.update(ContentUris.withAppendedId(
                                                      getBaseUri(),
                                                      getId()),
                                              getContentValues(),
                                              null,
                                              null);
        // Catching all Exceptions, since we can't be sure what the extension will do.
        } catch (Exception e) {
            Log.e(TAG,
                  "Error updating PublishableCard, ContentProvider threw an exception for uri:" +
                  " " + getBaseUri(), e);
        }

        // We must have updated at least one row
        return rows > 0;
    }

    /**
     * Unpublishes a PublishableCard, removing it from CM Home immediately. The card can be
     * republished at a later time, but will obtain a new ID and potentially a new position in CM
     * Home.
     * @param context A Context of the publishing application.
     * @return true if the unpublish was successful, false otherwise.
     */
    public boolean unpublish(Context context) {
        if (getId() == -1) {
            return false;
        }

        ContentResolver contentResolver = context.getContentResolver();
        int rows = 0;
        try {
            rows = contentResolver.delete(ContentUris.withAppendedId(
                                                      getBaseUri(),
                                                      getId()),
                                              null,
                                              null);
        // Catching all Exceptions, since we can't be sure what the extension will do.
        } catch (Exception e) {
            Log.e(TAG,
                  "Error unpublishing PublishableCard, ContentProvider threw an exception for " +
                  "uri:" + getBaseUri(), e);
        }

        return rows > 0;
    }

    /**
     * Has this card been published to the host application?
     * @param context The context of the publishing application
     * @return True if this card has been published.
     */
    public boolean isPublished(Context context) {
        if(mId == -1) {
            return false;
        }

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(ContentUris.withAppendedId(
                                                   getBaseUri(),
                                                   getId()),
                                           new String[]{mICmHomeContract.getIdColumnName()},
                                           null,
                                           null,
                                           null);
        // Catching all Exceptions, since we can't be sure what the extension will do.
        } catch (Exception e) {
            Log.e(TAG,
                  "Error querying PublishableCard, ContentProvider threw an exception for uri:" +
                  " " + getBaseUri(), e);
        }

        int cursorCount = 0;
        if (cursor != null) {
            cursorCount = cursor.getCount();
            cursor.close();
        }
        return cursorCount > 0;
    }

    private Uri getBaseUri() {
        Uri theUri = mICmHomeContract.getContentUri();

        // If this PublishableCard has an authority set, use that in the URI.
        if (!TextUtils.isEmpty(getAuthority())) {
            return theUri.buildUpon().authority(getAuthority()).build();
        }
        return theUri;
    }

    private static class PublishCardTask extends AsyncTask<Void, Void, Void> {
        PublishableCard mPublishableCard;
        Context mContext;

        public PublishCardTask(PublishableCard card, Context context) {
            mPublishableCard = card;
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mPublishableCard.publishAndWait(mContext);
            return null;
        }
    }
}
