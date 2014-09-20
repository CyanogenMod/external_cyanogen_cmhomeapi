package org.cyanogenmod.launcher.cardprovider;

import android.content.Context;
import android.content.Intent;

import org.cyanogenmod.launcher.cards.ApiCard;
import org.cyanogenmod.launcher.cards.CmCard;
import org.cyanogenmod.launcher.home.api.CMHomeApiManager;
import org.cyanogenmod.launcher.home.api.cards.CardData;
import org.cyanogenmod.launcher.home.api.cards.CardData.CardDeletedInfo;
import org.cyanogenmod.launcher.home.api.receiver.CmHomeCardChangeReceiver;

import com.cyanogen.cardbuilder.DataCardBuilderFactory;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;

public class CmHomeApiCardProvider implements ICardProvider,
        CMHomeApiManager.ICMHomeApiUpdateListener {
    private CMHomeApiManager mApiManager;
    private Context mCmHomeContext;
    private Context mHostActivityContext;
    private List<CardProviderUpdateListener> mUpdateListeners =
                                            new ArrayList<CardProviderUpdateListener>();
    private static final String CM_HOME_API_CARD_DELETED_BROADCAST_ACTION =
                                            "org.cyanogenmod.launcher.home.api.CARD_DELETED";
    private static final String CM_HOME_API_REFRESH_REQUESTED_BROADCAST_ACTION =
                                            "org.cyanogenmod.launcher.home.api.REFRESH_REQUESTED";
    public static final String CARD_AUTHORITY_APPEND_STRING = ".cmhomeapi";

    public CmHomeApiCardProvider(Context cmHomeContext, Context hostActivityContext) {
        mCmHomeContext = cmHomeContext;
        mHostActivityContext = hostActivityContext;

        if (mApiManager == null) {
            mApiManager = new CMHomeApiManager(mHostActivityContext);
            mApiManager.setApiUpdateListener(this);
        }
    }

    public CmHomeApiCardProvider(Context oneContext) {
        mCmHomeContext = oneContext;
        mHostActivityContext = oneContext;

        if (mApiManager == null) {
            mApiManager = new CMHomeApiManager(mHostActivityContext);
            mApiManager.setApiUpdateListener(this);
        }
    }

    @Override
    public void onHide(Context context) {
    }

    @Override
    public void onShow() {
    }

    @Override
    public void onDestroy(Context context) {
        mApiManager.destroy();
    }

    @Override
    public void requestRefresh() {
        sendRefreshBroadcast();
    }

    private void sendRefreshBroadcast() {
        if (mHostActivityContext != null) {
            Intent broadcast = new Intent();
            broadcast.setAction(CM_HOME_API_REFRESH_REQUESTED_BROADCAST_ACTION);
            mHostActivityContext.sendBroadcast(broadcast);
        }
    }

    private boolean cardExists(String globalId, List<CmCard> cards) {
        for (CmCard card : cards) {
            if (card instanceof ApiCard) {
                ApiCard apiCard = (ApiCard) card;
                if (apiCard.getId().equals(globalId)) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public CardProviderUpdateResult updateAndAddCards(List<CmCard> cards) {
        List<CmCard> cardsToAdd = new ArrayList<CmCard>();
        List<CmCard> cardsToRemove = new ArrayList<CmCard>();

        // Add
        for (CardData cardData : mApiManager.getAllCardDatas()) {
            if (!cardExists(cardData.getGlobalId(), cards)) {
                CmCard card = getCardFromCardData(cardData);
                if (card != null) {
                    cardsToAdd.add(card);
                }
            }
        }

        // Update and remove
        for (CmCard card : cards) {
            if (card instanceof ApiCard) {
                ApiCard apiCard = (ApiCard) card;
                long cardId = Long.parseLong(card.getId());
                boolean cardExists = mApiManager.hasCard(apiCard.getApiAuthority(), cardId);
                if (!cardExists) {
                    cardsToRemove.add(card);
                } else {
                    CardData cardData = mApiManager.getCard(apiCard.getApiAuthority(), cardId);
                    apiCard.updateFromCardData(cardData);
                }
            }
        }

        return new CardProviderUpdateResult(cardsToAdd, cardsToRemove);
    }

    @Override
    public void updateCard(CmCard card) {
        if (card instanceof ApiCard) {
            ApiCard apiCard = (ApiCard) card;
            long cardId = apiCard.getDbId();
            CardData cardData = mApiManager.getCard(apiCard.getApiAuthority(), cardId);

            boolean stillMatches = apiCard.getMatcher().hasMatchingContent(cardData);
            if (cardData != null && stillMatches) {
                apiCard.updateFromCardData(cardData);
            } else if (cardData != null) {
                // The card no longer matches, we will remove it and add a new one.
                onCardDelete(cardData.getGlobalId());
                onCardInsertOrUpdate(cardData.getGlobalId());
            }
        }
    }

    @Override
    public CmCard createCardForId(String id) {
        CardData cardData = mApiManager.getCardWithGlobalId(id);
        if (cardData != null) {
            return getCardFromCardData(cardData);
        }
        return null;
    }

    @Override
    public List<CmCard> getCards() {
        List<CmCard> listOfCards = new ArrayList<CmCard>();
        if (mApiManager == null) {
            return listOfCards;
        }
        List<CardData> theCards = mApiManager.getAllCardDatas();

        for (CardData cardData : theCards) {
            CmCard cmCard = getCardFromCardData(cardData);
            if (cmCard != null) {
                listOfCards.add(cmCard);
            }
        }
        return listOfCards;
    }

    private CmCard getCardFromCardData(CardData cardData) {
        if (cardData != null) {
            ApiCard card = (ApiCard) DataCardBuilderFactory.getCardForCardData(mCmHomeContext,
                                                                               cardData);
            if (card != null) {
                card.updateFromCardData(cardData);
            }
            return card;
        }
        return null;
    }

    @Override
    public void addOnUpdateListener(CardProviderUpdateListener listener) {
        mUpdateListeners.add(listener);
    }

    @Override
    public void onCardInsertOrUpdate(String globalId) {
        for (CardProviderUpdateListener listener : mUpdateListeners) {
            listener.onCardProviderUpdate(globalId);
        }
    }

    @Override
    public void onCardDelete(String globalId) {
        for (CardProviderUpdateListener listener : mUpdateListeners) {
            listener.onCardDelete(globalId);
        }
    }

    public static void sendCardDeletedBroadcast(Context context, CardData deletedCardData) {
        Intent broadcast = new Intent();
        broadcast.setAction(CM_HOME_API_CARD_DELETED_BROADCAST_ACTION);
        CardDeletedInfo deletedInfo = new CardDeletedInfo(deletedCardData.getId(),
                                                          deletedCardData.getInternalId(),
                                                          deletedCardData.getGlobalId(),
                                                          deletedCardData.getAuthority());

        broadcast.putExtra(CmHomeCardChangeReceiver.CARD_DATA_DELETED_INFO_BROADCAST_EXTRA,
                           deletedInfo);

        String authority = deletedCardData.getAuthority();

        // API cards provider authorities are in the form of <package-name>.cmhomeapi
        if (authority.contains(CARD_AUTHORITY_APPEND_STRING)) {
            int cmhomeIndex = authority.indexOf(CARD_AUTHORITY_APPEND_STRING);
            String appPackageName = deletedCardData.getGlobalId().substring(0, cmhomeIndex);
            broadcast.setPackage(appPackageName);
            context.sendBroadcast(broadcast);
        }
    }
}
