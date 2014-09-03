package org.cyanogenmod.launcher.home.api.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.cyanogenmod.launcher.home.api.cards.CardData;
import org.cyanogenmod.launcher.home.api.cards.CardData.CardDeletedInfo;

/**
 * A <a href="http://developer.android.com/reference/android/content/BroadcastReceiver.html">BroadcastReceiver</a>
 * that provides a method that will be called when a
 * Card is deleted and when a Card Data refresh has been requested. Subclass this class to handle
 * card deletions by implementing
 * {@link #onCardDeleted(android.content.Context, CardData.CardDeletedInfo)} and
 * {@link #onRefreshRequested(android.content.Context)}.
 *
 * Be sure to declare your subclass of this receiver in the manifest with the
 * org.cyanogenmod.launcher.home.api.CARD_DELETED and
 * org.cyanogenmod.launcher.home.api.REFRESH_REQUESTED intent filters. Also,
 * protect it with the org.cyanogenmod.launcher.home.api.FEED_HOST permission as such:
 *
 * <pre>
 * {@code
 * <receiver
 *     android:name="org.cyanogenmod.launcher.home.api.sdkexample.receiver.CardDeletedBroadcastReceiver"
 *     android:permission="org.cyanogenmod.launcher.home.api.FEED_HOST">
 *     <intent-filter>
 *         <action android:name="org.cyanogenmod.launcher.home.api.CARD_DELETED" />
 *         <action android:name="org.cyanogenmod.launcher.home.api.REFRESH_REQUESTED" />
 *     </intent-filter>
 * </receiver>
 * }
 * </pre>
 */
public abstract class CmHomeCardChangeReceiver extends BroadcastReceiver {
    /**
     * The broadcast extra that contains the CardDataDeletedInfo object for the deleted card.
     *
     * <b>You do not need to retrieve this yourself, simply override #onCardDeleted(Context,
     * CardDeletedInfo) instead.</b>
     */
    public final static String CARD_DATA_DELETED_INFO_BROADCAST_EXTRA = "CardDataDeletedInfo";

    private static final String CM_HOME_API_CARD_DELETED_BROADCAST_ACTION =
                                            "org.cyanogenmod.launcher.home.api.CARD_DELETED";
    private static final String CM_HOME_API_REFRESH_REQUESTED_BROADCAST_ACTION =
                                            "org.cyanogenmod.launcher.home.api.REFRESH_REQUESTED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (CM_HOME_API_CARD_DELETED_BROADCAST_ACTION.equals(intent.getAction())) {
            CardDeletedInfo deletedInfo = (CardDeletedInfo)
                    intent.getParcelableExtra(CARD_DATA_DELETED_INFO_BROADCAST_EXTRA);
            onCardDeleted(context, deletedInfo);
        } else if (CM_HOME_API_REFRESH_REQUESTED_BROADCAST_ACTION.equals(intent.getAction())) {
            onRefreshRequested(context);
        }
    }

    /**
     * Called when a card has been deleted.
     * @param context The context of the BroadcastReceiver that received this Broadcast.
     * @param cardDeletedInfo Info related to the card that was deleted,
     *                        including several identifiers.
     */
    protected abstract void onCardDeleted(Context context,
                                          CardData.CardDeletedInfo cardDeletedInfo);

    /**
     * Called when a data refresh has been requested by CM Home. This could occur periodically,
     * when the user has opened CM Home, or when the user has explicitly requested a refresh.
     *
     * Applications implmenting this method should take this opportunity to remove stale cards,
     * publish new ones and update existing cards. If there are no updates that must be made,
     * it is safe to ignore this Broadcast.
     * @param context The context of the BroadcastReceiver that received this Broadcast.
     */
    protected abstract void onRefreshRequested(Context context);
}
