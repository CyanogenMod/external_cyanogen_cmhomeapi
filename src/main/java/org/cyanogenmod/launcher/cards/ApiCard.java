package org.cyanogenmod.launcher.cards;

import android.content.Context;

import org.cyanogenmod.launcher.home.api.cards.DataCard;

import it.gmariotti.cardslib.library.internal.Card;

public class ApiCard extends Card {
    private String mApiAuthority;
    private long mDbId;

    public ApiCard(Context context) {
        super(context);
    }

    public ApiCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void setApiAuthority(String authority) {
        mApiAuthority = authority;
    }

    public String getApiAuthority() {
        return mApiAuthority;
    }

    public long getDbId() {
        return mDbId;
    }

    public void setDbId(long dbId) {
        mDbId = dbId;
    }

    public void updateFromDataCard(DataCard dataCard) {
        setId(dataCard.getGlobalId());
        setDbId(dataCard.getId());
        setApiAuthority(dataCard.getAuthority());
        setSwipeable(true);
    }
}