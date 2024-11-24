const apiBaseUrl = "http://localhost:8080";

$(document).ready(function() {
    $("#end-turn").click(async function() {
        const updateNextPlayerResponse = await fetch(`${apiBaseUrl}/updateNextPlayer`, { method: "POST" });
        const updateNextPlayerResult = await updateNextPlayerResponse.text();
        $(this).removeClass('highlight');
        $("#end-turn").prop('disabled', true);
        $("#output").val('');

        $("#draw").addClass("highlight");
        $('#draw').prop('disabled', false);

        const currentPlayerResponse = await fetch(`${apiBaseUrl}/currentPlayer`);
        const currentPlayerResult = await currentPlayerResponse.text();

        addToOutput(`${currentPlayerResult}, click the 'Draw Event Card' button to begin!`);
        addToOutput("");
    });

    $("#draw").click(async function() {
        $("#draw").removeClass("highlight");
        $('#draw').prop('disabled', true);

        await playTurn();
    });
});

async function startGame() {
    $("#output").val('');

    addToOutput("A new game has started!");
    addToOutput("Player ID: P1, click the 'Draw Event Card' button to begin!");
    addToOutput("");

    $("#draw").addClass("highlight");
    $('#draw').prop('disabled', false);

    await fetch(`${apiBaseUrl}/start`, { method: "POST" });
    setPlayerStats();
    for(let i = 0; i < 4; i++) {
        $(`#p${i+1}IsWinner`).text("No");
    }
}

async function playTurn() {
    const drawResponse = await fetch(`${apiBaseUrl}/drawCard`);
    const drawResult = await drawResponse.json();

    setCurrentPlayer();
    setPlayerStats();
    let text = "Drawn Card: " + drawResult.cardName;

    addToOutput(text);

    if (!drawResult.isQuest) {
        const handleDrawnECardResponse = await fetch(`${apiBaseUrl}/handleDrawnECard`);
        const handleDrawnECardResult = await handleDrawnECardResponse.json();
        let playerId = handleDrawnECardResult.currentPlayer.player.index;

        if (drawResult.cardType == 'QUEENS_FAVOR') {
            await trimHand(handleDrawnECardResult.currentPlayer);
        } else if (drawResult.cardType == 'PROSPERITY') {
            for (let player of handleDrawnECardResult.players) {
                await trimHand(player);
            }
        }
    } else {

        let sponsorIndex = await getSponsor(drawResult.currentPlayer);

        const handleDrawnQuestResponse = await fetch(`${apiBaseUrl}/handleDrawnQuest?sponsorId=${sponsorIndex}`);
        const handleDrawnQuestResult = await handleDrawnQuestResponse.json();

        if(sponsorIndex != -1) {

            await buildStages(handleDrawnQuestResult.cardValue, sponsorIndex);

            for(let i = 0; i < handleDrawnQuestResult.cardValue; i++) {
                eligibleParticipants = await getAndDisplayEligibleParticipants();
                await identifyStages();
                await getPromptedEligiblePlayers(eligibleParticipants, i + 1);
                await handleParticipation();
                await identifyStages();

                eligibleParticipantsResponse = await fetch(`${apiBaseUrl}/eligibleParticipants`);
                eligibleParticipantsResult = await eligibleParticipantsResponse.json();

                await setUpAttacks(eligibleParticipantsResult.eligiblePlayers);
                await resolveAttacks(i);
                isDone = await endResolution(i);

                if(isDone) {
                    break;
                }
            }

            await endQuest(sponsorIndex);
        }
    }

    setPlayerStats();

    if(await hasWinner()) {
        await displayWinners();

        // Set winner in UI
        for(let i = 0; i < 4; i++) {
            let isWinnerResponse = await fetch(`${apiBaseUrl}/isWinner?playerId=${i+1}`);
            let isWinnerResult = await isWinnerResponse.json();

            if(isWinnerResult) {
                $(`#p${i+1}IsWinner`).text("Yes");
            }
        }

        $("#end-turn").prop('disabled', true).removeClass('highlight');
        $("#input").prop('disabled', true).removeClass('highlight');

    } else {
        addToOutput(`The turn of Player ${drawResult.currentPlayer.id} has ended`);
        addToOutput("Press the 'End Turn' button to confirm the end of your turn");

        $("#end-turn").prop('disabled', false).addClass('highlight');
    }
}

async function displayWinners() {
    let displayWinnersResponse = await fetch(`${apiBaseUrl}/displayWinners`);
    let displayWinnersResult = await displayWinnersResponse.text();

    addToOutput(displayWinnersResult);
}

async function hasWinner() {
    let hasWinnerResponse = await fetch(`${apiBaseUrl}/hasWinner`);
    let hasWinnerResult = await hasWinnerResponse.json();

    return hasWinnerResult;
}

async function endQuest(sponsorIndex) {
    // End Quest
    let endQuestResponse = await fetch(`${apiBaseUrl}/handleEndQuest`);
    let endQuestResult = await endQuestResponse.json();

    addToOutput(`The sponsor (P${sponsorIndex + 1}) has picked up cards since the quest is over.`)
    setPlayerStats();

    // Let sponsor trim if needed
    await trimHand(endQuestResult);
}

