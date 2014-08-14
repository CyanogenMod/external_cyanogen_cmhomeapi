package org.cyanogenmod.launcher.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnUndoSwipeListListener;

import org.cyanogenmod.launcher.cardprovider.CmHomeApiCardProvider;
import org.cyanogenmod.launcher.cards.ApiCard.ApiCardPopulator;
import org.cyanogenmod.launcher.cards.CardViewPopulator.PopulateInfo;
import org.cyanogenmod.launcher.home.api.cards.CardData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cyanogenmod.launcher.cards.CardViewPopulator;

public class ApiCard<T extends ApiCardPopulator<S>, S extends ApiCard.ApiCardPopulateInfo>
    extends CmCard<T, S> implements OnUndoSwipeListListener {

    private CardData mCardData;

    public ApiCard(Context context, CardData cardData) {
        super(context);
        init(cardData);
    }

    public ApiCard(Context context, int innerLayout, CardData cardData) {
        super(context, innerLayout);
        init(cardData);
    }

    private void init(CardData cardData) {
        mCardData = cardData;
        setSwipeable(true);
        setOnUndoSwipeListListener(this);
        setId(cardData.getGlobalId());
    }

    public void setApiAuthority(String authority) {
        mCardData.setAuthority(authority);
    }

    public String getApiAuthority() {
        return mCardData.getAuthority();
    }
    
    public long getDbId() {
        return mCardData.getId();
    }

    public void updateFromCardData(CardData cardData) {
        mCardData = cardData;
        setId(cardData.getGlobalId());
    }

    public CardData getCardData() {
        return mCardData;
    }

    @Override
    public void onUndoSwipe(Card card, boolean timedOut) {
        if (timedOut) {
            boolean deleted = mCardData.unpublish(mContext);
            if (deleted) {
                CmHomeApiCardProvider.sendCardDeletedBroadcast(mContext, mCardData);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public S getPopulateInfo(LayoutInflater inflater, ViewGroup parent, View view) {
        ApiCardPopulateInfo info = new ApiCardPopulateInfo();
        info.content = view;
        info.inflater = inflater;
        info.cardData = mCardData;
        return (S) info;
    }

    public static class ApiCardPopulateInfo extends PopulateInfo {
        public CardData cardData;
    }

    public abstract static class ApiCardPopulator<T extends ApiCardPopulateInfo>
        implements CardViewPopulator<T> {
    }
}
