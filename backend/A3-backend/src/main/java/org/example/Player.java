package org.example;

import org.example.Cards.AdventureCard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.example.Enums.CardType;

public class Player {

    private final int id;
    private List<AdventureCard> hand;
    private int shields;
    private boolean isEligible;
    private ArrayList<AdventureCard> attack;

    public Player(int id) {
        this.id = id;
        this.hand = new ArrayList<>();
        this.shields = 0;
        this.isEligible = true;
        this.attack = new ArrayList<>();
    }

    public ArrayList<AdventureCard> getAttack() { return attack; }

    public void setEligibility(boolean eligibility) { isEligible = eligibility; }

    public boolean getEligibility() { return isEligible; }

    public int getIndex() { return id; }

    public int addCardToHand(AdventureCard card) {
        hand.add(card);
        sortHand();
        if(getHandSize() > 12) {
            return getHandSize() - 12;
        }
        return 0;
    }

    public void sortHand() {
        List<AdventureCard> foes = new ArrayList<>();
        List<AdventureCard> weapons = new ArrayList<>();

        // Separate foes and weapons
        for (AdventureCard card : hand) {
            if (card.getType().isFoe()) {
                foes.add(card);
            } else if (card.getType().isWeapon()) {
                weapons.add(card);
            }
        }

        foes.sort(Comparator.comparing(AdventureCard::getValue));
        weapons.sort(Comparator.comparing(AdventureCard::getValue));

        // Sword appears before horse
        weapons.sort((card1, card2) -> {
            if (card1.getType() == CardType.HORSE && card2.getType() == CardType.SWORD) {
                return 1;
            } else if (card1.getType() == CardType.SWORD && card2.getType() == CardType.HORSE) {
                return -1;
            }
            return 0;
        });

        // Add to hand
        hand.clear();
        hand.addAll(foes);
        hand.addAll(weapons);
    }

    public void discard(int index, Deck<AdventureCard> deck) {
        AdventureCard card = hand.remove(index);
        deck.getDiscardPile().add(card);
    }

    public String toString() {
        return "Player ID: P" + (id + 1);
    }

    public void addShields(int shields) { this.shields += shields; }
    public int getShields() { return shields; }
    public int getHandSize() { return hand.size(); }
    public List<AdventureCard> getHand() { return hand; }

}
