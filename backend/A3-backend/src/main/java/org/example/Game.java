package org.example;

import org.example.Cards.AdventureCard;
import org.example.Cards.EventCard;
import org.example.Enums.CardType;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private Deck<EventCard> eventDeck;
    private Deck<AdventureCard> adventureDeck;
    private final List<Player> players;
    private int currentPlayerIndex;
    private ArrayList<ArrayList<AdventureCard>> quest;
    private int currentStageIndex;
    private ArrayList<Player> eligibleParticipants;
    private int sponsorIndex;

    public Game() {
        eventDeck = new Deck<>();
        adventureDeck = new Deck<>();
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.quest = new ArrayList<>();
        this.currentStageIndex = 0;
        this.eligibleParticipants = new ArrayList<>();
        this.sponsorIndex = 0;
    }

    public void setSponsorIndex(int index) { this.sponsorIndex = index; }

    public int getSponsorIndex() { return sponsorIndex; }

    public int getAttackValue(int stageIndex) {
        int value = 0;
        ArrayList<AdventureCard> stage = quest.get(stageIndex);
        for(AdventureCard card : stage) {
            value += card.getValue();
        }
        return value;
    }

    public int getAttackValue(Player p) {
        int value = 0;
        for(AdventureCard card : p.getAttack()) {
            value += card.getValue();
        }
        return value;
    }

    public boolean isQuestDone() {
        return getEligibleParticipants().isEmpty();
    }

    public void setEligibleParticipants(ArrayList<Player> eligiblePlayers) {
        this.eligibleParticipants = eligiblePlayers;
    }

    public ArrayList<Player> getEligibleParticipants() { return eligibleParticipants; }

    public int isStageSelectionValid(AdventureCard card) {

        ArrayList<AdventureCard> stage = quest.get(currentStageIndex);

        // Return status code 1 if the sponsor tries to choose a Weapon card before a foe
        if (stage.isEmpty() && !card.getType().isFoe()) {
            return 1;
        }

        // Return status code 2 if the sponsor tries to select the same type of weapon
        if (!stage.isEmpty() && card.getType().isWeapon()) {
            boolean duplicate = false;
            for (AdventureCard stageCard : stage) {
                if (stageCard.getType().getName().equals(card.getType().getName())) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) {
                return 2;
            }
        }

        // Return status code 3 if teh sponsor tries to select more tahn one foe
        if(!stage.isEmpty() && card.getType().isFoe()) {
            boolean duplicate = false;
            for (AdventureCard stageCard : stage) {
                if (stageCard.getType().isFoe()) {
                    duplicate = true;
                    break;
                }
            }
            if(duplicate) {
                return 3;
            }
        }
        return 0;
    }

    public boolean isStageInsufficient(int currentStageIndex) {
        int currentValue = 0;

        if(quest.size() <= 1) {
            return false;
        }

        ArrayList<AdventureCard> stage = quest.get(currentStageIndex);
        for(AdventureCard card : stage) {
            currentValue += card.getValue();
        }

        int precedingValue = 0;
        ArrayList<AdventureCard> prevStage = quest.get(currentStageIndex - 1);
        for(AdventureCard card : prevStage) {
            precedingValue += card.getValue();
        }

        return (currentValue <= precedingValue);

    }

    public void setCurrentStageIndex(int index) { currentStageIndex = index; }

    public int getCurrentStageIndex() { return currentStageIndex; }

    public boolean isStageEmpty(int currentStage) { return quest.get(currentStage).isEmpty(); }

    public void initializeDecks() {
        int[] expectedFoeCounts = {8, 7, 8, 7, 7, 4, 4, 2, 2, 1};
        CardType[] foeCards = {CardType.F5, CardType.F10, CardType.F15, CardType.F20, CardType.F25, CardType.F30, CardType.F35, CardType.F40, CardType.F50, CardType.F70};

        int[] expectedWeaponCounts = {6, 12, 16, 8, 6, 2};
        CardType[] weaponCards = {CardType.DAGGER, CardType.HORSE, CardType.SWORD, CardType.BATTLE_AXE, CardType.LANCE, CardType.EXCALIBUR};

        for(int i = 0; i < foeCards.length; i++) {
            for(int j = 0; j < expectedFoeCounts[i]; j++) {
                AdventureCard card = new AdventureCard(foeCards[i]);
                adventureDeck.addCard(card);
            }
        }
        for(int i = 0; i < weaponCards.length; i++) {
            for(int j = 0; j < expectedWeaponCounts[i]; j++) {
                AdventureCard card = new AdventureCard(weaponCards[i]);
                adventureDeck.addCard(card);
            }
        }

        int[] expectedQuestCounts = {3, 4, 3, 2};
        CardType[] questCards = {CardType.Q2, CardType.Q3, CardType.Q4, CardType.Q5};

        int[] expectedEventCounts = {1, 2, 2};
        CardType[] eventCards = {CardType.PLAGUE, CardType.QUEENS_FAVOR, CardType.PROSPERITY};

        for(int i = 0; i < questCards.length; i++) {
            for(int j = 0; j < expectedQuestCounts[i]; j++) {
                EventCard card = new EventCard(questCards[i]);
                eventDeck.addCard(card);
            }
        }
        for(int i = 0; i < eventCards.length; i++) {
            for(int j = 0; j < expectedEventCounts[i]; j++) {
                EventCard card = new EventCard(eventCards[i]);
                eventDeck.addCard(card);
            }
        }
        shuffleAdventureDeck();
        shuffleEventDeck();
    }

    public void initializePlayers() {
        for(int i = 0; i < 4; i++) {
            Player player = new Player(i);
            players.add(player);
            eligibleParticipants.add(player);

            // Deal 12 adventure cards
            for (int j = 0; j < 12; j++) {
                player.addCardToHand(drawAdventureCard());
            }
        }
    }

    public void updateNextPlayer() {
        if(currentPlayerIndex == 3) {
            currentPlayerIndex = 0;
        } else {
            currentPlayerIndex += 1;
        }
    }

    public boolean hasWinner() {
        for (Player player : players) {
            if (player.getShields() >= 7) {
                return true;
            }
        }
        return false;
    }

    public List<Player> getWinners() {
        List<Player> winners = new ArrayList<>();
        for (Player player : players) {
            if (player.getShields() >= 7) {
                winners.add(player);
            }
        }
        return winners;
    }

    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }
    public Player getPlayer(int id) { return players.get(id); }

    public AdventureCard drawAdventureCard() {
        return adventureDeck.drawCard();
    }
    public EventCard drawEventCard() {
        return eventDeck.drawCard();
    }

    public Deck<EventCard> getEventDeck() { return eventDeck; }
    public Deck<AdventureCard> getAdventureDeck() { return adventureDeck; }

    public void shuffleAdventureDeck() { adventureDeck.shuffleCards(); }
    public void shuffleEventDeck() { eventDeck.shuffleCards(); }

    public int getEventDeckSize() { return eventDeck.getSize(); }
    public int getAdventureDeckSize() { return adventureDeck.getSize(); }

    public ArrayList<ArrayList<AdventureCard>> getQuest() { return quest; }
}
