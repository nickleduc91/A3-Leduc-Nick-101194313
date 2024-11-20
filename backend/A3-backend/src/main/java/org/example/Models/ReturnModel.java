package org.example.Models;

import org.example.Enums.CardType;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Game;

import java.util.ArrayList;

public class ReturnModel {

    private boolean isQuest;
    private String cardName;
    private CardType cardType;
    private int cardValue;
    private ArrayList<PlayerModel> players;
    private PlayerModel currentPlayer;
    private int sponsorIndex;
    private StageModel stage;

    public ReturnModel(CardType type, Game game) {
        isQuest = type.isQuest();
        cardName = type.getName();
        cardType = type;
        players = new ArrayList<>();

        for(int i = 0; i < 4; i++) {
            players.add(new PlayerModel(game.getPlayer(i)));
        }
        currentPlayer = new PlayerModel(game.getCurrentPlayer());
        sponsorIndex = game.getSponsorIndex();
        cardValue = cardType.getValue();
        stage = new StageModel();
    }

    public void setSponsorIndex(int index) {
        sponsorIndex = index;
    }

    @JsonProperty("isQuest")
    public boolean isCardQuest() {
        return isQuest;
    }

    @JsonProperty("stage")
    public StageModel getStage() {
        return stage;
    }

    @JsonProperty("currentPlayer")
    public PlayerModel getCurrentPlayer() {
        return currentPlayer;
    }

    @JsonProperty("cardName")
    public String getName() {
        return cardName;
    }

    @JsonProperty("cardValue")
    public int getValue() {
        return cardValue;
    }

    @JsonProperty("sponsorIndex")
    public int getSponsorIndex() {
        return sponsorIndex;
    }

    @JsonProperty("cardType")
    public CardType getType() {
        return cardType;
    }

    @JsonProperty("players")
    public ArrayList<PlayerModel> getPlayers() {
        return players;
    }
}
