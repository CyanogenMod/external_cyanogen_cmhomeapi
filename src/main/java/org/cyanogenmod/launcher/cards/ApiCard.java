package org.cyanogenmod.launcher.cards;

import android.content.Context;

import org.cyanogenmod.launcher.home.api.cards.DataCard;

import it.gmariotti.cardslib.library.internal.Card;

public class ApiCard extends Card {

    private DataCard mDataCard;

    public ApiCard(Context context, DataCard dataCard) {
        super(context);
        init(dataCard);
    }

    public ApiCard(Context context, int innerLayout, DataCard dataCard) {
        super(context, innerLayout);
        init(dataCard);
    }

    private void init(DataCard dataCard) {
        mDataCard = dataCard;
        setSwipeable(true);
        if (dataCard != null) {
            setId(dataCard.getGlobalId());
        }
    }
    public void setApiAuthority(String authority) {
        mDataCard.setAuthority(authority);
    }

    public String getApiAuthority() {
        return mDataCard.getAuthority();
    }

    public long getDbId() {
        return mDataCard.getId();
    }

    public void updateFromDataCard(DataCard dataCard) {
        mDataCard = dataCard;
    }

    public DataCard getDataCard() {
        return mDataCard;
    }
}