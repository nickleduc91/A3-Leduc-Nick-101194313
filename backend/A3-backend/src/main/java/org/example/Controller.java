package org.example;

import org.example.Cards.AdventureCard;
import org.example.Models.EligibleModel;
import org.example.Models.EndResolutionModel;
import org.example.Models.PlayerModel;
import org.example.Models.ReturnModel;
import org.example.Cards.EventCard;
import org.example.Enums.CardType;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:8081")
public class Controller {
    private Game game;
    public EventCard drawnCard;

    public Controller(Game game) {
        this.game = game;

        game.initializeDecks();
        game.initializePlayers();
    }

    public Controller() {
        this.game = new Game();

        game.initializeDecks();
        game.initializePlayers();
    }

    @PostMapping("/start")
    public void startGame() {
        this.game = new Game();

        game.initializeDecks();
        game.initializePlayers();

    }

    @GetMapping("/drawCard")
    public ReturnModel drawCard() {
        drawnCard = game.drawEventCard();

        return new ReturnModel(drawnCard.getType(), game);
    }

    @GetMapping("/currentPlayer")
    public String getCurrentPlayer() {
        return game.getCurrentPlayer().toString();
    }

    @GetMapping("/displayHand")
    public String getCurrentPlayer(@RequestParam int playerId) {
        PlayerModel model = new PlayerModel(game.getPlayer(playerId));
        return model.getDisplayedHandString();
    }

    @GetMapping("/eligibleParticipants")
    public EligibleModel getEligiblePlayers() {
        StringBuilder output = new StringBuilder();

        game.getEligibleParticipants().removeIf(p -> p.getIndex() == game.getSponsorIndex() || !p.getEligibility());
        EligibleModel model = new EligibleModel(game);

        if(game.getEligibleParticipants().isEmpty()) {
            output.append("There are NO eligible participants for the quest");
        } else {
            output.append("Eligible participants: ").append("\n");
            for(Player p : game.getEligibleParticipants()) {
                output.append(p.toString()).append("\n");
            }
        }

        model.setOutput(output.toString());
        return model;
    }

    @GetMapping("/handleParticipation")
    public ArrayList<PlayerModel> handleParticipation() {
        ArrayList<PlayerModel> models = new ArrayList<>();

        for(Player p : game.getEligibleParticipants()) {
            PlayerModel model = new PlayerModel(p);
            int trim = p.addCardToHand(game.drawAdventureCard());
            model.setTrimCount(trim);

            models.add(model);
        }
        return models;
    }

    @GetMapping("/identifyStages")
    public String identifyStages() {
        StringBuilder output = new StringBuilder();

        for(int i = 0; i < game.getQuest().size(); i++) {
            output.append("Stage ").append(i + 1).append(": ");
            for(AdventureCard _ : game.getQuest().get(i)) {
                output.append("X");
            }
            output.append("\n");
        }
        return output.toString();
    }

    @PostMapping("/updateNextPlayer")
    public void updateNextPlayer() {
        game.updateNextPlayer();
    }

    @PostMapping("/updateEligiblePlayers")
    public void updateEligiblePlayers(@RequestBody List<Integer> playerIds) {
        ArrayList<Player> eligiblePlayers = new ArrayList<>();

        for (Player p : game.getEligibleParticipants()) {
            if (playerIds.contains(p.getIndex())) {
                eligiblePlayers.add(p);
            } else {
                p.setEligibility(false);
            }
        }

        game.setEligibleParticipants(eligiblePlayers);
    }

    @GetMapping("/displayStage")
    public String displayCurrentStage(@RequestParam int stageIndex) {

        StringBuilder output = new StringBuilder();

        output.append("Current Stage: ");
        for(AdventureCard card : game.getQuest().get(stageIndex)) {
            output.append(card).append(" ");
        }
        output.append("\n");
        return output.toString();
    }

    @GetMapping("/getShields")
    public int getShields(@RequestParam int playerId) {
        Player player = game.getPlayer(playerId - 1);

        return player.getShields();
    }

    @GetMapping("/getCardCount")
    public int getCardCount(@RequestParam int playerId) {
        Player player = game.getPlayer(playerId - 1);

        return player.getHandSize();
    }

    @GetMapping("/handleDrawnECard")
    public ReturnModel handleCard() {
        ReturnModel model = new ReturnModel(drawnCard.getType(), game);

        handleDrawnECard(drawnCard, model);

        return model;
    }

    @GetMapping("/handleDrawnQuest")
    public ReturnModel handleQuest(@RequestParam int sponsorId) {
        ReturnModel model = new ReturnModel(drawnCard.getType(), game);

        model.setSponsorIndex(sponsorId);
        handleDrawnQuest(model);

        return model;
    }

