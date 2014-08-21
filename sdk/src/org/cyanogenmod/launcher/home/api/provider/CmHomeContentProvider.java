package org.cyanogenmod.launcher.home.api.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import org.cyanogenmod.launcher.home.api.db.CmHomeDatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import static org.cyanogenmod.launcher.home.api.db.CmHomeDatabaseHelper.CARD_DATA_IMAGE_TABLE_NAME;
import static org.cyanogenmod.launcher.home.api.db.CmHomeDatabaseHelper.CARD_DATA_TABLE_NAME;

/**
 * <p>The ContentProvider that shares data for all types of Cards to CM Home.</p>
 *
 * <p><b>This ContentProvider is intended to be internal. When importing the CM Home SDK,
 * you do not need to subclass or otherwise reference this ContentProvider,
 * besides declaring it in your manifest.</b></p>
 *
 * <p>Be sure to declare this content provider as such:</p>
 * <pre>
 * {@code
 *    <provider android:name="org.cyanogenmod.launcher.home.api.provider.CmHomeContentProvider"
 *              android:label="@string/provider_name"
 *              android:authorities="org.cyanogenmod.launcher.home.api.sdkexample.cmhomeapi"
 *              android:enabled="true"
 *              android:exported="true"
 *              android:readPermission="org.cyanogenmod.launcher.home.api.FEED_READ"
 *              android:writePermission="org.cyanogenmod.launcher.home.api.FEED_WRITE" />
 * }
 * </pre>
 */
public class CmHomeContentProvider extends ContentProvider {
    CmHomeDatabaseHelper mCmHomeDatabaseHelper;
    /**
     * The internal storage directory where cached images will be stored.
     */
    public final static String IMAGE_FILE_CACHE_DIR = "CardDataImageCache";

