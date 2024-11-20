package org.example.Cards;

import org.example.Enums.CardType;

public class EventCard {

    private final CardType cardType;
    private final int cardValue;

    public EventCard(CardType cardType) {
        this.cardType = cardType;
        this.cardValue = cardType.getValue();
    }

    public String toString() { return cardType.getName(); }
    public CardType getType() { return cardType; }
}