    @GetMapping("/handleBuildStage")
    public ReturnModel buildStage(@RequestParam String position) {
        ReturnModel model = new ReturnModel(drawnCard.getType(), game);

        int choice;
        if(position.equals("q")) {
            choice = -1;
        } else {
            choice = Integer.parseInt(position);
        }

        handleBuildStage(model, choice);

        return model;
    }

    @GetMapping("/handleBuildAttack")
    public PlayerModel buildAttack(@RequestParam int position, @RequestParam int playerId) {

        Player p = game.getPlayer(playerId);
        PlayerModel model = new PlayerModel(p);
        boolean flag = true;

        AdventureCard card = p.getHand().get(position);

        //Check if a foe was selected
        if(card.getType().isFoe()) {
            model.setErrorOutput("Invalid selection: You cannot select a foe in an attack");
            model.setIsAttackValid(false);
            flag = false;
        }

        // Check for duplicates
        if(!p.getAttack().isEmpty()) {
            boolean duplicate = false;
            for (AdventureCard attackCard : p.getAttack()) {
                if (attackCard.getType().getName().equals(card.getType().getName())) {
                    duplicate = true;
                    break;
                }
            }
            if(duplicate) {
                model.setErrorOutput("Invalid selection: You cannot have duplicate weapons in an attack");
                model.setIsAttackValid(false);
                flag = false;
            }
        }

        if(flag) {
            p.getAttack().add(card);
            model.setDisplayedAttack();
        }

        return model;

    }

    @GetMapping("/handlePostAttack")
    public String postAttack(@RequestParam int playerId) {
        StringBuilder output = new StringBuilder();
        Player p = game.getPlayer(playerId);

        output.append("\n");
        if(p.getAttack().isEmpty()) {
            output.append("Attack for ").append(p).append(": No cards were selected");
        } else {
            output.append("Attack for ").append(p).append(": ");
            for(AdventureCard card : p.getAttack()) {
                output.append(card).append(" ");
                p.getHand().remove(card);
            }
        }

        output.append("\n");

        return output.toString();
    }

    @GetMapping("/handleResolveAttacks")
    public String resolveAttacks(@RequestParam int stageIndex) {
        StringBuilder output = new StringBuilder();

        int stageAttackValue = game.getAttackValue(stageIndex);

        output.append("-- Resolving attacks --\n");
        // Display the Stage's attack
        output.append("Attack for Stage ").append(stageIndex + 1).append(": ");
        for(AdventureCard card : game.getQuest().get(stageIndex)) {
            output.append(card).append(" ");
        }
        output.append("\n\n");

        for(Player p : game.getEligibleParticipants()) {
            int attackValue = game.getAttackValue(p);

            // Display the players attack
            output.append("Attack for ").append(p).append(": ");
            for(AdventureCard card : p.getAttack()) {
                output.append(card).append(" ");
            }
            output.append("\n");

            if(attackValue < stageAttackValue) {
                p.setEligibility(false);
                output.append(p).append(" is now ineligible since their attack value is less than the stage value");
            } else {
                output.append(p).append(" is eligible to continue");
            }
            output.append("\n");
        }

        return output.toString();
    }

    @GetMapping("/handleEndResolution")
    public EndResolutionModel endResolution(@RequestParam int stageIndex) {
        StringBuilder output = new StringBuilder();
        EndResolutionModel model = new EndResolutionModel();

        // Discard all cards used to attack
        for(Player p : game.getEligibleParticipants()) {
            ArrayList<AdventureCard> attackHand = p.getAttack();
            ArrayList<AdventureCard> cardsToDiscard = new ArrayList<>(attackHand);

            // Remove each card from the player's attack hand and add it to the discard pile
            for(AdventureCard card : cardsToDiscard) {
                game.getAdventureDeck().addToDiscardPile(card);
            }
            attackHand.clear();
        }

        game.getEligibleParticipants().removeIf(p -> !p.getEligibility());

        if(game.getEligibleParticipants().isEmpty()) {
            model.setOutput("The quest is now finished since nobody is eligible to continue");
            model.setIsDone(true);
            return model;
        }

        // Last stage just happened and there are still participants who are eligible
        if(stageIndex == game.getQuest().size() - 1) {
            output.append("Below are the winner(s) of the quest, each have gained ").append(stageIndex + 1).append(" shields:\n");
            for(Player p : game.getEligibleParticipants()) {
                output.append(p.toString()).append("\n");
                p.addShields(stageIndex + 1);
            }
            model.setOutput(output.toString());
            model.setIsDone(true);
            return model;
        }

        model.setOutput("Resolution for stage " + (stageIndex + 1) + " is done.\n");
        return model;

    }

