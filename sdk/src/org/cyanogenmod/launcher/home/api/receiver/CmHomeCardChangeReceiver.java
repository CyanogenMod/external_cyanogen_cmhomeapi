package org.cyanogenmod.launcher.home.api.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.cyanogenmod.launcher.home.api.cards.CardData;
import org.cyanogenmod.launcher.home.api.cards.CardData.CardDeletedInfo;

/**
 * A {@link android.content.BroadcastReceiver} that provides a method that will be called when a
 * Card is deleted. Subclass this class to handle card deletions by implementing
 * {@link org.cyanogenmod.launcher.home.api.receiver.CmHomeCardChangeReceiver#onCardDeleted}
 *
 * Be sure to declare your subclass of this receiver in the manifest with the
 * org.cyanogenmod.launcher.home.api.CARD_DELETED intent filter and protect it with the
 * org.cyanogenmod.launcher.home.api.FEED_WRITE permission as such:
 *
 * <pre>
 *     <receiver
 *         android:name="org.cyanogenmod.launcher.home.api.sdkexample.receiver.CardDeletedBroadcastReceiver">
 *         <intent-filter>
 *             <action android:name="org.cyanogenmod.launcher.home.api.CARD_DELETED" />
 *         </intent-filter>
 *     </receiver>
 * </pre>
 */
public abstract class CmHomeCardChangeReceiver extends BroadcastReceiver {
    public final static String CARD_DATA_DELETED_INFO_BROADCAST_EXTRA = "CardDataDeletedInfo";

    @Override
    public void onReceive(Context context, Intent intent) {
        CardDeletedInfo deletedInfo = (CardDeletedInfo)
                                intent.getParcelableExtra(CARD_DATA_DELETED_INFO_BROADCAST_EXTRA);
        onCardDeleted(context, deletedInfo);
    }

    /**
     * Called when a card has been deleted.
     * @param context The context of the BroadcastReceiver that received this Broadcast.
     * @param cardDeletedInfo Info related to the card that was deleted,
     *                        including several identifiers.
     */
    protected abstract void onCardDeleted(Context context,
                                          CardData.CardDeletedInfo cardDeletedInfo);

}