async function endResolution(stageIndex) {
    // End Resolution
    let endResolutionResponse = await fetch(`${apiBaseUrl}/handleEndResolution?stageIndex=${stageIndex}`);
    let endResolutionResult = await endResolutionResponse.json();
    addToOutput(endResolutionResult.output);

    return endResolutionResult.isDone;
}

async function resolveAttacks(stageIndex) {
    // Resolve Attacks
    let resolveAttacksResponse = await fetch(`${apiBaseUrl}/handleResolveAttacks?stageIndex=${stageIndex}`);
    let resolveAttacksResult = await resolveAttacksResponse.text();
    addToOutput(resolveAttacksResult);
}

async function setUpAttacks(eligiblePlayers) {
    // Build attack for each player
    for(let player of eligiblePlayers) {
        addToOutput(player.displayedHand);

        while(true) {
            addToOutput(player.id + ", enter the index of the card in your hand you would like to add to the attack, or type 'q' to quit building this attack");
            let choice = await waitForBuildInput(player.player.index);

            if(choice === "q") {
                break;
            }

            let buildAttackResponse = await fetch(`${apiBaseUrl}/handleBuildAttack?position=${choice}&playerId=${player.player.index}`);
            let buildAttackResult = await buildAttackResponse.json();

            if(!buildAttackResult.isAttackValid) {
                addToOutput(buildAttackResult.errorOutput);
                continue;
            }

            addToOutput(buildAttackResult.displayedAttack);

        }

        let postAttackResponse = await fetch(`${apiBaseUrl}/handlePostAttack?playerId=${player.player.index}`);
        let postAttackResult = await postAttackResponse.text();
        addToOutput(postAttackResult);

        setPlayerStats();
    }
}

async function handleParticipation() {
    // Players who participate draw a card
    const handleParticipationResponse = await fetch(`${apiBaseUrl}/handleParticipation`);
    const handleParticipationResult = await handleParticipationResponse.json();

    setPlayerStats();

    for (let player of handleParticipationResult) {
        await trimHand(player);
    }
}

async function getPromptedEligiblePlayers(eligiblePlayers, stage) {
    let eligiblePlayerIds  = [];

    for(let player of eligiblePlayers) {
        addToOutput(`${player.id}, would you like to participate in stage ${stage}? (yes/no)`);
        let choice = await waitForParticipation(player.player.index);
        if (choice !== -1) {
            eligiblePlayerIds.push(player.player.index);
        }
    }
    const updateEligiblePlayersResponse = await fetch(`${apiBaseUrl}/updateEligiblePlayers`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(eligiblePlayerIds)
    });
}

async function identifyStages() {
    let identifyStagesResponse = await fetch(`${apiBaseUrl}/identifyStages`);
    let identifyStagesResult = await identifyStagesResponse.text();
    addToOutput(identifyStagesResult);
}

async function getAndDisplayEligibleParticipants() {
    let eligibleParticipantsResponse = await fetch(`${apiBaseUrl}/eligibleParticipants`);
    let eligibleParticipantsResult = await eligibleParticipantsResponse.json();
    addToOutput(eligibleParticipantsResult.output);

    return eligibleParticipantsResult.eligiblePlayers;
}

async function buildStages(cardValue, sponsorIndex) {

    // Build stages
    for(let i = 0; i < cardValue; i++) {
        let preStageResponse = await fetch(`${apiBaseUrl}/handlePreStage`);
        let preStageResult = await preStageResponse.text();
        addToOutput(preStageResult);

        while (true) {
            addToOutput("Enter the index of the card in your hand you would like to add to the stage of the quest, or type 'q' to quit building this stage");
            let choice = await waitForBuildInput(sponsorIndex);

            let buildStageResponse = await fetch(`${apiBaseUrl}/handleBuildStage?position=${choice}`);
            let buildStageResult = await buildStageResponse.json();

            if(buildStageResult.stage.isStageEmpty) {
                addToOutput("Error: A stage cannot be empty");
                continue;
            } else if(buildStageResult.stage.isStageInsufficient) {
                addToOutput("Error: Insufficient value for this stage");
                continue;
            } else if (buildStageResult.stage.isStageValid) {
                break;
            }

            if(buildStageResult.stage.invalidType == 1) {
                addToOutput("Invalid selection: You must choose a foe card first");
                continue;
            } else if(buildStageResult.stage.invalidType == 2) {
                addToOutput("Invalid selection: You cannot have duplicate weapons in a stage");
                continue;
            } else if (buildStageResult.stage.invalidType == 3) {
                addToOutput("Invalid selection: You cannot choose 2 foe cards in a stage");
                continue;
            }

            let displayStageResponse = await fetch(`${apiBaseUrl}/displayStage?stageIndex=${i}`);
            let displayStageResult = await displayStageResponse.text();

            addToOutput(displayStageResult);
        }

        let postStageResponse = await fetch(`${apiBaseUrl}/handlePostStage`);
        let postStageResult = await postStageResponse.text();
        addToOutput(postStageResult);
        setPlayerStats();
    }
}

