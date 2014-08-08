package org.cyanogenmod.launcher.home.api.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import org.cyanogenmod.launcher.home.api.cards.DataCard;
import org.cyanogenmod.launcher.home.api.cards.DataCard.CardDeletedInfo;

public abstract class CmHomeCardChangeReceiver extends BroadcastReceiver {
    public final String DATA_CARD_DELETED_INFO_BROADCAST_EXTRA = "dataCardDeletedInfo";

    @Override
    public void onReceive(Context context, Intent intent) {
        CardDeletedInfo deletedInfo = (CardDeletedInfo)
                                intent.getParcelableExtra(DATA_CARD_DELETED_INFO_BROADCAST_EXTRA);
        onCardDeleted(deletedInfo);
    }

    protected abstract void onCardDeleted(DataCard.CardDeletedInfo cardDeletedInfo);

}
