package org.cyanogenmod.launcher.home.api.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import org.cyanogenmod.launcher.home.api.db.CmHomeDatabaseHelper;

import static org.cyanogenmod.launcher.home.api.db.CmHomeDatabaseHelper.DATA_CARD_IMAGE_TABLE_NAME;
import static org.cyanogenmod.launcher.home.api.db.CmHomeDatabaseHelper.DATA_CARD_TABLE_NAME;

public class CmHomeContentProvider extends ContentProvider {
    CmHomeDatabaseHelper mCmHomeDatabaseHelper;

    private static final int DATA_CARD_LIST = 1;
    private static final int DATA_CARD_ITEM = 2;
    private static final int DATA_CARD_IMAGE_LIST = 3;
    private static final int DATA_CARD_IMAGE_ITEM = 4;
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(CmHomeContract.AUTHORITY,
                           "datacard",
                           DATA_CARD_LIST);
        URI_MATCHER.addURI(CmHomeContract.AUTHORITY,
                           "datacard/#",
                           DATA_CARD_ITEM);
        URI_MATCHER.addURI(CmHomeContract.AUTHORITY,
                           "datacardimage",
                           DATA_CARD_IMAGE_LIST);
        URI_MATCHER.addURI(CmHomeContract.AUTHORITY,
                           "datacardimage/#",
                           DATA_CARD_IMAGE_ITEM);
    }

    @Override
    public boolean onCreate() {
        mCmHomeDatabaseHelper = new CmHomeDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mCmHomeDatabaseHelper.getWritableDatabase();

        int uriMatch = URI_MATCHER.match(uri);
        switch (uriMatch) {
            case DATA_CARD_LIST:
                queryBuilder.setTables(DATA_CARD_TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = CmHomeContract.DataCard.SORT_ORDER_DEFAULT;
                }
                break;
            case DATA_CARD_ITEM:
                queryBuilder.setTables(DATA_CARD_TABLE_NAME);
                queryBuilder.appendWhere(CmHomeContract.DataCard._ID + " = " + uri
                        .getLastPathSegment());
                break;
            case DATA_CARD_IMAGE_LIST:
                queryBuilder.setTables(DATA_CARD_IMAGE_TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = CmHomeContract.DataCard.SORT_ORDER_DEFAULT;
                }
                break;
            case DATA_CARD_IMAGE_ITEM:
                queryBuilder.setTables(DATA_CARD_IMAGE_TABLE_NAME);
                queryBuilder.appendWhere(CmHomeContract.DataCardImage._ID + " = " + uri
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
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mCmHomeDatabaseHelper.getWritableDatabase();
        int updateCount = 0;
        int uriMatch = URI_MATCHER.match(uri);

        switch (uriMatch) {
            case DATA_CARD_LIST:
                updateCount = db.update(DATA_CARD_TABLE_NAME,
                                    values,
                                    selection,
                                    selectionArgs);
                break;
            case DATA_CARD_ITEM:
                String idStr = uri.getLastPathSegment();
                String where = CmHomeContract.DataCard._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(DATA_CARD_TABLE_NAME,
                                            values,
                                            where,
                                            selectionArgs);
                break;
            case DATA_CARD_IMAGE_LIST:
                updateCount = db.update(DATA_CARD_IMAGE_TABLE_NAME,
                                    values,
                                    selection,
                                    selectionArgs);
                break;
            case DATA_CARD_IMAGE_ITEM:
                idStr = uri.getLastPathSegment();
                where = CmHomeContract.DataCardImage._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(DATA_CARD_IMAGE_TABLE_NAME,
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
        return updateCount;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriMatch = URI_MATCHER.match(uri);

        SQLiteDatabase db = mCmHomeDatabaseHelper.getWritableDatabase();
        switch (uriMatch) {
            case DATA_CARD_LIST:
                long id = db.insert(DATA_CARD_TABLE_NAME,
                                    null,
                                    values);
                return getUriForId(id, uri);
            case DATA_CARD_ITEM:
                id = db.insertWithOnConflict(DATA_CARD_TABLE_NAME,
                                                  null,
                                                  values,
                                                  SQLiteDatabase.CONFLICT_REPLACE);
                return getUriForId(id, uri);
            case DATA_CARD_IMAGE_LIST:
                id = db.insert(DATA_CARD_IMAGE_TABLE_NAME,
                                    null,
                                    values);
                return getUriForId(id, uri);
            case DATA_CARD_IMAGE_ITEM:
                id = db.insertWithOnConflict(DATA_CARD_IMAGE_TABLE_NAME,
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

        switch (uriMatch) {
            case DATA_CARD_LIST:
                deleteCount = db.delete(DATA_CARD_TABLE_NAME,
                                            selection,
                                            selectionArgs);
                break;
            case DATA_CARD_ITEM:
                String idStr = uri.getLastPathSegment();
                String where = CmHomeContract.DataCard._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                deleteCount = db.delete(DATA_CARD_TABLE_NAME,
                                            where,
                                            selectionArgs);
                break;
            case DATA_CARD_IMAGE_LIST:
                deleteCount = db.delete(DATA_CARD_IMAGE_TABLE_NAME,
                                        selection,
                                        selectionArgs);
                break;
            case DATA_CARD_IMAGE_ITEM:
                idStr = uri.getLastPathSegment();
                where = CmHomeContract.DataCardImage._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                deleteCount = db.delete(DATA_CARD_IMAGE_TABLE_NAME,
                                        where,
                                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for update: " + uri);
        }

        if (deleteCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
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
            case DATA_CARD_LIST:
                return CmHomeContract.DataCard.CONTENT_TYPE;
            case DATA_CARD_ITEM:
                return CmHomeContract.DataCard.CONTENT_ITEM_TYPE;
            case DATA_CARD_IMAGE_LIST:
                return CmHomeContract.DataCardImage.CONTENT_TYPE;
            case DATA_CARD_IMAGE_ITEM:
                return CmHomeContract.DataCardImage.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
