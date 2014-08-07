package org.cyanogenmod.launcher.home.api.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.cyanogenmod.launcher.home.api.cards.CardData;
import org.cyanogenmod.launcher.home.api.cards.CardData.CardDeletedInfo;

public abstract class CmHomeCardChangeReceiver extends BroadcastReceiver {
    public final static String CARD_DATA_DELETED_INFO_BROADCAST_EXTRA = "CardDataDeletedInfo";

    @Override
    public void onReceive(Context context, Intent intent) {
        CardDeletedInfo deletedInfo = (CardDeletedInfo)
                                intent.getParcelableExtra(CARD_DATA_DELETED_INFO_BROADCAST_EXTRA);
        onCardDeleted(context, deletedInfo);
    }

    protected abstract void onCardDeleted(Context context,
                                          CardData.CardDeletedInfo cardDeletedInfo);

}