    private static final String TAG                  = "CmHomeContentProvider";
    private static final int    CARD_DATA_LIST       = 1;
    private static final int    CARD_DATA_ITEM       = 2;
    private static final int    CARD_DATA_IMAGE_LIST = 3;
    private static final int    CARD_DATA_IMAGE_ITEM = 4;
    private static final int    IMAGE_FILE           = 5;
    private static UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        setupUriMatcher(CmHomeContract.AUTHORITY);
    }

    private static void setupUriMatcher(String authority) {
        URI_MATCHER.addURI(CmHomeContract.AUTHORITY,
                           CmHomeContract.CardDataContract.LIST_INSERT_UPDATE_URI_PATH,
                           CARD_DATA_LIST);
        URI_MATCHER.addURI(CmHomeContract.AUTHORITY,
                           CmHomeContract.CardDataContract.SINGLE_ROW_INSERT_UPDATE_URI_PATH,
                           CARD_DATA_ITEM);
        URI_MATCHER.addURI(CmHomeContract.AUTHORITY,
                           CmHomeContract.CardDataImageContract.LIST_INSERT_UPDATE_URI_PATH,
                           CARD_DATA_IMAGE_LIST);
        URI_MATCHER.addURI(CmHomeContract.AUTHORITY,
                           CmHomeContract.CardDataImageContract.SINGLE_ROW_INSERT_UPDATE_URI_PATH,
                           CARD_DATA_IMAGE_ITEM);
        URI_MATCHER.addURI(CmHomeContract.AUTHORITY,
                           CmHomeContract.ImageFile.PATH + "/*",
                           IMAGE_FILE);
    }

    @Override
    public boolean onCreate() {
        setAuthority();
        mCmHomeDatabaseHelper = new CmHomeDatabaseHelper(getContext());
        return true;
    }

    /**
     * The authority of the ContentProvider must be unique across all apps that implement this
     * API protocol. To resolve this, dynamically set the authority to be unique for this package
     * name.
     */
    private void setAuthority() {
        String providerAuthority = getContext().getPackageName() + ".cmhomeapi";
        CmHomeContract.setAuthority(providerAuthority);
        setupUriMatcher(providerAuthority);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mCmHomeDatabaseHelper.getWritableDatabase();

        int uriMatch = URI_MATCHER.match(uri);
        switch (uriMatch) {
            case CARD_DATA_LIST:
                queryBuilder.setTables(CARD_DATA_TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = CmHomeContract.CardDataContract.SORT_ORDER_DEFAULT;
                }
                break;
            case CARD_DATA_ITEM:
                queryBuilder.setTables(CARD_DATA_TABLE_NAME);
                queryBuilder.appendWhere(CmHomeContract.CardDataContract._ID + " = " + uri
                        .getLastPathSegment());
                break;
            case CARD_DATA_IMAGE_LIST:
                queryBuilder.setTables(CARD_DATA_IMAGE_TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = CmHomeContract.CardDataImageContract.SORT_ORDER_DEFAULT;
                }
                break;
            case CARD_DATA_IMAGE_ITEM:
                queryBuilder.setTables(CARD_DATA_IMAGE_TABLE_NAME);
                queryBuilder.appendWhere(CmHomeContract.CardDataImageContract._ID + " = " + uri
                        .getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }

        Cursor cursor =
                queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public AssetFileDescriptor openTypedAssetFile(Uri uri,
            String mimeTypeFilter, Bundle opts) throws FileNotFoundException {
        int uriMatch = URI_MATCHER.match(uri);
        if (uriMatch == IMAGE_FILE) {
            String filename = uri.getLastPathSegment();
            File dir = new File(getContext().getFilesDir(), IMAGE_FILE_CACHE_DIR);
            ParcelFileDescriptor pfd =
                    ParcelFileDescriptor.open(new File(dir, filename),
                                              ParcelFileDescriptor.MODE_READ_ONLY);
            return new AssetFileDescriptor(pfd, 0, AssetFileDescriptor.UNKNOWN_LENGTH);
        }
        throw new FileNotFoundException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mCmHomeDatabaseHelper.getWritableDatabase();
        int updateCount = 0;
        int uriMatch = URI_MATCHER.match(uri);

        switch (uriMatch) {
            case CARD_DATA_LIST:
                updateCount = db.update(CARD_DATA_TABLE_NAME,
                                    values,
                                    selection,
                                    selectionArgs);
                break;
            case CARD_DATA_ITEM:
                String idStr = uri.getLastPathSegment();
                String where = CmHomeContract.CardDataContract._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(CARD_DATA_TABLE_NAME,
                                            values,
                                            where,
                                            selectionArgs);
                break;
            case CARD_DATA_IMAGE_LIST:
                updateCount = db.update(CARD_DATA_IMAGE_TABLE_NAME,
                                    values,
                                    selection,
                                    selectionArgs);
                break;
            case CARD_DATA_IMAGE_ITEM:
                idStr = uri.getLastPathSegment();
                where = CmHomeContract.CardDataImageContract._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(CARD_DATA_IMAGE_TABLE_NAME,
                                        values,
                                        where,
                                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for update: " + uri);
        }

        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        cleanupCardDataImageCache();
        return updateCount;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriMatch = URI_MATCHER.match(uri);

        SQLiteDatabase db = mCmHomeDatabaseHelper.getWritableDatabase();
        switch (uriMatch) {
            case CARD_DATA_LIST:
                long id = db.insert(CARD_DATA_TABLE_NAME,
                                    null,
                                    values);
                return getUriForId(id, uri);
            case CARD_DATA_ITEM:
                id = db.insertWithOnConflict(CARD_DATA_TABLE_NAME,
                                                  null,
                                                  values,
                                                  SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            case CARD_DATA_IMAGE_LIST:
                id = db.insert(CARD_DATA_IMAGE_TABLE_NAME,
                                    null,
                                    values);
                return getUriForId(id, uri);
            case CARD_DATA_IMAGE_ITEM:
                id = db.insertWithOnConflict(CARD_DATA_IMAGE_TABLE_NAME,
                                                  null,
                                                  values,
                                                  SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            default:
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mCmHomeDatabaseHelper.getWritableDatabase();
        int deleteCount = 0;
        int uriMatch = URI_MATCHER.match(uri);
        String idStr = uri.getLastPathSegment();

        switch (uriMatch) {
            case CARD_DATA_LIST:
                deleteCount = db.delete(CARD_DATA_TABLE_NAME,
                                            selection,
                                            selectionArgs);
                break;
            case CARD_DATA_ITEM:
                String where = CmHomeContract.CardDataContract._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                deleteCount = db.delete(CARD_DATA_TABLE_NAME,
                                            where,
                                            selectionArgs);
                break;
            case CARD_DATA_IMAGE_LIST:
                deleteCount = db.delete(CARD_DATA_IMAGE_TABLE_NAME,
                                        selection,
                                        selectionArgs);
                break;
            case CARD_DATA_IMAGE_ITEM:
                where = CmHomeContract.CardDataImageContract._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                deleteCount = db.delete(CARD_DATA_IMAGE_TABLE_NAME,
                                        where,
                                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for update: " + uri);
        }

        if (deleteCount == 1) {
            if(uriMatch == CARD_DATA_ITEM) {
                // Notifies for a delete
                getUriForId(Long.parseLong(idStr),
                            Uri.withAppendedPath(CmHomeContract.CONTENT_URI,
                                    CmHomeContract.CardDataContract.SINGLE_ROW_DELETE_URI_PATH));
            }
            if(uriMatch == CARD_DATA_IMAGE_ITEM) {
                // Notifies for a delete
                getUriForId(Long.parseLong(idStr),
                            Uri.withAppendedPath(CmHomeContract.CONTENT_URI,
                                    CmHomeContract.CardDataImageContract.SINGLE_ROW_DELETE_URI_PATH));
            }
        } else if (deleteCount > 1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        cleanupCardDataImageCache();
        return deleteCount;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            // notify all listeners of changes:
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        throw new IllegalArgumentException("Problem while inserting into uri: " + uri);
    }

    @Override
    public String getType(Uri uri) {
        int uriMatch = URI_MATCHER.match(uri);
        switch (uriMatch) {
            case CARD_DATA_LIST:
                return CmHomeContract.CardDataContract.CONTENT_TYPE;
            case CARD_DATA_ITEM:
                return CmHomeContract.CardDataContract.CONTENT_ITEM_TYPE;
            case CARD_DATA_IMAGE_LIST:
                return CmHomeContract.CardDataImageContract.CONTENT_TYPE;
            case CARD_DATA_IMAGE_ITEM:
                return CmHomeContract.CardDataImageContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    /**
     * For all files in the CardDataImage cache directory, if
     * they are not represented in the database, delete them.
     */
    private void cleanupCardDataImageCache() {
        Set<String> filenames = new HashSet<String>();

        // Handle CardDataImage rows
        Cursor cursor = query(CmHomeContract.CardDataImageContract.CONTENT_URI,
                                              CmHomeContract.CardDataImageContract.PROJECTION_ALL,
                                              null,
                                              null,
                                              null);
        while(cursor.moveToNext()) {
            String uriString =
                    cursor.getString(cursor.getColumnIndex(CmHomeContract.CardDataImageContract.IMAGE_URI_COL));
            if (uriString != null) {
                String filename = Uri.parse(uriString).getLastPathSegment();
                filenames.add(filename);
            }
        }

        // Handle CardData image fields
        String[] cardDataProjection = {CmHomeContract.CardDataContract.AVATAR_IMAGE_URI_COL,
                                     CmHomeContract.CardDataContract.CONTENT_SOURCE_IMAGE_URI_COL};

        cursor = query(CmHomeContract.CardDataContract.CONTENT_URI,
                                              cardDataProjection,
                                              null,
                                              null,
                                              null);

        while(cursor.moveToNext()) {
            String contentSourceUri =
                    cursor.getString(cursor.getColumnIndex(CmHomeContract.CardDataContract.CONTENT_SOURCE_IMAGE_URI_COL));
            String avatarUri =
                    cursor.getString(cursor.getColumnIndex(CmHomeContract.CardDataContract.AVATAR_IMAGE_URI_COL));
            if (contentSourceUri != null) {
                String filename = Uri.parse(contentSourceUri).getLastPathSegment();
                filenames.add(filename);
            }

            if (avatarUri != null) {
                String filename = Uri.parse(avatarUri).getLastPathSegment();
                filenames.add(filename);
            }
        }

        // Delete all files that do not exist in the database
        File internalStorageDir = getContext().getFilesDir();
        File imageCacheDir = new File(internalStorageDir, IMAGE_FILE_CACHE_DIR);
        for (File file : imageCacheDir.listFiles()) {
            if (!filenames.contains(file.getName())) {
                file.delete();
            }
        }
    }

    /**
     * Generates a String representing the MD5 hash of the input byte array.
     * @param bytes A byte array
     * @return A String representation of the MD5 hash of the input byte array
     */
    private static String hashBytesMD5(byte[] bytes) {
        // Compute the hash
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes, 0, bytes.length);
            String hash = new BigInteger(1, md.digest()).toString(16);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            Log.w(TAG, "Unable to compute MD5 hash of byte array.");
        }

        // No hash computed
        return null;
    }

    /**
     * Check if a cached Bitmap file exists in {@link org.cyanogenmod.launcher.home.api.provider
     * .CmHomeContentProvider.IMAGE_FILE_CACHE_DIR} with the given filename.
     * @param filename The filename to check for
     * @param context The context with access to the directory that would contain this file.
     * @return True if the file exists.
     */
    private static boolean bitmapCacheFileExists(String filename, Context context) {
        // Create a file in the cache subdirectory
        File imageDir = new File(context.getFilesDir(), IMAGE_FILE_CACHE_DIR);
        File imageFile = new File(imageDir, filename);

        return imageFile.exists();
    }

    /**
     * Stores the given bitmap in internal storage in {@link #IMAGE_FILE_CACHE_DIR} using an MD5
     * sum of the bitmap content as the filename, if the cache does not exist already.
     * @param bitmap The <a href="http://developer.android.com/reference/android/graphics/Bitmap.html">Bitmap</a>
     *               to store in the cache.
     * @param context A <a href="http://developer.android.com/reference/android/content/Context.html">Context</a>
     *                of the application that will share this image in this ContentProvider.
     * @return A Uri pointing to the newly stored image file in the cache, or the existing image,
     *         if one is found.
     */
    public static Uri storeBitmapInCache(Bitmap bitmap, Context context) {
        FileOutputStream outputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            // Get the bytes containing the image data
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] bitmapBytes = byteArrayOutputStream.toByteArray();

            String hash = hashBytesMD5(bitmapBytes);
            // Can't continue without a hash
            if (hash == null) return null;

            String filename = hash + ".png";

            // If the cache already exists, just return the URI to the cache file
            if (bitmapCacheFileExists(filename, context)) {
                return Uri.withAppendedPath(CmHomeContract.ImageFile.CONTENT_URI,
                                            filename);
            }

            // Write the bytes to a file using the hash as the filename
            // Create a file in the cache subdirectory
            File imageDir = new File(context.getFilesDir(), IMAGE_FILE_CACHE_DIR);
            imageDir.mkdirs();
            File imageFile = new File(imageDir, filename);
            outputStream = new FileOutputStream(imageFile);
            outputStream.write(bitmapBytes);

            Uri imageUri = Uri.withAppendedPath(CmHomeContract.ImageFile.CONTENT_URI,
                                                filename);

            // Set the image URI, which will actually be stored in the database.
            return imageUri;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Unable to save bitmap to temporary file. Could not open file.");
        } catch (IOException e) {
            Log.e(TAG, "Unable to save bitmap to temporary file, IOException occurred.");
        } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to save bitmap to temporary file, IOException occurred.");
                }
                try {
                    if (byteArrayOutputStream != null) {
                        byteArrayOutputStream.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to save bitmap to temporary file, IOException occurred.");
                }
        }
        // Failure, no URI available
        return null;
    }

}
