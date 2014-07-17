import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.lang.Override;

/**
 * Creates the SQLite database that backs CMHomeContentProvider.
 */
public class CmHomeDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CmHomeAPI";
    private static final String TAG = "CmHomeDatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATA_CARD_TABLE_NAME = DataCard;
    private static final String DATA_CARD_TABLE_CREATE =
                                  "CREATE TABLE " + DATA_CARD_TABLE_NAME +
                                  "(_id INTEGER PRIMARY KEY," +
                                  "subject TEXT NOT NULL," +
                                  "date_content_created TEXT NOT NULL," +
                                  "date_created TEXT DEFAULT CURRENT_TIMESTAMP " +
                                  "NOT NULL," +
                                  "last_modified TEXT DEFAULT CURRENT_TIMESTAMP " +
                                  "NOT NULL," +
                                  "content_source_image_uri TEXT DEFAULT NULL," +
                                  "avatar_image_uri TEXT DEFAULT NULL," +
                                  "title_text TEXT DEFAULT NULL," +
                                  "caption_text TEXT DEFAULT NULL" +
                                  "body_text TEXT DEFAULT NULL" +
                                  "action_1_text TEXT DEFAULT NULL" +
                                  "action_1_uri TEXT DEFAULT NULL," +
                                  "action_2_text TEXT DEFAULT NULL" +
                                  "action_2_uri TEXT DEFAULT NULL);";

    private static final String DATA_CARD_IMAGE_TABLE_NAME = DataCard;
    private static final String DATA_CARD_IMAGE_TABLE_CREATE =
                                  "CREATE TABLE " + DATA_CARD_TABLE_NAME +
                                  "(_id INTEGER PRIMARY KEY," +
                                  "data_card_id INTEGER NOT NULL," +
                                  "image_uri TEXT NOT NULL," +
                                  "FOREIGN KEY(data_card_id) REFERENCES " + DATA_CARD_TABLE_NAME +
                                  "(_id));";

    private static final String DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS";

    public CmHomeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATA_CARD_TABLE_CREATE);
        database.execSQL(DATA_CARD_IMAGE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion +
             ". All existing data will be destroyed.");
        database.execSQL(DROP_TABLE_STATEMENT + " " + DATA_CARD_TABLE_NAME);
        database.execSQL(DROP_TABLE_STATEMENT + " " + DATA_CARD_IMAGE_TABLE_NAME);
        onCreate(database);
    }
}