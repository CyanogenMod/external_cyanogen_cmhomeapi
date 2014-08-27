package org.cyanogenmod.launcher.cardprovider;

import android.content.Context;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import org.cyanogenmod.launcher.cards.CmCard;

/**
 * An interface for classes that can manage data for and provide Cards to be displayed.
 */
public interface ICardProvider {
    public void onHide(Context context);
    public void onShow();
    public void onDestroy(Context context);
    public void requestRefresh();

    /**
     * Given a list of cards, update any card for which
     * there is new data available.
     * @param cards Cards to update
     * @return A list of cards that must be added
     */
    public CardProviderUpdateResult updateAndAddCards(List<CmCard> cards);

    /**
     * Given a card, update it to the freshest data.
     * @param card The card to update.
     */
    public void updateCard(CmCard card);

    /**
     * Given an ID known to this ICardProvider,
     * generate a card to represent the latest data
     * for that Card ID.
     * @param id An ID string known to this card provider, such as
     *           passed in CardProviderUpdateListener.onCardProviderUpdate
     */
    public CmCard createCardForId(String id);
    public List<CmCard> getCards();
    public void addOnUpdateListener(CardProviderUpdateListener listener);

    public interface CardProviderUpdateListener {
        public void onCardProviderUpdate(String cardId);
        public void onCardDelete(String cardId);
    }

    public class CardProviderUpdateResult {
        List<CmCard> mCardsToAdd;
        List<CmCard> mCardsToRemove;

        public CardProviderUpdateResult(List<CmCard> cardsToAdd, List<CmCard> cardsToRemove) {
            mCardsToAdd = cardsToAdd;
            mCardsToRemove = cardsToRemove;
        }

        public List<CmCard> getCardsToAdd() {
            return mCardsToAdd;
        }

        public List<CmCard> getCardsToRemove() {
            return mCardsToRemove;
        }
    }
}
