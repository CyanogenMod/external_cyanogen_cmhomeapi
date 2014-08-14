package org.cyanogenmod.launcher.cards;

import org.cyanogenmod.launcher.cards.CardViewPopulator.PopulateInfo;

import it.gmariotti.cardslib.library.internal.Card;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class CmCard<A extends CardViewPopulator<B>, B extends PopulateInfo> extends Card {

    private LayoutInflater mInflater;
    private A mCardPopulator;
    private DataCardMatcher mMatcher;

    public LayoutInflater getInflater() {
        return mInflater;
    }

    public CmCard(Context context, int layoutId) {
        super(context, layoutId);
        init();
    }

    public CmCard(Context context) {
        super(context);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
    }

    public void setMatcher(DataCardMatcher matcher) {
        mMatcher = matcher;
    }

    public final void setCardPopulator(A customView) {
        mCardPopulator = customView;
    }

    public final A getCardPopulator() {
        return mCardPopulator;
    }

    @Override
    public final void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        parent.setMinimumHeight(0);
        if (mCardPopulator != null) {
            B info = getPopulateInfo(mInflater, parent, view);
            info.matcher = mMatcher;
            mCardPopulator.populateContent(info);
        }
    }

    public abstract B getPopulateInfo(LayoutInflater inflater, ViewGroup parent, View view);

}
