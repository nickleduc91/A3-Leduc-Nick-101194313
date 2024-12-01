import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.Enums.CardType;
import org.example.Game;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeleniumTest {

    private WebDriver driver;
    private Game game;

    @BeforeEach
    void setup() {

        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/chromedriver.exe");

        // Initialize the WebDriver instance
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://127.0.0.1:8081");

        // Start game
        WebElement startButton = driver.findElement(By.id("start"));
        startButton.click();
    }

    @Test
    @DisplayName("A1_scenario")
    void A1_scenario() {
        RestAssured.baseURI = "http://localhost:8080";

        // Rig event deck
        rigDeck("/rigEventDeck", List.of(CardType.Q4));

        // Rig adventure deck
        rigDeck("/rigAdventureDeck", List.of(
            CardType.F30, CardType.SWORD, CardType.BATTLE_AXE, CardType.F10,
            CardType.LANCE, CardType.LANCE, CardType.BATTLE_AXE, CardType.SWORD,
            CardType.F30, CardType.LANCE
        ));

        // Rig player decks
        rigPlayerDeck(1, List.of(
            CardType.F5, CardType.F5, CardType.F15, CardType.F15, CardType.DAGGER,
            CardType.SWORD, CardType.SWORD, CardType.HORSE, CardType.HORSE,
            CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.LANCE
        ));

        rigPlayerDeck(2, List.of(
            CardType.F5, CardType.F5, CardType.F15, CardType.F15, CardType.F40,
            CardType.DAGGER, CardType.SWORD, CardType.HORSE, CardType.HORSE,
            CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.EXCALIBUR
        ));

        rigPlayerDeck(3, List.of(
            CardType.F5, CardType.F5, CardType.F5, CardType.F15, CardType.DAGGER,
            CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.HORSE,
            CardType.HORSE, CardType.BATTLE_AXE, CardType.LANCE
        ));

        rigPlayerDeck(4, List.of(
            CardType.F5, CardType.F15, CardType.F15, CardType.F40, CardType.DAGGER,
            CardType.DAGGER, CardType.SWORD, CardType.HORSE, CardType.HORSE,
            CardType.BATTLE_AXE, CardType.LANCE, CardType.EXCALIBUR
        ));

        clickDrawButton();

        // Player 2 becomes sponsor
        enterInputAndSubmit("no");
        enterInputAndSubmit("yes");

        // Sponsor builds stage 1 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("7");
        enterInputAndSubmit("q");

        // Sponsor builds stage 2 of quest
        enterInputAndSubmit("1");
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Sponsor builds stage 3 of quest
        enterInputAndSubmit("1");
        enterInputAndSubmit("3");
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Sponsor builds stage 4 of quest
        enterInputAndSubmit("1");
        enterInputAndSubmit("3");
        enterInputAndSubmit("q");

        //Player 2 - Hand
        assertHandSize(2, 3);

        // Stage 1

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // All players discard after participation
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Player 1 builds attack
        enterInputAndSubmit("4");
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("4");
        enterInputAndSubmit("3");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("3");
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        //Player 1 - Hand
        assertHandSize(1, 10);
        //Player 3 - Hand
        assertHandSize(3, 10);
        //Player 4 - Hand
        assertHandSize(4, 10);

        // Stage 2

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 1 builds attack
        enterInputAndSubmit("6");
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("8");
        enterInputAndSubmit("3");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("5");
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        //Player 1 - Hand
        assertHandSize(1, 9);
        //Player 3 - Hand
        assertHandSize(3, 9);
        //Player 4 - Hand
        assertHandSize(4, 9);

        // Stage 3

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 3 builds attack
        enterInputAndSubmit("8");
        enterInputAndSubmit("3");
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("6");
        enterInputAndSubmit("4");
        enterInputAndSubmit("8");
        enterInputAndSubmit("q");

        //Player 3 - Hand
        assertHandSize(3, 7);
        //Player 4 - Hand
        assertHandSize(4, 7);

        // Stage 4

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 3 builds attack
        enterInputAndSubmit("6");
        enterInputAndSubmit("5");
        enterInputAndSubmit("7");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("3");
        enterInputAndSubmit("4");
        enterInputAndSubmit("5");
        enterInputAndSubmit("7");
        enterInputAndSubmit("q");

        //Player 3 - Hand
        assertHandSize(3, 5);
        //Player 4 - Hand
        assertHandSize(4, 4);

        // End of quest, sponsor discards
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        //Player 2 - Hand
        assertHandSize(2, 12);

        // Final Assertions

        // Player 1 - Shield/Hand count and Hand
        assertShieldCount(1, 0);
        assertHandSize(1, 9);
        assertHand(1, List.of(CardType.F5, CardType.F10, CardType.F15, CardType.F15, CardType.F30, CardType.HORSE, CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.LANCE));

        // Player 2 - Shield/Hand count
        assertShieldCount(2, 0);
        assertHandSize(2, 12);

        // Player 3 - Shield/Hand count and Hand
        assertShieldCount(3, 0);
        assertHandSize(3, 5);
        assertHand(3, List.of(CardType.F5, CardType.F5, CardType.F15, CardType.F30, CardType.SWORD));

        // Player 4 - Shield/Hand count and Hand
        assertShieldCount(4, 4);
        assertHandSize(4, 4);
        assertHand(4, List.of(CardType.F15, CardType.F15, CardType.F40, CardType.LANCE));

    }

    @Test()
    @DisplayName("2winner_game_2winner_quest")
    void two_winner_game_two_winner_quest() {
        RestAssured.baseURI = "http://localhost:8080";

        // Rig event deck
        rigDeck("/rigEventDeck", List.of(CardType.Q4, CardType.Q3));

        // Rig adventure deck
        rigDeck("/rigAdventureDeck", List.of(
            CardType.F5, CardType.F40, CardType.F10, CardType.F10,
            CardType.F30, CardType.F30, CardType.F15, CardType.F15,
            CardType.F20, CardType.F5, CardType.F10, CardType.F15,
            CardType.F15, CardType.F20, CardType.F20, CardType.F20,
            CardType.F20, CardType.F25, CardType.F25, CardType.F30,
            CardType.DAGGER, CardType.DAGGER, CardType.F15, CardType.F15,
            CardType.F25, CardType.F25, CardType.F20, CardType.F20,
            CardType.F25, CardType.F30, CardType.SWORD, CardType.BATTLE_AXE,
            CardType.BATTLE_AXE, CardType.LANCE
        ));

        // Rig player decks
        rigPlayerDeck(1, List.of(
            CardType.F5, CardType.F5, CardType.F10, CardType.F10,
            CardType.F15, CardType.F15, CardType.DAGGER, CardType.HORSE,
            CardType.HORSE, CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.LANCE
        ));

        rigPlayerDeck(2, List.of(
            CardType.F40, CardType.F50, CardType.HORSE, CardType.HORSE,
            CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.BATTLE_AXE,
            CardType.BATTLE_AXE, CardType.LANCE, CardType.LANCE, CardType.EXCALIBUR
        ));

        rigPlayerDeck(3, List.of(
            CardType.F5, CardType.F5, CardType.F5, CardType.F5,
            CardType.DAGGER, CardType.DAGGER, CardType.DAGGER, CardType.HORSE,
            CardType.HORSE, CardType.HORSE, CardType.HORSE, CardType.HORSE
        ));

        rigPlayerDeck(4, List.of(
            CardType.F50, CardType.F70, CardType.HORSE, CardType.HORSE,
            CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.BATTLE_AXE,
            CardType.BATTLE_AXE, CardType.LANCE, CardType.LANCE, CardType.EXCALIBUR
        ));

        clickDrawButton();

        // Player 1 becomes sponsor
        enterInputAndSubmit("yes");

        // Sponsor builds stage 1 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("q");

        // Sponsor builds stage 2 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Sponsor builds stage 3 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("4");
        enterInputAndSubmit("q");

        // Sponsor builds stage 4 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("4");
        enterInputAndSubmit("q");

        // Player 1 - Hand
        assertHandSize(1, 5);

        // Stage 1

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // All players discard after participation
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Player 2 - Hand
        assertHandSize(2, 12);
        // Player 3 - Hand
        assertHandSize(3, 12);
        // Player 4 - Hand
        assertHandSize(4, 12);

        // Player 2 builds attack
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 11);
        // Player 3 - Hand
        assertHandSize(3, 12);
        // Player 4 - Hand
        assertHandSize(4, 11);

        // Stage 2

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 2 builds attack
        enterInputAndSubmit("3");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("3");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 11);
        // Player 4 - Hand
        assertHandSize(4, 11);

        // Stage 3

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 2 builds attack
        enterInputAndSubmit("4");
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("4");
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 10);
        // Player 4 - Hand
        assertHandSize(4, 10);

        // Stage 4

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 2 builds attack
        enterInputAndSubmit("5");
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("5");
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 9);
        // Player 4 - Hand
        assertHandSize(4, 9);

        // End of quest, sponsor discards
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Player 1 - Hand
        assertHandSize(1, 12);

        // Player 1 - Shield/Hand count
        assertShieldCount(1, 0);
        assertHandSize(1, 12);
        // Player 2 - Shield/Hand count
        assertShieldCount(2, 4);
        assertHandSize(2, 9);
        // Player 3 - Shield/Hand count
        assertShieldCount(3, 0);
        assertHandSize(3, 12);
        // Player 4 - Shield/Hand count
        assertShieldCount(4, 4);
        assertHandSize(4, 9);

        // Player 1 turn is done, now is Player 2's turn
        clickEndTurnButton();
        clickDrawButton();

        // Player 3 becomes sponsor
        enterInputAndSubmit("no");
        enterInputAndSubmit("yes");

        // Sponsor builds stage 1 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("q");

        // Sponsor builds stage 2 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("3");
        enterInputAndSubmit("q");

        // Sponsor builds stage 3 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("4");
        enterInputAndSubmit("q");

        // Player 3 - Hand
        assertHandSize(3, 7);

        // Stage 1

        // Handle Participation
        enterInputAndSubmit("no");
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 2 builds attack
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 9);
        // Player 4 - Hand
        assertHandSize(4, 9);

        // Stage 2

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 2 builds attack
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 9);
        // Player 4 - Hand
        assertHandSize(4, 9);

        // Stage 3

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 2 builds attack
        enterInputAndSubmit("9");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("9");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 9);
        // Player 4 - Hand
        assertHandSize(4, 9);

        // End of quest, sponsor discards
        enterInputAndSubmit("0");
        enterInputAndSubmit("1");
        enterInputAndSubmit("1");

        // Final Assertions

        // Player 1 - Shield/Hand count and Hand
        assertShieldCount(1, 0);
        assertHandSize(1, 12);
        assertHand(1, List.of(CardType.F15, CardType.F15, CardType.F20, CardType.F20, CardType.F20, CardType.F20, CardType.F25, CardType.F25, CardType.F30, CardType.HORSE, CardType.BATTLE_AXE, CardType.LANCE));

        // Player 2 - Shield/Hand count and Hand
        assertShieldCount(2, 7);
        assertHandSize(2, 9);
        assertHand(2, List.of(CardType.F10, CardType.F15, CardType.F15, CardType.F25, CardType.F30, CardType.F40, CardType.F50, CardType.LANCE, CardType.LANCE));

        // Player 3 - Shield/Hand count and Hand
        assertShieldCount(3, 0);
        assertHandSize(3, 12);
        assertHand(3, List.of(CardType.F20, CardType.F40, CardType.DAGGER, CardType.DAGGER, CardType.SWORD, CardType.HORSE, CardType.HORSE, CardType.HORSE, CardType.HORSE, CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.LANCE));

        // Player 4 - Shield/Hand count and Hand
        assertShieldCount(4, 7);
        assertHandSize(4, 9);
        assertHand(4, List.of(CardType.F15, CardType.F15, CardType.F20, CardType.F25, CardType.F30, CardType.F50, CardType.F70, CardType.LANCE, CardType.LANCE));

        // Player 2 - Winner
        assertWinner(2);

        // Player 4 - Winner
        assertWinner(4);
    }

    @Test
    @DisplayName("1winner_game_with_events")
    void one_winner_game_with_events() {
        RestAssured.baseURI = "http://localhost:8080";

        // Rig event deck
        rigDeck("/rigEventDeck", List.of(CardType.Q4, CardType.PLAGUE, CardType.PROSPERITY, CardType.QUEENS_FAVOR, CardType.Q3));

        // Rig adventure deck
        rigDeck("/rigAdventureDeck", List.of(
            CardType.F5, CardType.F10, CardType.F20, CardType.F15,
            CardType.F5, CardType.F25, CardType.F5, CardType.F10,
            CardType.F20, CardType.F5, CardType.F10, CardType.F20,
            CardType.F5, CardType.F5, CardType.F10, CardType.F10,
            CardType.F15, CardType.F15, CardType.F15, CardType.F15,
            CardType.F25, CardType.F25, CardType.HORSE, CardType.SWORD,
            CardType.BATTLE_AXE, CardType.F40, CardType.DAGGER, CardType.DAGGER,
            CardType.F30, CardType.F25, CardType.BATTLE_AXE, CardType.HORSE,
            CardType.F50, CardType.SWORD, CardType.SWORD, CardType.F40,
            CardType.F50, CardType.HORSE, CardType.HORSE, CardType.HORSE,
            CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.SWORD,
            CardType.F35
        ));

        // Rig player decks
        rigPlayerDeck(1, List.of(
            CardType.F5, CardType.F5, CardType.F10, CardType.F10,
            CardType.F15, CardType.F15, CardType.F20, CardType.F20,
            CardType.DAGGER, CardType.DAGGER, CardType.DAGGER, CardType.DAGGER
        ));

        rigPlayerDeck(2, List.of(
            CardType.F25, CardType.F30, CardType.HORSE, CardType.HORSE,
            CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.BATTLE_AXE,
            CardType.BATTLE_AXE, CardType.LANCE, CardType.LANCE, CardType.EXCALIBUR
        ));

        rigPlayerDeck(3, List.of(
            CardType.F25, CardType.F30, CardType.HORSE, CardType.HORSE,
            CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.BATTLE_AXE,
            CardType.BATTLE_AXE, CardType.LANCE, CardType.LANCE, CardType.EXCALIBUR
        ));

        rigPlayerDeck(4, List.of(
            CardType.F25, CardType.F30, CardType.F70, CardType.HORSE,
            CardType.HORSE, CardType.SWORD, CardType.SWORD, CardType.SWORD,
            CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.LANCE, CardType.LANCE
        ));

        clickDrawButton();

        // Player 1 becomes sponsor
        enterInputAndSubmit("yes");

        // Sponsor builds stage 1 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("q");

        // Sponsor builds stage 2 of quest
        enterInputAndSubmit("1");
        enterInputAndSubmit("q");

        // Sponsor builds stage 3 of quest
        enterInputAndSubmit("2");
        enterInputAndSubmit("q");

        // Sponsor builds stage 4 of quest
        enterInputAndSubmit("3");
        enterInputAndSubmit("q");

        // Player 1 - Hand
        assertHandSize(1, 8);

        // Stage 1

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // All players discard after participation
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Player 2 builds attack
        enterInputAndSubmit("2");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("2");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("3");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 11);
        // Player 3 - Hand
        assertHandSize(3, 11);
        // Player 4 - Hand
        assertHandSize(4, 11);

        // Stage 2

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 2 builds attack
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 11);
        // Player 3 - Hand
        assertHandSize(3, 11);
        // Player 4 - Hand
        assertHandSize(4, 11);

        // Stage 3

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 2 builds attack
        enterInputAndSubmit("7");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("7");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("8");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 11);
        // Player 3 - Hand
        assertHandSize(3, 11);
        // Player 4 - Hand
        assertHandSize(4, 11);

        // Stage 4

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 2 builds attack
        enterInputAndSubmit("10");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("10");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("11");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 11);
        // Player 3 - Hand
        assertHandSize(3, 11);
        // Player 4 - Hand
        assertHandSize(4, 11);

        // End of quest, sponsor discards
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("1");
        enterInputAndSubmit("1");

        // Player 1 - Hand
        assertHandSize(1, 12);
        // Player 1 - Shield count
        assertShieldCount(1, 0);
        // Player 2 - Shield count
        assertShieldCount(2, 4);
        // Player 3 - Shield count
        assertShieldCount(3, 4);
        // Player 4 - Shield count
        assertShieldCount(4, 4);

        // Player 1 turn is done, now is Player 2's turn
        clickEndTurnButton();
        clickDrawButton();

        // Player 2 - Shield count
        assertShieldCount(2, 2);

        // Player 2 turn is done, now is Player 3's turn
        clickEndTurnButton();
        clickDrawButton();

        // Player 1 trims hand
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Player 2 trims hand
        enterInputAndSubmit("0");

        // Player 3 trims hand
        enterInputAndSubmit("0");

        // Player 4 trims hand
        enterInputAndSubmit("0");

        // Player 1 - Hand
        assertHandSize(1, 12);
        // Player 2 - Hand
        assertHandSize(2, 12);
        // Player 3 - Hand
        assertHandSize(3, 12);
        // Player 4 - Hand
        assertHandSize(4, 12);

        // Player 3 turn is done, now is Player 4's turn
        clickEndTurnButton();
        clickDrawButton();

        // Player 4 trims hand
        enterInputAndSubmit("1");
        enterInputAndSubmit("3");

        // Player 4 - Hand
        assertHandSize(4, 12);

        // Player 4 turn is done, now is Player 1's turn
        clickEndTurnButton();
        clickDrawButton();

        // Player 1 becomes sponsor
        enterInputAndSubmit("yes");

        // Sponsor builds stage 1 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("q");

        // Sponsor builds stage 2 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("7");
        enterInputAndSubmit("q");

        // Sponsor builds stage 3 of quest
        enterInputAndSubmit("3");
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        // Player 1 - Hand
        assertHandSize(1, 7);

        // Stage 1

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // All players discard after participation
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Player 2 builds attack
        enterInputAndSubmit("8");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("8");
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("9");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 11);
        // Player 3 - Hand
        assertHandSize(3, 11);
        // Player 4 - Hand
        assertHandSize(4, 11);

        // Stage 2

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 2 builds attack
        enterInputAndSubmit("9");
        enterInputAndSubmit("8");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("9");
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 10);
        // Player 3 - Hand
        assertHandSize(3, 10);

        // Stage 3

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // Player 2 builds attack
        enterInputAndSubmit("9");
        enterInputAndSubmit("6");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("10");
        enterInputAndSubmit("q");

        // End of quest, sponsor discards
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Player 2 - Hand
        assertHandSize(2, 9);
        // Player 3 - Hand
        assertHandSize(3, 10);

        // Final Assertions

        // Player 1 - Shield/Hand count and Hand
        assertShieldCount(1, 0);
        assertHandSize(1, 12);
        assertHand(1, List.of(CardType.F25, CardType.F25, CardType.F35, CardType.DAGGER, CardType.DAGGER, CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.HORSE, CardType.HORSE, CardType.HORSE));

        // Player 2 - Shield/Hand count and Hand
        assertShieldCount(2, 5);
        assertHandSize(2, 9);
        assertHand(2, List.of(CardType.F15, CardType.F25, CardType.F30, CardType.F40, CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.HORSE, CardType.EXCALIBUR));

        // Player 3 - Shield/Hand count and Hand
        assertShieldCount(3, 7);
        assertHandSize(3, 10);
        assertHand(3, List.of(CardType.F10, CardType.F25, CardType.F30, CardType.F40, CardType.F50, CardType.SWORD, CardType.SWORD, CardType.HORSE, CardType.HORSE, CardType.LANCE));

        // Player 4 - Shield/Hand count and Hand
        assertShieldCount(4, 4);
        assertHandSize(4, 11);
        assertHand(4, List.of(CardType.F25, CardType.F25, CardType.F30, CardType.F50, CardType.F70, CardType.DAGGER, CardType.DAGGER, CardType.SWORD, CardType.SWORD, CardType.BATTLE_AXE, CardType.LANCE));

        // Player 3 - Winner
        assertWinner(3);

    }

    @Test
    @DisplayName("0_winner_quest")
    void zero_winner_quest() {
        RestAssured.baseURI = "http://localhost:8080";

        // Rig event deck
        rigDeck("/rigEventDeck", List.of(CardType.Q2));

        // Rig adventure deck
        rigDeck("/rigAdventureDeck", List.of(
            CardType.F5, CardType.F15, CardType.F10, CardType.F5,
            CardType.F10, CardType.F15, CardType.DAGGER, CardType.DAGGER,
            CardType.DAGGER, CardType.DAGGER, CardType.HORSE, CardType.HORSE,
            CardType.HORSE, CardType.HORSE, CardType.SWORD, CardType.SWORD,
            CardType.SWORD
        ));

        // Rig player decks
        rigPlayerDeck(1, List.of(
            CardType.F50, CardType.F70, CardType.DAGGER, CardType.DAGGER,
            CardType.HORSE, CardType.HORSE, CardType.SWORD, CardType.SWORD,
            CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.LANCE, CardType.LANCE
        ));

        rigPlayerDeck(2, List.of(
            CardType.F5, CardType.F5, CardType.F10, CardType.F15, CardType.F15,
            CardType.F20, CardType.F20, CardType.F25, CardType.F30, CardType.F30,
            CardType.F40, CardType.EXCALIBUR
        ));

        rigPlayerDeck(3, List.of(
            CardType.F5, CardType.F5, CardType.F10, CardType.F15, CardType.F15,
            CardType.F20, CardType.F20, CardType.F25, CardType.F25, CardType.F30,
            CardType.F40, CardType.LANCE
        ));

        rigPlayerDeck(4, List.of(
            CardType.F5, CardType.F5, CardType.F10, CardType.F15, CardType.F15,
            CardType.F20, CardType.F20, CardType.F25, CardType.F25, CardType.F30,
            CardType.F50, CardType.EXCALIBUR
        ));

        clickDrawButton();

        // Player 1 becomes sponsor
        enterInputAndSubmit("yes");

        // Sponsor builds stage 1 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("2");
        enterInputAndSubmit("6");
        enterInputAndSubmit("4");
        enterInputAndSubmit("8");
        enterInputAndSubmit("10");
        enterInputAndSubmit("q");

        // Sponsor builds stage 2 of quest
        enterInputAndSubmit("0");
        enterInputAndSubmit("1");
        enterInputAndSubmit("3");
        enterInputAndSubmit("2");
        enterInputAndSubmit("4");
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Player 1 - Hand
        assertHandSize(1, 0);

        // Stage 1

        // Handle Participation
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");
        enterInputAndSubmit("yes");

        // All players discard after participation
        enterInputAndSubmit("0");
        enterInputAndSubmit("3");
        enterInputAndSubmit("2");

        // Player 2 builds attack
        enterInputAndSubmit("11");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("q");

        // Player 2 - Hand
        assertHandSize(2, 11);
        // Player 3 - Hand
        assertHandSize(3, 12);
        // Player 4 - Hand
        assertHandSize(4, 12);

        // End of quest, sponsor discards
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Player 1 - Hand
        assertHandSize(1, 12);
        assertHand(1, List.of(CardType.F15, CardType.DAGGER, CardType.DAGGER, CardType.DAGGER, CardType.DAGGER, CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.HORSE, CardType.HORSE, CardType.HORSE, CardType.HORSE));

        // Player 2 - Hand
        assertHand(2, List.of(CardType.F5, CardType.F5, CardType.F10, CardType.F15, CardType.F15, CardType.F20, CardType.F20, CardType.F25, CardType.F30, CardType.F30, CardType.F40));

        // Player 3 - Hand
        assertHand(3, List.of(CardType.F5, CardType.F5, CardType.F10, CardType.F15, CardType.F15, CardType.F20, CardType.F20, CardType.F25, CardType.F25, CardType.F30, CardType.F40, CardType.LANCE));

        // Player 4 - Hand
        assertHand(4, List.of(CardType.F5, CardType.F5, CardType.F10, CardType.F15, CardType.F15, CardType.F20, CardType.F20, CardType.F25, CardType.F25, CardType.F30, CardType.F50, CardType.EXCALIBUR));

    }

    private void assertHand(int playerId, List<CardType> hand) {
        assertEquals(hand, getHand(playerId));

        // Make sure hand displayed on web is equal
        String displayedHand = driver.findElement(By.id("p" + playerId + "Hand")).getText();
        displayedHand = displayedHand.replace("\n", ",").replaceAll("\\s*,\\s*", ",");

        StringBuilder expectedHandBuilder = new StringBuilder();

        // Create string that represents the players hand
        for(CardType type : hand) {
            expectedHandBuilder.append(type.getName()).append(",");
        }

        // Remove the last comma and space if it exists
        if (!expectedHandBuilder.isEmpty()) {
            expectedHandBuilder.setLength(expectedHandBuilder.length() - 1);
        }

        assertEquals(expectedHandBuilder.toString(), displayedHand);
    }

    private void assertWinner(int playerId) {
        assertTrue(isWinner(playerId));
        assertTrue(getUIPlayerIsWinner(playerId));
    }

    private void assertShieldCount(int playerId, int expected) {
        assertEquals(expected, getShields(playerId));
        assertEquals(expected, getUIPlayerShieldCount(playerId));
    }

    private void assertHandSize(int playerId, int expected) {
        assertEquals(expected, getHandSize(playerId));
        assertEquals(expected, getUIPlayerHandSize(playerId));
    }

    private Boolean getUIPlayerIsWinner(int playerId) {
        String id = "p" + playerId + "IsWinner";
        return Objects.equals(driver.findElement(By.id(id)).getText(), "Yes");
    }

    private int getUIPlayerHandSize(int playerId) {
        String id = "p" + playerId + "CardCount";
        return Integer.parseInt(driver.findElement(By.id(id)).getText());
    }

    private int getUIPlayerShieldCount(int playerId) {
        String id = "p" + playerId + "ShieldCount";
        return Integer.parseInt(driver.findElement(By.id(id)).getText());
    }

    private boolean isWinner(int playerId) {
        return given()
                .queryParam("playerId", playerId)
                .contentType("application/json")
                .get("/isWinner")
                .then()
                .statusCode(200)
                .extract()
                .as(Boolean.class);
    }

    private List<CardType> getHand(int playerId) {
        Response response = given()
                .queryParam("playerId", playerId)
                .contentType(ContentType.JSON)
                .get("/getHand")
                .then()
                .statusCode(200)
                .extract()
                .response();
        List<String> cardTypeStrings = response.jsonPath().getList("type");

        List<CardType> cardTypes = new ArrayList<>();

        for (String cardTypeString : cardTypeStrings) {
            CardType cardType = CardType.valueOf(cardTypeString);  // Convert string to CardType enum
            cardTypes.add(cardType);  // Add the CardType to the list
        }

        return cardTypes;

    }

    private int getHandSize(int playerId) {
        return given()
                .queryParam("playerId", playerId)
                .contentType("application/json")
                .get("/getCardCount")
                .then()
                .statusCode(200)
                .extract()
                .as(Integer.class);
    }

    private int getShields(int playerId) {

        return given()
                .queryParam("playerId", playerId)
                .contentType("application/json")
                .get("/getShields")
                .then()
                .statusCode(200)
                .extract()
                .as(Integer.class);
    }

    private void clickDrawButton() {
        try {
            Thread.sleep(1250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.id("draw")).click();
    }

    private void clickSubmitButton() {
        try {
            Thread.sleep(1250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.id("submit")).click();
    }

    private void clickEndTurnButton() {
        driver.findElement(By.id("end-turn")).click();
    }

    private void enterInputAndSubmit(String text) {
        driver.findElement(By.id("input")).sendKeys(text);
        try {
            Thread.sleep(1250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clickSubmitButton();
        try {
            Thread.sleep(1250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void rigDeck(String endpoint, List<CardType> cardTypes) {
        given()
                .contentType("application/json")
                .body(cardTypes)
                .post(endpoint)
                .then()
                .statusCode(200);
    }

    private void rigPlayerDeck(int playerId, List<CardType> cardTypes) {
        given()
                .queryParam("playerId", playerId)
                .contentType("application/json")
                .body(cardTypes)
                .post("/rigPlayerDeck")
                .then()
                .statusCode(200);
    }
}
