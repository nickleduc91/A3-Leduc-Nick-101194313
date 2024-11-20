package org.example.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.Game;
import org.example.Player;

import java.util.ArrayList;

public class EligibleModel {
    private ArrayList<PlayerModel> eligiblePlayers;
    private String output;

    public EligibleModel(Game game) {
        eligiblePlayers = new ArrayList<>();
        for(Player p : game.getEligibleParticipants()) {
            eligiblePlayers.add(new PlayerModel(p));
        }
        output = "";
    }

    @JsonProperty("eligiblePlayers")
    public ArrayList<PlayerModel> getEligiblePlayers() {
        return  eligiblePlayers;
    }

    @JsonProperty("output")
    public String getOutput() {
        return  output;
    }

    public void setOutput(String text) {
        output = text;
    }
}
