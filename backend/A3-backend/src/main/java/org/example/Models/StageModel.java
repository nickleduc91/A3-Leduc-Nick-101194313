package org.example.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Cards.AdventureCard;

import java.util.ArrayList;

public class StageModel {
    private boolean isStageEmpty;
    private boolean isStageInsufficient;
    private boolean isStageValid;
    private int invalidType;
    private ArrayList<AdventureCard> cards;

    public StageModel() {
        isStageEmpty = false;
        isStageInsufficient = false;
        isStageValid = false;
        invalidType = 0;
        cards = new ArrayList<>();
    }

    @JsonProperty("cards")
    public ArrayList<AdventureCard> getCards() {
        return cards;
    }

    public void addCard(AdventureCard card) {
       cards.add(card);
    }

    @JsonProperty("invalidType")
    public int getInvalidType() {
        return invalidType;
    }

    public void setInvalidType(int type) {
        invalidType = type;
    }

    @JsonProperty("isStageEmpty")
    public boolean isStageEmpty() {
        return isStageEmpty;
    }

    public void setStageEmpty(boolean status) {
        isStageEmpty = status;
    }

    @JsonProperty("isStageInsufficient")
    public boolean isStageInsufficient() {
        return isStageInsufficient;
    }

    public void setStageInsufficient(boolean status) {
        isStageInsufficient = status;
    }

    @JsonProperty("isStageValid")
    public boolean isStageValid() {
        return isStageValid;
    }

    public void setStageValid(boolean status) {
        isStageValid = status;
    }
}
