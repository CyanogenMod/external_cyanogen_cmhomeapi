package org.cyanogenmod.launcher.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnUndoSwipeListListener;
import android.content.Context;

public abstract class CmCard extends Card implements OnUndoSwipeListListener {
    public static final String NO_CATEGORY = "noCategory";
    private String mCategory = NO_CATEGORY;

    public CmCard(Context context) {
        super(context);
        init();
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getCategory() {
        return mCategory;
    }

    public CmCard(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }

    private void init() {
        setSwipeable(true);
        setOnUndoSwipeListListener(this);
    }

    @Override
    public abstract void onUndoSwipe(Card card, boolean timedOut);

}
