package org.cyanogenmod.launcher.home.api.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.cyanogenmod.launcher.home.api.cards.DataCard;

public abstract class CmHomeCardChangeReceiver extends BroadcastReceiver {
    private static final String CARD_ID_EXTRA = "cardId";
    private static final String CARD_INTERNAL_ID_EXTRA = "cardInternalId";
    private static final String CARD_GLOBAL_ID_EXTRA = "cardGlobalId";
    private static final String CARD_AUTHORITY_EXTRA = "cardAuthorityId";

    @Override
    public void onReceive(Context context, Intent intent) {
        long cardId = intent.getLongExtra(CARD_ID_EXTRA, -1);
        String internalId = intent.getStringExtra(CARD_INTERNAL_ID_EXTRA);
        String globalId = intent.getStringExtra(CARD_GLOBAL_ID_EXTRA);
        String authority = intent.getStringExtra(CARD_AUTHORITY_EXTRA);

        DataCard.CardDeletedInfo deletedInfo = new DataCard.CardDeletedInfo(cardId,
                                                                            internalId,
                                                                            globalId,
                                                                            authority);
        onCardDeleted(deletedInfo);
    }

    protected abstract void onCardDeleted(DataCard.CardDeletedInfo cardDeletedInfo);

}
