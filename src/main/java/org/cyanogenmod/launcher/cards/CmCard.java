package org.cyanogenmod.launcher.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnUndoSwipeListListener;
import android.content.Context;

public abstract class CmCard extends Card implements OnUndoSwipeListListener {

    public CmCard(Context context) {
        super(context);
        init();
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