    @GetMapping("/handleEndQuest")
    public PlayerModel endQuest() {
        PlayerModel model = new PlayerModel(game.getPlayer(game.getSponsorIndex()));

        int cardCount = 0;
        int numStages = game.getQuest().size();

        // Remove all cards from the quest and discard them
        for (ArrayList<AdventureCard> stage : game.getQuest()) {
            ArrayList<AdventureCard> cardsToDiscard = new ArrayList<>(stage);

            // Add each card to the discard pile
            for (AdventureCard card : cardsToDiscard) {
                cardCount += 1;
                game.getAdventureDeck().addToDiscardPile(card);
            }

            stage.clear();
        }

        // Draw cards and trim if needed
        Player sponsor = game.getPlayer(game.getSponsorIndex());

        int trim = 0;
        for(int i = 0; i < (cardCount + numStages); i++) {
            trim = sponsor.addCardToHand(game.drawAdventureCard());
        }
        model.setTrimCount(trim);

        // Discard the q card
        game.getEventDeck().addToDiscardPile(drawnCard);

        // Reset all quest related variables
        game.getQuest().clear();
        game.getEligibleParticipants().clear();
        game.setCurrentStageIndex(0);

        for(int i = 0; i < 4; i++) {
            Player p = game.getPlayer(i);
            p.setEligibility(true);

            game.getEligibleParticipants().add(p);
        }

        return model;
    }

    @GetMapping("/handlePostStage")
    public String postStage() {
        StringBuilder output = new StringBuilder();

        output.append("\n");
        output.append("Cards for Stage ").append(game.getCurrentStageIndex() + 1).append(": ");

        for(AdventureCard card : game.getQuest().get(game.getCurrentStageIndex())) {
            output.append(card).append(" ");
            game.getPlayer(game.getSponsorIndex()).getHand().remove(card);
        }
        output.append("\n");
        game.setCurrentStageIndex(game.getCurrentStageIndex() + 1);

        return output.toString();
    }

    @GetMapping("/handlePreStage")
    public String preStage() {
        game.getQuest().add(new ArrayList<>());
        PlayerModel model = new PlayerModel(game.getPlayer(game.getSponsorIndex()));
        return model.getDisplayedHandString();
    }

    @GetMapping("/trim")
    public String trimHand(@RequestParam int cardIndex, @RequestParam int playerId) {
        Player player = game.getPlayer(playerId);
        player.discard(cardIndex, game.getAdventureDeck());
        return "Discarded card at index " + cardIndex;
    }

    @GetMapping("/hasWinner")
    public Boolean hasWinner() {
        return game.hasWinner();
    }

    @GetMapping("/displayWinners")
    public String displayWinners() {
        StringBuilder output = new StringBuilder();

        output.append("Winning Players:\n");
        for (Player player : game.getWinners()) {
            output.append(player).append("\n");
        }

        return output.toString();
    }

    public void handleBuildStage(ReturnModel model, int position) {

        int currentStageIndex = game.getCurrentStageIndex();
        Player sponsor = game.getPlayer(model.getSponsorIndex());
        boolean flag = true;

        if (position == -1) {
            if (game.isStageEmpty(currentStageIndex)) {
                model.getStage().setStageEmpty(true);
                flag = false;
            } else if (game.isStageInsufficient(currentStageIndex)) {
                model.getStage().setStageInsufficient(true);
                flag = false;
            } else {
                model.getStage().setStageValid(true);
                flag = false;
            }
        }

        if(flag) {
            AdventureCard card = sponsor.getHand().get(position);
            int option = game.isStageSelectionValid(card);

            model.getStage().setInvalidType(option);

            if(option == 0) {
                game.getQuest().get(currentStageIndex).add(card);
                model.getStage().addCard(card);
            }

        }
    }

    public void handleDrawnQuest(ReturnModel model) {
        if(model.getSponsorIndex() == -1) {
            game.getEventDeck().addToDiscardPile(drawnCard);
        } else {
            game.setSponsorIndex(model.getSponsorIndex());
        }
    }

    public void handleDrawnECard(EventCard card, ReturnModel model) {
        if (card.getType() == CardType.PLAGUE) {
            int shields = game.getCurrentPlayer().getShields();
            game.getCurrentPlayer().addShields(shields < 2 ? -shields : -2);
        } else if (card.getType() == CardType.QUEENS_FAVOR) {
            Player p = game.getPlayer(game.getCurrentPlayer().getIndex());
            p.addCardToHand(game.drawAdventureCard());
            int trim = p.addCardToHand(game.drawAdventureCard());
            model.getCurrentPlayer().setTrimCount(trim);
        } else if (card.getType() == CardType.PROSPERITY) {
            for(int i = 0; i < 4; i++) {
                Player p = game.getPlayer(i);
                p.addCardToHand(game.drawAdventureCard());
                int trim = p.addCardToHand(game.drawAdventureCard());
                model.getPlayers().get(i).setTrimCount(trim);
            }
        }
        game.getEventDeck().addToDiscardPile(card);
    }

}
