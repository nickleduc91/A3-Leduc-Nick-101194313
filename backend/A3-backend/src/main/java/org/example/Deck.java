package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck <T> {

    private List<T> cards;
    private List<T> discardPile;

    public Deck() {
        cards = new ArrayList<>();
        discardPile = new ArrayList<>();
    }

    public void addCard(T card) {
        cards.add(card);
    }

    public T drawCard() {
        if (!cards.isEmpty()) {
            return cards.removeFirst();
        }
        cards.addAll(discardPile);
        shuffleCards();
        discardPile.clear();
        return cards.removeFirst();
    }

    public List<T> getDiscardPile() { return discardPile; }

    public int getDiscardPileSize() { return discardPile.size(); }

    public void addToDiscardPile(T card) { discardPile.add(card); }

    public void setDeck(List<T> newCards) { cards = newCards; }

    public int getSize() { return cards.size(); }

    public void shuffleCards() { Collections.shuffle(cards); };

    public List<T> getCards() { return cards; }
}
