package org.cyanogenmod.launcher.home.api.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <p>The database contracts for all tables in the CM Home extension Database.</p>
 * <p><b>This class is intended to be internal, and does not need to be referenced by
 * applications using the SDK.</b></p>
 */
public class CmHomeContract {
    /**
     * The authority for the CM Home API provider that exists in this application.
     */
    public static String AUTHORITY =
            "org.cyanogenmod.launcher.home.api";

    /**
     * The base content URI for the CM Home API provider that exists in this application.
     */
    public static Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    /**
     * The database contract for persisting
     * {@link org.cyanogenmod.launcher.home.api.cards.CardData} objects.
     *
     * <p><b>This class is intended to be internal, and does not need to be referenced by
     * applications using the SDK.</b></p>
     */
    public static final class CardDataContract implements BaseColumns, ICmHomeContract {
        public static final String INTERNAL_ID_COL          = "internal_id";
        public static final String REASON_COL               = "reason_text";
        public static final String DATE_CONTENT_CREATED_COL = "date_content_created";
        public static final String DATE_CREATED_COL = "date_created";
        public static final String LAST_MODIFIED_COL = "last_modified";
        public static final String CONTENT_SOURCE_IMAGE_URI_COL = "content_source_image_uri";
        public static final String AVATAR_IMAGE_URI_COL = "avatar_image_uri";
        public static final String TITLE_TEXT_COL = "title_text";
        public static final String SMALL_TEXT_COL = "small_text";
        public static final String BODY_TEXT_COL = "body_text";
        public static final String CATEGORY_COL = "category";
        public static final String CARD_CLICK_URI_COL = "card_click_uri";
        public static final String ACTION_1_TEXT_COL = "action_1_text";
        public static final String ACTION_1_URI_COL = "action_1_uri";
        public static final String ACTION_2_TEXT_COL = "action_2_text";
        public static final String ACTION_2_URI_COL = "action_2_uri";
        public static final String PRIORITY_COL = "priority";

        public static final String IS_BROADCAST_INTENT_EXTRA = "cmHomeIntentIsBroadcast";

        public static Uri CONTENT_URI =
                Uri.withAppendedPath(CmHomeContract.CONTENT_URI, "carddata");

        public static String LIST_INSERT_UPDATE_URI_PATH = "carddata";
        public static String SINGLE_ROW_INSERT_UPDATE_URI_PATH = "carddata/#";
        public static String SINGLE_ROW_DELETE_URI_PATH = "carddata/delete";
        public static String SINGLE_ROW_DELETE_URI_PATH_MATCH = "carddata/delete/#";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.cyanogenmod.home.api.carddata";

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.cyanogenmod.home.api.carddata";

        public static final String[] PROJECTION_ALL =
                {_ID, INTERNAL_ID_COL, REASON_COL, DATE_CONTENT_CREATED_COL, DATE_CREATED_COL,
                 LAST_MODIFIED_COL, CONTENT_SOURCE_IMAGE_URI_COL, AVATAR_IMAGE_URI_COL,
                 TITLE_TEXT_COL, SMALL_TEXT_COL, BODY_TEXT_COL, CATEGORY_COL, CARD_CLICK_URI_COL,
                 ACTION_1_TEXT_COL, ACTION_1_URI_COL, ACTION_2_TEXT_COL, ACTION_2_URI_COL,
                 PRIORITY_COL};

        public static final String SORT_ORDER_DEFAULT =
                PRIORITY_COL + " ASC";

        @Override
        public Uri getContentUri() {
            return CONTENT_URI;
        }

        @Override
        public String getIdColumnName() {
            return _ID;
        }
    }

    /**
     * The database contract for persisting
     * {@link org.cyanogenmod.launcher.home.api.cards.CardDataImage} objects.
     *
     * <p><b>This class is intended to be internal, and does not need to be referenced by
     * applications using the SDK.</b></p>
     */
    public static final class CardDataImageContract implements BaseColumns, ICmHomeContract {
        public static final String INTERNAL_ID_COL  = "internal_id";
        public static final String CARD_DATA_ID_COL = "card_data_id";
        public static final String IMAGE_URI_COL    = "image_uri";
        public static final String IMAGE_LABEL_COL  = "image_label";

        public static Uri CONTENT_URI =
                Uri.withAppendedPath(CmHomeContract.CONTENT_URI, "carddataimage");

        public static String LIST_INSERT_UPDATE_URI_PATH       = "carddataimage";
        public static String SINGLE_ROW_INSERT_UPDATE_URI_PATH = "carddataimage/#";
        public static String SINGLE_ROW_DELETE_URI_PATH        = "carddataimage/delete";
        public static String SINGLE_ROW_DELETE_URI_PATH_MATCH  = "carddataimage/delete/#";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.cyanogenmod.home.api.carddataimage";

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/org.cyanogenmod.home.api.carddataimage";

        public static final String[] PROJECTION_ALL =
                {_ID, INTERNAL_ID_COL, CARD_DATA_ID_COL, IMAGE_URI_COL, IMAGE_LABEL_COL};

        public static final String SORT_ORDER_DEFAULT =
                CARD_DATA_ID_COL + " ASC";

        @Override
        public Uri getContentUri() {
            return CONTENT_URI;
        }

        @Override
        public String getIdColumnName() {
            return _ID;
        }
    }

    /**
     * The provider constants for exposing cached images from this application to CM Home.
     *
     * <p><b>This class is intended to be internal, and does not need to be referenced by
     * applications using the SDK.</b></p>
     */
    public static final class ImageFile {
        public static final String PATH = "imagefile";
        public static Uri CONTENT_URI  =
                Uri.withAppendedPath(CmHomeContract.CONTENT_URI, "imagefile");
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/org.cyanogenmod.home.api.imagefile";
    }

    /**
     * Sets the static {@link #AUTHORITY} constant and all derived Uris to use a new authority
     * String.
     *
     * @param authority The authority to use to set all content uris to use.
     */
    public static void setAuthority(String authority) {
        AUTHORITY = authority;
        CONTENT_URI = Uri.parse("content://" + AUTHORITY);
        CardDataContract.CONTENT_URI = Uri.withAppendedPath(CmHomeContract.CONTENT_URI, "carddata");
        CardDataImageContract.CONTENT_URI =
                Uri.withAppendedPath(CmHomeContract.CONTENT_URI, "carddataimage");
        ImageFile.CONTENT_URI = Uri.withAppendedPath(CmHomeContract.CONTENT_URI, "imagefile");
    }

    /**
     * A basic interface for all CM Home database Contract classes.
     *
     * <p><b>This class is intended to be internal, and does not need to be referenced by
     * applications using the SDK.</b></p>
     */
    public interface ICmHomeContract {
        public Uri getContentUri();

        public String getIdColumnName();
    }
}
