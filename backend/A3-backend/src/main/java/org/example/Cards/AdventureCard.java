package org.example.Cards;
import org.example.Enums.CardType;

public class AdventureCard {

    private final CardType cardType;
    private final int cardValue;

    public AdventureCard(CardType cardType) {
        this.cardType = cardType;
        this.cardValue = cardType.getValue();
    }

    public int getValue() { return cardValue; }
    public CardType getType() { return cardType; }
    public String toString() { return cardType.getName(); }

}