async function getSponsor(currentPlayerModel) {
    let sponsorIndex = -1;
    let i = currentPlayerModel.player.index;
    while(true) {
        addToOutput(`Player ID: P${i+1}, would you like to sponsor the quest? (yes/no)`);
        let choice = await waitForParticipation(i);
        if(choice === i) {
            addToOutput(`Player ID: P${i+1} is the sponsor of this quest`);
            sponsorIndex = i;
            break;
        }
        i = (i + 1) % 4;

        if(i == currentPlayerModel.player.index) {
            addToOutput("None of the players accepted the quest");
            break;
        }
    }
    return sponsorIndex;
}

async function trimHand(playerModel) {
    try {
        for (let i = 0; i < playerModel.trimCount; i++) {
            addToOutput("");
            addToOutput(`A trim is needed for ${playerModel.id}`);

            // Display the players hand
            let displayHandResponse = await fetch(`${apiBaseUrl}/displayHand?playerId=${playerModel.player.index}`);
            let displayHandResult = await displayHandResponse.text();
            addToOutput(displayHandResult);

            addToOutput("Input the card index you would like to delete:");
            await waitForTrimInput(playerModel.player.index);
        }
    } catch (error) {
        console.error("Error in setCurrentPlayer:", error);
    }
}

async function setCurrentPlayer() {
    try {
        const currentPlayerResponse = await fetch(`${apiBaseUrl}/currentPlayer`);
        const currentPlayerResult = await currentPlayerResponse.text();
        $("#currentPlayer").text(currentPlayerResult);
    } catch (error) {
        console.error("Error in setCurrentPlayer:", error);
    }
}

async function setPlayerStats() {
    try {
        for (let playerId = 1; playerId <= 4; playerId++) {
            const getShieldsResponse = await fetch(`${apiBaseUrl}/getShields?playerId=${playerId}`);
            const getShieldsResult = await getShieldsResponse.text();
            $(`#p${playerId}ShieldCount`).text(getShieldsResult);

            const getCardCountResponse = await fetch(`${apiBaseUrl}/getCardCount?playerId=${playerId}`);
            const getCardCountResult = await getCardCountResponse.text();
            $(`#p${playerId}CardCount`).text(getCardCountResult);
        }
    } catch (error) {
        console.error("Error in setPlayerStats:", error);
    }
}

function addToOutput(text, input = false) {
    const output = $("#output");

    if (input) {
        text = `--- ${text} ---`;
    }

    output.val(function (i, currentText) {
        return currentText + '\n' + text;
    });

    // Auto-scroll to the bottom
    output.scrollTop(output[0].scrollHeight);
}

async function waitForTrimInput(playerId) {
    $("#submit").addClass("highlight");
    $("#input").addClass("highlight");
    $('#input').attr('placeholder', `Player ${playerId + 1} input`);
    $('#input').focus();
    $("#submit").prop('disabled', false);

    return new Promise(resolve => {
        $("#submit").off("click").on("click", async function() {
            try {
                const userInput = $('#input').val();
                addToOutput(userInput, true);

                const trimResponse = await fetch(`${apiBaseUrl}/trim?playerId=${playerId}&cardIndex=${userInput}`);
                const trimResult = await trimResponse.text();
                addToOutput(trimResult);

                $("#submit").removeClass("highlight");
                $("#input").removeClass("highlight");
                $("#input").val("");
                $('#input').attr('placeholder', 'Player input');
                $("#submit").prop('disabled', true);

                setPlayerStats();
                resolve();
            } catch (error) {
                console.error("Error fetching trim:", error);
            }
        });
    });
}

async function waitForParticipation(playerId) {
    $("#submit").addClass("highlight");
    $("#input").addClass("highlight");
    $('#input').attr('placeholder', `Player ${playerId + 1} input`);
    $('#input').focus();
    $("#submit").prop('disabled', false);

    return new Promise(resolve => {
        $("#submit").off("click").on("click", function() {
            const userInput = $('#input').val();
            addToOutput(userInput, true);

            if (userInput === 'yes') {
                resolve(playerId);
            } else {
                resolve(-1);
            }

            $("#submit").removeClass("highlight");
            $("#input").removeClass("highlight");
            $("#input").val("");
            $('#input').attr('placeholder', 'Player input');
            $("#submit").prop('disabled', true);
        });
    });
}

async function waitForBuildInput(playerIndex) {
    $("#submit").addClass("highlight");
    $("#input").addClass("highlight");
    $('#input').attr('placeholder', `Player ${playerIndex + 1} input`);
    $('#input').focus();
    $("#submit").prop('disabled', false);

    return new Promise(resolve => {
        $("#submit").off("click").on("click", function() {
            const userInput = $('#input').val();
            addToOutput(userInput, true);

            resolve(userInput);

            $("#submit").removeClass("highlight");
            $("#input").removeClass("highlight");
            $("#input").val("");
            $('#input').attr('placeholder', 'Player input');
            $("#submit").prop('disabled', true);
        });
    });
}
