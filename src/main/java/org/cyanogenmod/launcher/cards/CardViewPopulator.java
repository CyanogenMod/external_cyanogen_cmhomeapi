package org.cyanogenmod.launcher.cards;

import android.view.LayoutInflater;
import android.view.View;

import org.cyanogenmod.launcher.cards.CardViewPopulator.PopulateInfo;

public interface CardViewPopulator<T extends PopulateInfo> {

    public static class PopulateInfo {
        public LayoutInflater inflater;
        public View content;
        public DataCardMatcher matcher;
    }

    public void populateContent(T t);

}