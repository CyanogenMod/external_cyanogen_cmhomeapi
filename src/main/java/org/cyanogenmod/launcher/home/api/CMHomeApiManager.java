package org.cyanogenmod.launcher.home.api;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;

import org.cyanogenmod.launcher.cardprovider.ApiCardPackageChangedReceiver;
import org.cyanogenmod.launcher.cardprovider.CmHomeApiCardProvider;
import org.cyanogenmod.launcher.cards.ApiCard;
import org.cyanogenmod.launcher.home.api.cards.CardData;
import org.cyanogenmod.launcher.home.api.cards.CardDataImage;
import org.cyanogenmod.launcher.home.api.provider.CmHomeContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMHomeApiManager {
    private final static String TAG = "CMHomeApiManager";
    private final static String FEED_READ_PERM = "org.cyanogenmod.launcher.home.api.FEED_READ";
    private final static String FEED_WRITE_PERM = "org.cyanogenmod.launcher.home.api.FEED_WRITE";
    private static final int    CARD_DATA_LIST                = 1;
    private static final int    CARD_DATA_ITEM                = 2;
    private static final int    CARD_DATA_DELETE_ITEM         = 3;
    private static final int    CARD_DATA_IMAGE_LIST          = 4;
    private static final int    CARD_DATA_IMAGE_ITEM          = 5;
    private static final int    CARD_DATA_IMAGE_DELETE_ITEM   = 6;
    private static final int    UPDATE_CARD_DATA_MESSAGE_WHAT = 0;
    private static final int    DELETE_CARD_DATA_MESSAGE_WHAT = 1;
    private static final String CARD_MESSAGE_BUNDLE_ID_KEY    = "CardId";

    // All provider authorities that contain Cards.
    private List<String> mProviders = new ArrayList<String>();
    // Provider authority string -> SparseArray from card ID -> CardData
    private HashMap<String, LongSparseArray<CardData>> mCards = new HashMap<String,
                                                                  LongSparseArray<CardData>>();

    private CardContentObserver mContentObserver;
    private HandlerThread mContentObserverHandlerThread;
    private Handler mContentObserverHandler;
    private ICMHomeApiUpdateListener mApiUpdateListener;
    private ApiCardPackageChangedReceiver mPackageChangedReceiver;

    private Handler mUiThreadHandler = new Handler() {
        @Override
        public
        void handleMessage(Message msg) {
            super.handleMessage(msg);
            String globalId = msg.getData().getString(CARD_MESSAGE_BUNDLE_ID_KEY);

            if (!TextUtils.isEmpty(globalId)) {
                switch (msg.what) {
                    case UPDATE_CARD_DATA_MESSAGE_WHAT:
                        // Update listeners that a card has changed.
                        mApiUpdateListener.onCardInsertOrUpdate(globalId);
                        break;
                    case DELETE_CARD_DATA_MESSAGE_WHAT:
                        globalId = msg.getData().getString(CARD_MESSAGE_BUNDLE_ID_KEY);
                        // Update listeners that a card has been deleted
                        mApiUpdateListener.onCardDelete(globalId);
                        break;
                    default:
                        // nothing
                }
            }
        }
    };

    private Context mContext;

    public CMHomeApiManager(Context context) {
        mContext = context;
        mPackageChangedReceiver = new ApiCardPackageChangedReceiver(this);
        init();
    }

    public boolean hasCard(String apiAuthority, long cardId) {
        LongSparseArray<CardData> cards = mCards.get(apiAuthority);
        boolean hasCard = false;
        if (cards != null) {
            hasCard = cards.get(cardId) != null;
        }
        return hasCard;
    }

    public CardData getCard(String apiAuthority, long cardId) {
        LongSparseArray<CardData> cards = mCards.get(apiAuthority);
        CardData card = null;
        if (cards != null) {
            card = cards.get(cardId);
        }
        return card;
    }

    public CardData getCardWithGlobalId(String cardId) {
        String[] idParts = cardId.split("/");
        if (idParts.length > 1) {
            String apiAuthority = idParts[0];
            String idString = idParts[1];
            try {
                long id = Long.parseLong(idString);
                return getCard(apiAuthority, id);
            } catch (NumberFormatException e) {
                // This card is either malformed or not a
                // CmHome API card. Return null.
            }
        }

        return null;
    }

    public void setApiUpdateListener(ICMHomeApiUpdateListener listener) {
        mApiUpdateListener = listener;
    }

    public void init() {
        // Start up a background thread to handle any incoming changes.
        mContentObserverHandlerThread = new HandlerThread("CMHomeApiObserverThread");
        mContentObserverHandlerThread.start();
        mContentObserverHandler = new Handler(mContentObserverHandlerThread.getLooper());

        // Register the package changed broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        intentFilter.addDataScheme("package");
        mContext.registerReceiver(mPackageChangedReceiver, intentFilter);

        new LoadExtensionsAndCardsAsync().execute();
    }

    public void destroy() {
        mContentObserverHandlerThread.quitSafely();
        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
        mContext.unregisterReceiver(mPackageChangedReceiver);
    }

    private class LoadExtensionsAndCardsAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected
        Void doInBackground(Void... voids) {
            loadAllExtensions();
            trackAllExtensions();
            loadAllCards();
            return null;
        }
    }

    private void loadAllExtensions() {
        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> providerPackages =
                pm.getInstalledPackages(PackageManager.GET_PROVIDERS);
        for (PackageInfo packageInfo : providerPackages) {
            loadExtensionIfSupported(packageInfo);
        }
    }

    private String loadExtensionIfSupported(PackageInfo packageInfo) {
        if (packageInfo != null) {
            ProviderInfo[] providers = packageInfo.providers;
            if (providers != null) {
                for (ProviderInfo providerInfo : providers) {
                    if (FEED_READ_PERM.equals(providerInfo.readPermission)
                        && FEED_WRITE_PERM.equals(providerInfo.writePermission)) {
                        mProviders.add(providerInfo.authority);
                        return providerInfo.authority;
                    }
                }
            }
        }
        return null;
    }

    private void loadExtensionAndCardsForPackageIfSupported(String packageName,
                                                            boolean notifyListener) {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PROVIDERS);
            String authority = loadExtensionIfSupported(packageInfo);

            boolean alreadyExists = mProviders.contains(authority) &&
                                    mCards.containsKey(authority);

            // If the provider is already being tracked, our work is done
            if (authority != null && !alreadyExists) {
                trackExtension(authority);
                loadCards(authority, notifyListener);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Unable to load providers for package: " + packageName);
        }
    }

    private void removeAllCardsForPackage(String packageName) {
        String providerAuthority = packageName + CmHomeApiCardProvider.CARD_AUTHORITY_APPEND_STRING;
        LongSparseArray<CardData> cards = mCards.get(providerAuthority);
        if (cards != null) {
            // Notify listener that all cards will be deleted
            for (int i = 0; i < cards.size(); i++) {
                CardData cardData = cards.valueAt(i);

                sendNotifyCardDataChangeMessageOnUIHandler(cardData.getGlobalId(),
                                                           DELETE_CARD_DATA_MESSAGE_WHAT);
            }

            // Clear storage of all cards for this provider
            mProviders.remove(providerAuthority);
            mCards.remove(providerAuthority);
        }
    }

    private void trackExtension(String authority) {
        if (mContentObserver == null) {
            mContentObserver = new CardContentObserver(mContentObserverHandler);
        }

        ContentResolver contentResolver = mContext.getContentResolver();

        Uri getCardsUri = Uri.parse("content://" + authority + "/" +
                                         CmHomeContract.CardDataContract
                                                 .LIST_INSERT_UPDATE_URI_PATH);
        Uri getImagesUri = Uri.parse("content://" + authority + "/" +
                                     CmHomeContract.CardDataImageContract
                                             .LIST_INSERT_UPDATE_URI_PATH);
        contentResolver.registerContentObserver(getCardsUri,
                                                     true,
                                                     mContentObserver);
        contentResolver.registerContentObserver(getImagesUri,
                                                     true,
                                                     mContentObserver);
    }

    private void trackAllExtensions() {
        mContentObserver = new CardContentObserver(mContentObserverHandler);

        for (String authority : mProviders) {
            trackExtension(authority);
        }
    }

    private void loadCards(String authority, boolean notifyListener) {
        Uri getCardsUri = Uri.parse("content://" + authority + "/" +
                                    CmHomeContract.CardDataContract
                                            .LIST_INSERT_UPDATE_URI_PATH);
        Uri getImagesUri = Uri.parse("content://" + authority + "/" +
                                     CmHomeContract.CardDataImageContract
                                             .LIST_INSERT_UPDATE_URI_PATH);
        List<CardData> cards = CardData.getAllPublishedCardDatas(mContext,
                                                                 getCardsUri,
                                                                 getImagesUri);

        //For quick access, build a LongSparseArray using the id as the key
        LongSparseArray<CardData> cardMap = mCards.get(authority);
        if (cardMap == null) {
            cardMap = new LongSparseArray<CardData>();
            mCards.put(authority, cardMap);
        }

        for (CardData card : cards) {
            cardMap.put(card.getId(), card);

            if (notifyListener) {
                sendNotifyCardDataChangeMessageOnUIHandler(card.getGlobalId(),
                                                           UPDATE_CARD_DATA_MESSAGE_WHAT);
            }
        }
    }

    private void loadAllCards() {
        for (String authority : mProviders) {
            loadCards(authority, false);
        }
    }

    private class CardContentObserver extends ContentObserver {

        public CardContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (!selfChange) {
                handleUriChange(uri);
            }
        }
    }

    private void handleUriChange(Uri uri) {
        String authority = uri.getAuthority();
        UriMatcher matcher = getUriMatcherForAuthority(authority);
        switch (matcher.match(uri)) {
            case CARD_DATA_LIST:
                // Todo: figure out what rows were changed?
                // It might not be trivial to handle a list of changes
                break;
            case CARD_DATA_ITEM:
                onCardInsertOrUpdate(uri);
                break;
            case CARD_DATA_DELETE_ITEM:
                onCardDelete(uri);
                break;
            case CARD_DATA_IMAGE_LIST:
                // Todo: figure out what rows were changed?
                // It might not be trivial to handle a list of changes
                break;
            case CARD_DATA_IMAGE_ITEM:
                onCardImageInsertOrUpdate(uri);
                break;
            case CARD_DATA_IMAGE_DELETE_ITEM:
                onCardImageDelete(uri);
                break;
            default:
                Log.w(TAG, "Unsupported Uri change notification: " + uri);
        }
    }

    private void onCardInsertOrUpdate(Uri uri) {
        String authority = uri.getAuthority();
        String idString = uri.getLastPathSegment();
        long id = Long.parseLong(idString);
        LongSparseArray<CardData> cards = mCards.get(authority);

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(uri,
                                              CmHomeContract.CardDataContract.PROJECTION_ALL,
                                              null,
                                              null,
                                              CmHomeContract.CardDataContract.DATE_CREATED_COL);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            CardData theNewCard = CardData.createFromCurrentCursorRow(cursor, authority);
            if (cards == null) {
                cards = new LongSparseArray<CardData>();
                cards.put(theNewCard.getId(), theNewCard);
                mCards.put(authority, cards);
            } else {
                cards.put(theNewCard.getId(), theNewCard);
            }

            sendNotifyCardDataChangeMessageOnUIHandler(theNewCard.getGlobalId(),
                                                       UPDATE_CARD_DATA_MESSAGE_WHAT);
        }

        cursor.close();
    }

    private void onCardDelete(Uri uri) {
        String authority = uri.getAuthority();
        LongSparseArray<CardData> cards = mCards.get(authority);
        if (cards != null) {
            long id = Long.parseLong(uri.getLastPathSegment());
            String globalId = cards.get(id).getGlobalId();
            cards.delete(id);

            Message uiMessage = new Message();
            uiMessage.what = DELETE_CARD_DATA_MESSAGE_WHAT;
            Bundle messageData = new Bundle();
            messageData.putString(CARD_MESSAGE_BUNDLE_ID_KEY, globalId);
            uiMessage.setData(messageData);

            mUiThreadHandler.sendMessage(uiMessage);
        }
    }

    private void onCardImageInsertOrUpdate(Uri uri) {
        // Get CardDataImage from URI id
        String authority = uri.getAuthority();
        String idString = uri.getLastPathSegment();
        long id = Long.parseLong(idString);

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(uri,
                                              CmHomeContract.CardDataImageContract.PROJECTION_ALL,
                                              null,
                                              null,
                                              null);


        CardDataImage newImage = null;
        if (cursor.getCount() > 0) {
            // This will be called only for a single image insertion or update
            cursor.moveToFirst();
            newImage = CardDataImage.createFromCurrentCursorRow(cursor, authority);
        }

        // Find the CardData it is associated with and update its images
        if (newImage != null) {
            CardData associatedCard = getCard(authority, newImage.getCardDataId());
            associatedCard.addOrUpdateCardDataImage(newImage);

            // Call update on that CardDataImage
            sendNotifyCardDataChangeMessageOnUIHandler(associatedCard.getGlobalId(),
                                                       UPDATE_CARD_DATA_MESSAGE_WHAT);
        }
    }

    private void onCardImageDelete(Uri uri) {
        // Get a DataCardImage with the ID in the uri
        // Find the datacard associated with it and remove the image
        // call update on that datacard
    }

    /**
     * Sends a message to mUIThreadHandler to notify listeners on the UI thread of a card
     * deletion or removal.
     * @param globalId The global ID of the card to remove.
     * @param what One of UPDATE_CARD_DATA_MESSAGE_WHAT or DELETE_CARD_DATA_MESSAGE_WHAT.
     */
    private void sendNotifyCardDataChangeMessageOnUIHandler(String globalId, int what) {
        Message uiMessage = new Message();
        uiMessage.what = what;
        Bundle messageData = new Bundle();
        messageData.putString(CARD_MESSAGE_BUNDLE_ID_KEY, globalId);
        uiMessage.setData(messageData);

        mUiThreadHandler.sendMessage(uiMessage);
    }

    private UriMatcher getUriMatcherForAuthority(String authority) {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(authority,
                       CmHomeContract.CardDataContract.LIST_INSERT_UPDATE_URI_PATH,
                       CARD_DATA_LIST);
        matcher.addURI(authority,
                       CmHomeContract.CardDataContract.SINGLE_ROW_INSERT_UPDATE_URI_PATH,
                       CARD_DATA_ITEM);
        matcher.addURI(authority,
                       CmHomeContract.CardDataContract.SINGLE_ROW_DELETE_URI_PATH_MATCH,
                       CARD_DATA_DELETE_ITEM);
        matcher.addURI(authority,
                       CmHomeContract.CardDataImageContract.LIST_INSERT_UPDATE_URI_PATH,
                       CARD_DATA_IMAGE_LIST);
        matcher.addURI(authority,
                       CmHomeContract.CardDataImageContract.SINGLE_ROW_INSERT_UPDATE_URI_PATH,
                       CARD_DATA_IMAGE_ITEM);
        matcher.addURI(authority,
                       CmHomeContract.CardDataImageContract.SINGLE_ROW_DELETE_URI_PATH_MATCH,
                       CARD_DATA_IMAGE_DELETE_ITEM);
        return matcher;
    }

    public List<CardData> getAllCardDatas() {
        List<CardData> theCards = new ArrayList<CardData>();
        for (LongSparseArray<CardData> cards : mCards.values()) {
            for (int i = 0; i < cards.size(); i++) {
                theCards.add(cards.valueAt(i));
            }
        }
        return theCards;
    }

    public interface ICMHomeApiUpdateListener {
        public void onCardInsertOrUpdate(String globalId);
        public void onCardDelete(String globalId);
    }

    /**
     * Loads and Adds all cards from the given package and registers to track for changes,
     * asynchronously. Then, notifies listeners of their removal on the UI thread.
     */
    private class LoadExtensionAndAddCardsForPackageTask extends AsyncTask<Void, Void, Void> {
        String mPackageName;
        boolean mNotifyListeners = false;

        public LoadExtensionAndAddCardsForPackageTask(String packageName, boolean notifyListeners) {
            mPackageName = packageName;
            mNotifyListeners = notifyListeners;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            loadExtensionAndCardsForPackageIfSupported(mPackageName, mNotifyListeners);
            return null;
        }
    }

    /**
     * Removes all cards from the given package asynchronously and then notifies listeners of their
     * removal on the UI thread.
     */
    private class RemoveAllCardsForPackageTask extends AsyncTask<Void, Void, Void> {
        String mPackageName;

        public RemoveAllCardsForPackageTask(String packageName) {
            mPackageName = packageName;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            removeAllCardsForPackage(mPackageName);
            return null;
        }
    }

    /**
     * Update the collection of cards contained in this CmHomeApiManager when
     * a package has been added, changed or removed.
     * @param action Should be one of {@link android.content.Intent#ACTION_PACKAGE_ADDED},
     *     {@link android.content.Intent#ACTION_PACKAGE_CHANGED}, or
     *     {@link android.content.Intent#ACTION_PACKAGE_REMOVED},
     *     ApiCardPackageChangedReceiver.PACKAGE_CHANGED_ENABLE_PROVIDER,
     *     ApiCardPackageChangedReceiver.PACKAGE_CHANGED_DISABLE_PROVIDER, or
     *     {@link android.content.Intent#ACTION_PACKAGE_DATA_CLEARED}.
     *     Any other action Strings will be ignored.
     * @param packageName The package name of the application that has been updated.
     */
    public void onPackageChanged(String action, String packageName) {
        if (Intent.ACTION_PACKAGE_ADDED.equals(action) ||
            ApiCardPackageChangedReceiver.PACKAGE_CHANGED_ENABLE_PROVIDER.equals(action)) {
            new LoadExtensionAndAddCardsForPackageTask(packageName, true).execute();
        } else if (ApiCardPackageChangedReceiver.PACKAGE_CHANGED_DISABLE_PROVIDER.equals(action) ||
                   Intent.ACTION_PACKAGE_REMOVED.equals(action) ||
                   Intent.ACTION_PACKAGE_DATA_CLEARED.equals(action)) {
            new RemoveAllCardsForPackageTask(packageName).execute();
        }
    }
}
