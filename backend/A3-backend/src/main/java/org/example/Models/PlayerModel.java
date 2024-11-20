package org.example.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Cards.AdventureCard;
import org.example.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerModel {

    private Player player;
    private int trimCount;
    private String id;
    private String displayedHand;
    private boolean isAttackValid;
    private String errorOutput;
    private String displayedAttack;

    public PlayerModel(Player p) {
        trimCount = 0;
        player = p;
        id = p.toString();
        displayedHand = setDisplayedHand(p);
        isAttackValid = true;
        errorOutput = "";
        displayedAttack = "";
    }

    public void setErrorOutput(String output) {
        errorOutput = output;
    }

    @JsonProperty("displayedAttack")
    public String getAttack() {
        setDisplayedAttack();
        return displayedAttack;
    }

    public void setDisplayedAttack() {
        StringBuilder output = new StringBuilder();
        output.append(player).append(", Current Attack: ");
        for(AdventureCard card : player.getAttack()) {
            output.append(card).append(" ");
        }

        displayedAttack = output.toString();
    }

    @JsonProperty("errorOutput")
    public String getErrorOutput() {
        return errorOutput;
    }

    public void setIsAttackValid(boolean status) {
        isAttackValid = status;
    }

    @JsonProperty("isAttackValid")
    public boolean isAttackValid() {
        return isAttackValid;
    }

    public void setTrimCount(int count) {
        trimCount = count;
    }

    @JsonProperty("isTrim")
    public boolean isTrimNeeded() {
        return trimCount > 0;
    }

    @JsonProperty("trimCount")
    public int getTrimCount() {
        return trimCount;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("player")
    public Player getPlayer() {
        return player;
    }

    @JsonProperty("displayedHand")
    public String getDisplayedHand() {
        return setDisplayedHand(player);
    }

    public String getDisplayedHandString() {
        return setDisplayedHand(player);
    }

    public String setDisplayedHand(Player player) {
        StringBuilder output = new StringBuilder();

        List<AdventureCard> foes = new ArrayList<>();
        List<AdventureCard> weapons = new ArrayList<>();

        // Append player's hand information
        output.append(player).append(" - HAND:\n");
        for (AdventureCard card : player.getHand()) {
            if (card.getType().isFoe()) {
                foes.add(card);
            } else if (card.getType().isWeapon()) {
                weapons.add(card);
            }
        }

        // Append foes list
        output.append("Foes: ");
        for (int i = 0; i < foes.size(); i++) {
            output.append(i).append("(").append(foes.get(i)).append(") ");
        }
        output.append("\n");

        // Append weapons list
        int start = foes.size();
        output.append("Weapons: ");
        for (int i = 0; i < weapons.size(); i++) {
            output.append((start + i)).append("(").append(weapons.get(i)).append(") ");
        }
        output.append("\n");

        // Return the built string
        return output.toString();
    }

}
