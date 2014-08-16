package org.cyanogenmod.launcher.home.api.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContract;

/**
 * <p>Creates the SQLite database that backs CMHomeContentProvider.</p>
 * <p><b>This class is intended to be internal, and does not need to be referenced by
 * applications using the SDK.</b></p>
 */
public class CmHomeDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME          = "CmHomeAPI.db";
    private static final String TAG                    = "CmHomeDatabaseHelper";
    private static final int    DATABASE_VERSION       = 1;
    public static final  String CARD_DATA_TABLE_NAME   = "CardData";
    private static final String CARD_DATA_TABLE_CREATE =
            "CREATE TABLE " + CARD_DATA_TABLE_NAME +
            "(" + CmHomeContract.CardDataContract._ID + " INTEGER PRIMARY KEY NOT NULL," +
            CmHomeContract.CardDataContract.INTERNAL_ID_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataContract.REASON_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataContract.DATE_CONTENT_CREATED_COL + " TEXT NOT NULL," +
            CmHomeContract.CardDataContract.DATE_CREATED_COL + " TEXT DEFAULT CURRENT_TIMESTAMP " +
            "NOT NULL," +
            CmHomeContract.CardDataContract.LAST_MODIFIED_COL + " TEXT DEFAULT CURRENT_TIMESTAMP " +
            "NOT NULL," +
            CmHomeContract.CardDataContract.CONTENT_SOURCE_IMAGE_URI_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataContract.AVATAR_IMAGE_URI_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataContract.TITLE_TEXT_COL + " TEXT NOT NULL," +
            CmHomeContract.CardDataContract.SMALL_TEXT_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataContract.BODY_TEXT_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataContract.CARD_CLICK_URI_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataContract.ACTION_1_TEXT_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataContract.ACTION_1_URI_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataContract.ACTION_2_TEXT_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataContract.ACTION_2_URI_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataContract.PRIORITY_COL + " INTEGER DEFAULT NULL);";

    private static final String CARD_DATA_UPDATE_TIME_TRIGGER =
            "CREATE TRIGGER card_data_update_time_trigger " +
            "AFTER UPDATE ON " + CARD_DATA_TABLE_NAME + " FOR EACH ROW" +
            " BEGIN " +
            "UPDATE " + CARD_DATA_TABLE_NAME +
            " SET " + "last_modified" + " = CURRENT_TIMESTAMP" +
            " WHERE " + "_id" + " = old._id;" +
            " END";

    public static final  String CARD_DATA_IMAGE_TABLE_NAME   = "CardDataImage";
    private static final String CARD_DATA_IMAGE_TABLE_CREATE =
            "CREATE TABLE " + CARD_DATA_IMAGE_TABLE_NAME +
            "(" + CmHomeContract.CardDataImageContract._ID + " INTEGER PRIMARY KEY," +
            CmHomeContract.CardDataImageContract.CARD_DATA_ID_COL + " INTEGER NOT NULL," +
            CmHomeContract.CardDataImageContract.INTERNAL_ID_COL + " TEXT DEFAULT NULL," +
            CmHomeContract.CardDataImageContract.IMAGE_URI_COL + " TEXT NOT NULL," +
            CmHomeContract.CardDataImageContract.IMAGE_LABEL_COL + " TEXT DEFAULT NULL," +
            "FOREIGN KEY(" + CmHomeContract.CardDataImageContract.CARD_DATA_ID_COL +
            ") REFERENCES " +
            CARD_DATA_TABLE_NAME + "(" + CmHomeContract.CardDataContract._ID + "));";

    private static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS";

    public CmHomeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CARD_DATA_TABLE_CREATE);
        database.execSQL(CARD_DATA_UPDATE_TIME_TRIGGER);
        database.execSQL(CARD_DATA_IMAGE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion +
                   ". All existing data will be destroyed.");
        database.execSQL(DROP_TABLE_STATEMENT + " " + CARD_DATA_TABLE_NAME);
        database.execSQL(DROP_TABLE_STATEMENT + " " + CARD_DATA_IMAGE_TABLE_NAME);
        onCreate(database);
    }
}
