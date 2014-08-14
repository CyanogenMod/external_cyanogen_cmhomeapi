package org.cyanogenmod.launcher.cards;

import org.cyanogenmod.launcher.home.api.cards.CardData;

import com.cyanogen.cardbuilder.DataCardBuilder;

import android.content.Context;

public interface DataCardMatcher {
    public boolean hasMatchingContent(CardData cardData);
    public DataCardBuilder getBuilderForMatcher(Context context);
}
