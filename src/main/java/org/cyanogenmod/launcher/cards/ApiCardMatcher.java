package org.cyanogenmod.launcher.cards;

import android.content.Context;
import org.cyanogenmod.launcher.home.api.cards.CardData;

public interface ApiCardMatcher {
    public boolean hasMatchingContent(CardData cardData);
    public ApiCard getCardForMatcher(Context context, CardData data);
}