package org.cyanogenmod.launcher.home.api.db;

import android.database.sqlite.SQLiteDatabase;

public class CmHomeDatabase {
    private CmHomeDatabaseHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    public static final String DATA_CARD_TABLE_NAME = DataCard;
    public static final String DATA_CARD_ID_COL = "_id";
    public static final String DATA_CARD_SUBJECT_COL = "subject";
    public static final String DATA_CARD_DATE_CONTENT_CREATED_COL = "date_content_created";
    public static final String DATA_CARD_DATE_CREATED_COL = "date_created";
    public static final String DATA_CARD_LAST_MODIFIED_COL = "last_modified";
    public static final String DATA_CARD_CONTENT_SOURCE_IMAGE_URI_COL = "content_source_image_uri";
    public static final String DATA_CARD_AVATAR_IMAGE_URI_COL = "avatar_image_uri";
    public static final String DATA_CARD_ID_TITLE_TEXT_COL = "title_text";
    public static final String DATA_CARD_ID_SMALL_TEXT_COL = "small_text";
    public static final String DATA_CARD_BODY_TEXT_COL = "body_text";
    public static final String DATA_CARD_ACTION_1_TEXT_COL = "action_1_text";
    public static final String DATA_CARD_ACTION_1_URI = "action_1_uri";
    public static final String DATA_CARD_ACTION_2_TEXT_COL = "action_2_text";
    public static final String DATA_CARD_ACTION_2_URI = "action_2_uri";

    public static final String DATA_CARD_IMAGE_TABLE_NAME = DataCard;
    public static final String DATA_CARD_ID_COL = "_id";
    public static final String DATA_CARD_ID_COL = "data_card_id";
    public static final String DATA_CARD_IMAGE_URI = "image_uri";

    public CmHomeDatabase(Context context) {
        mDbHelper = new CmHDatabaseHelper(context);
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }
}