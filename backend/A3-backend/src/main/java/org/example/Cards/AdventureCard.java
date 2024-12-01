package org.example.Cards;
import org.example.Enums.CardType;

public class AdventureCard {

    private final CardType cardType;
    private final int cardValue;
    private final String name;
    private final Boolean isFoe;

    public AdventureCard(CardType cardType) {
        this.cardType = cardType;
        this.cardValue = cardType.getValue();
        this.name = cardType.getName();
        this.isFoe = cardType.isFoe();
    }

    public int getValue() { return cardValue; }
    public CardType getType() { return cardType; }
    public String getName() { return name; }
    public Boolean getIsFoe() { return isFoe; }
    public String toString() { return cardType.getName(); }

}
