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
        rigPlayerDeck(0, List.of(
            CardType.F5, CardType.F5, CardType.F15, CardType.F15, CardType.DAGGER,
            CardType.SWORD, CardType.SWORD, CardType.HORSE, CardType.HORSE,
            CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.LANCE
        ));

        rigPlayerDeck(1, List.of(
            CardType.F5, CardType.F5, CardType.F15, CardType.F15, CardType.F40,
            CardType.DAGGER, CardType.SWORD, CardType.HORSE, CardType.HORSE,
            CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.EXCALIBUR
        ));

        rigPlayerDeck(2, List.of(
            CardType.F5, CardType.F5, CardType.F5, CardType.F15, CardType.DAGGER,
            CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.HORSE,
            CardType.HORSE, CardType.BATTLE_AXE, CardType.LANCE
        ));

        rigPlayerDeck(3, List.of(
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

        // End of quest, sponsor discards
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Final Assertions

        // Player 3 - Shield count
        assertEquals(0, getShields(3));
        assertEquals(0, getUIPlayerShieldCount(3));

        // Player 4 - Shield count
        assertEquals(4, getShields(4));
        assertEquals(4, getUIPlayerShieldCount(4));

        // Player 1 - Hand size
        assertEquals(9, getHandSize(1));
        assertEquals(9, getUIPlayerHandSize(1));

        // Player 1 - Hand cards (specific list of CardTypes)
        assertEquals(
            List.of(
                CardType.F5, CardType.F10, CardType.F15, CardType.F15,
                CardType.F30, CardType.HORSE, CardType.BATTLE_AXE, CardType.BATTLE_AXE,
                CardType.LANCE
            ),
            getHand(1)
        );

        // Player 3 - Hand size
        assertEquals(5, getHandSize(3));
        assertEquals(5, getUIPlayerHandSize(3));

        // Player 3 - Hand cards (specific list of CardTypes)
        assertEquals(
            List.of(
                CardType.F5, CardType.F5, CardType.F15, CardType.F30,
                CardType.SWORD
            ),
            getHand(3)
        );

        // Player 4 - Hand size
        assertEquals(4, getHandSize(4));
        assertEquals(4, getUIPlayerHandSize(4));

        // Player 4 - Hand cards
        assertEquals(
            List.of(
                CardType.F15, CardType.F15, CardType.F40, CardType.LANCE
            ),
            getHand(4)
        );

        // Player 2 - Hand size
        assertEquals(12, getHandSize(2));
        assertEquals(12, getUIPlayerHandSize(2));
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
            CardType.F25, CardType.F25, CardType.F20, CardType.F25,
            CardType.F30, CardType.F30, CardType.SWORD, CardType.BATTLE_AXE,
            CardType.BATTLE_AXE, CardType.LANCE
        ));

        // Rig player decks
        rigPlayerDeck(0, List.of(
            CardType.F5, CardType.F5, CardType.F10, CardType.F10,
            CardType.F15, CardType.F15, CardType.DAGGER, CardType.HORSE,
            CardType.HORSE, CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.LANCE
        ));

        rigPlayerDeck(1, List.of(
            CardType.F40, CardType.F50, CardType.HORSE, CardType.HORSE,
            CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.BATTLE_AXE,
            CardType.BATTLE_AXE, CardType.LANCE, CardType.LANCE, CardType.EXCALIBUR
        ));

        rigPlayerDeck(2, List.of(
            CardType.F5, CardType.F5, CardType.F5, CardType.F5,
            CardType.DAGGER, CardType.DAGGER, CardType.DAGGER, CardType.HORSE,
            CardType.HORSE, CardType.HORSE, CardType.HORSE, CardType.HORSE
        ));

        rigPlayerDeck(3, List.of(
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
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

        // Player 3 builds attack
        enterInputAndSubmit("q");

        // Player 4 builds attack
        enterInputAndSubmit("5");
        enterInputAndSubmit("q");

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

        // End of quest, sponsor discards
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Player 2 - Shield count
        assertEquals(4, getShields(2));
        assertEquals(4, getUIPlayerShieldCount(2));

        // Player 4 - Shield count
        assertEquals(4, getShields(4));
        assertEquals(4, getUIPlayerShieldCount(4));

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

        // End of quest, sponsor discards
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Final Assertions

        // Player 2 - Shield count
        assertEquals(7, getShields(2));
        assertEquals(7, getUIPlayerShieldCount(2));

        // Player 4 - Shield count
        assertEquals(7, getShields(4));
        assertEquals(7, getUIPlayerShieldCount(4));

        // Player 2 - Winner
        assertTrue(isWinner(2));
        assertTrue(getUIPlayerIsWinner(2));

        // Player 4 - Winner
        assertTrue(isWinner(4));
        assertTrue(getUIPlayerIsWinner(4));

        // Player 1 - Hand
        assertEquals(
            List.of(
                CardType.F15, CardType.F15, CardType.F20, CardType.F20,
                CardType.F20, CardType.F20, CardType.F25, CardType.F25,
                CardType.F30, CardType.HORSE, CardType.BATTLE_AXE, CardType.LANCE
            ),
            getHand(1)
        );

        // Player 2 - Hand
        assertEquals(
            List.of(
                CardType.F10, CardType.F15, CardType.F15, CardType.F25,
                CardType.F30, CardType.F40, CardType.F50, CardType.LANCE,
                CardType.LANCE
            ),
            getHand(2)
        );

        // Player 3 - Hand
        assertEquals(
            List.of(
                CardType.F30, CardType.F40, CardType.DAGGER, CardType.DAGGER,
                CardType.SWORD, CardType.HORSE, CardType.HORSE, CardType.HORSE,
                CardType.HORSE, CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.LANCE
            ),
            getHand(3)
        );

        // Player 4 - Hand
        assertEquals(
            List.of(
                CardType.F15, CardType.F15, CardType.F20, CardType.F25,
                CardType.F30, CardType.F50, CardType.F70, CardType.LANCE,
                CardType.LANCE
            ),
            getHand(4)
        );
    }

//    @Test
//    @DisplayName("1winner_game_with_events")
//    void one_winner_game_with_events() {
//        RestAssured.baseURI = "http://localhost:8080";
//
//        // Rig event deck
//        rigDeck("/rigEventDeck", List.of(CardType.Q4, CardType.PLAGUE, CardType.PROSPERITY, CardType.QUEENS_FAVOR, CardType.Q3));
//
//        // Rig adventure deck
//        rigDeck("/rigAdventureDeck", List.of(
//            CardType.F5, CardType.F10, CardType.F20, CardType.F15,
//            CardType.F5, CardType.F25, CardType.F5, CardType.F10,
//            CardType.F20, CardType.F5, CardType.F10, CardType.F20,
//            CardType.F5, CardType.F10, CardType.F10, CardType.F15,
//            CardType.F15, CardType.F15, CardType.F20, CardType.F20,
//            CardType.F15, CardType.F15, CardType.HORSE, CardType.SWORD,
//            CardType.BATTLE_AXE, CardType.F40, CardType.DAGGER, CardType.DAGGER,
//            CardType.F20, CardType.F25, CardType.BATTLE_AXE, CardType.HORSE,
//            CardType.F30, CardType.SWORD, CardType.SWORD, CardType.F40,
//            CardType.F50, CardType.HORSE, CardType.HORSE, CardType.HORSE,
//            CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.SWORD,
//            CardType.F50
//        ));
//
//        // Rig player decks
//        rigPlayerDeck(0, List.of(
//            CardType.F5, CardType.F5, CardType.F10, CardType.F10,
//            CardType.F15, CardType.F15, CardType.F20, CardType.F20,
//            CardType.DAGGER, CardType.DAGGER, CardType.DAGGER, CardType.DAGGER
//        ));
//
//        rigPlayerDeck(1, List.of(
//            CardType.F25, CardType.F30, CardType.HORSE, CardType.HORSE,
//            CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.BATTLE_AXE,
//            CardType.BATTLE_AXE, CardType.LANCE, CardType.LANCE, CardType.EXCALIBUR
//        ));
//
//        rigPlayerDeck(2, List.of(
//            CardType.F25, CardType.F30, CardType.HORSE, CardType.HORSE,
//            CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.BATTLE_AXE,
//            CardType.BATTLE_AXE, CardType.LANCE, CardType.LANCE, CardType.EXCALIBUR
//        ));
//
//        rigPlayerDeck(3, List.of(
//            CardType.F25, CardType.F30, CardType.F70, CardType.HORSE,
//            CardType.HORSE, CardType.SWORD, CardType.SWORD, CardType.SWORD,
//            CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.LANCE, CardType.LANCE
//        ));
//
//        clickDrawButton();
//
//        // Player 1 becomes sponsor
//        enterInputAndSubmit("yes");
//
//        // Sponsor builds stage 1 of quest
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("q");
//
//        // Sponsor builds stage 2 of quest
//        enterInputAndSubmit("1");
//        enterInputAndSubmit("q");
//
//        // Sponsor builds stage 3 of quest
//        enterInputAndSubmit("2");
//        enterInputAndSubmit("q");
//
//        // Sponsor builds stage 4 of quest
//        enterInputAndSubmit("3");
//        enterInputAndSubmit("q");
//
//        // Stage 1
//
//        // Handle Participation
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//
//        // All players discard after participation
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//
//        // Player 2 builds attack
//        enterInputAndSubmit("2");
//        enterInputAndSubmit("q");
//
//        // Player 3 builds attack
//        enterInputAndSubmit("2");
//        enterInputAndSubmit("q");
//
//        // Player 4 builds attack
//        enterInputAndSubmit("3");
//        enterInputAndSubmit("q");
//
//        // Stage 2
//
//        // Handle Participation
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//
//        // Player 2 builds attack
//        enterInputAndSubmit("5");
//        enterInputAndSubmit("q");
//
//        // Player 3 builds attack
//        enterInputAndSubmit("5");
//        enterInputAndSubmit("q");
//
//        // Player 4 builds attack
//        enterInputAndSubmit("6");
//        enterInputAndSubmit("q");
//
//        // Stage 3
//
//        // Handle Participation
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//
//        // Player 2 builds attack
//        enterInputAndSubmit("7");
//        enterInputAndSubmit("q");
//
//        // Player 3 builds attack
//        enterInputAndSubmit("7");
//        enterInputAndSubmit("q");
//
//        // Player 4 builds attack
//        enterInputAndSubmit("8");
//        enterInputAndSubmit("q");
//
//        // Stage 4
//
//        // Handle Participation
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//
//        // Player 2 builds attack
//        enterInputAndSubmit("10");
//        enterInputAndSubmit("q");
//
//        // Player 3 builds attack
//        enterInputAndSubmit("10");
//        enterInputAndSubmit("q");
//
//        // Player 4 builds attack
//        enterInputAndSubmit("11");
//        enterInputAndSubmit("q");
//
//        // End of quest, sponsor discards
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//
//        // Player 1 - Shield count
//        assertEquals(0, getShields(1));
//        assertEquals(0, getUIPlayerShieldCount(1));
//
//        // Player 2 - Shield count
//        assertEquals(4, getShields(2));
//        assertEquals(4, getUIPlayerShieldCount(2));
//
//        // Player 3 - Shield count
//        assertEquals(4, getShields(3));
//        assertEquals(4, getUIPlayerShieldCount(3));
//
//        // Player 4 - Shield count
//        assertEquals(4, getShields(4));
//        assertEquals(4, getUIPlayerShieldCount(4));
//
//        // Player 1 turn is done, now is Player 2's turn
//        clickEndTurnButton();
//        clickDrawButton();
//
//        // Player 2 - Shield count
//        assertEquals(2, getShields(2));
//        assertEquals(2, getUIPlayerShieldCount(2));
//
//        // Player 2 turn is done, now is Player 3's turn
//        clickEndTurnButton();
//        clickDrawButton();
//
//        // Player 1 trims hand
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//
//        // Player 2 trims hand
//        enterInputAndSubmit("0");
//
//        // Player 3 trims hand
//        enterInputAndSubmit("0");
//
//        // Player 4 trims hand
//        enterInputAndSubmit("0");
//
//        // Player 3 turn is done, now is Player 4's turn
//        clickEndTurnButton();
//        clickDrawButton();
//
//        // Player 4 trims hand
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//
//        // Player 4 turn is done, now is Player 1's turn
//        clickEndTurnButton();
//        clickDrawButton();
//
//        // Player 1 becomes sponsor
//        enterInputAndSubmit("yes");
//
//        // Sponsor builds stage 1 of quest
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("q");
//
//        // Sponsor builds stage 2 of quest
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("7");
//        enterInputAndSubmit("q");
//
//        // Sponsor builds stage 3 of quest
//        enterInputAndSubmit("3");
//        enterInputAndSubmit("6");
//        enterInputAndSubmit("q");
//
//        // Stage 1
//
//        // Handle Participation
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//
//        // All players discard after participation
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//
//        // Player 2 builds attack
//        enterInputAndSubmit("2");
//        enterInputAndSubmit("q");
//
//        // Player 3 builds attack
//        enterInputAndSubmit("2");
//        enterInputAndSubmit("q");
//
//        // Player 4 builds attack
//        enterInputAndSubmit("3");
//        enterInputAndSubmit("q");
//
//        // Stage 2
//
//        // Handle Participation
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//
//        // Player 2 builds attack
//        enterInputAndSubmit("5");
//        enterInputAndSubmit("q");
//
//        // Player 3 builds attack
//        enterInputAndSubmit("5");
//        enterInputAndSubmit("q");
//
//        // Player 4 builds attack
//        enterInputAndSubmit("6");
//        enterInputAndSubmit("q");
//
//        // Stage 3
//
//        // Handle Participation
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//        enterInputAndSubmit("yes");
//
//        // Player 2 builds attack
//        enterInputAndSubmit("7");
//        enterInputAndSubmit("q");
//
//        // Player 3 builds attack
//        enterInputAndSubmit("7");
//        enterInputAndSubmit("q");
//
//        // Player 4 builds attack
//        enterInputAndSubmit("8");
//        enterInputAndSubmit("q");
//
//        // End of quest, sponsor discards
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//        enterInputAndSubmit("0");
//
//    }

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
        rigPlayerDeck(0, List.of(
            CardType.F50, CardType.F70, CardType.DAGGER, CardType.DAGGER,
            CardType.HORSE, CardType.HORSE, CardType.SWORD, CardType.SWORD,
            CardType.BATTLE_AXE, CardType.BATTLE_AXE, CardType.LANCE, CardType.LANCE
        ));

        rigPlayerDeck(1, List.of(
            CardType.F5, CardType.F5, CardType.F10, CardType.F15, CardType.F15,
            CardType.F20, CardType.F20, CardType.F25, CardType.F30, CardType.F30,
            CardType.F40, CardType.EXCALIBUR
        ));

        rigPlayerDeck(2, List.of(
            CardType.F5, CardType.F5, CardType.F10, CardType.F15, CardType.F15,
            CardType.F20, CardType.F20, CardType.F25, CardType.F25, CardType.F30,
            CardType.F40, CardType.LANCE
        ));

        rigPlayerDeck(3, List.of(
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

        // End of quest, sponsor discards
        enterInputAndSubmit("0");
        enterInputAndSubmit("0");

        // Player 1 - Hand
        assertEquals(
            List.of(
                CardType.F15, CardType.DAGGER, CardType.DAGGER, CardType.DAGGER, CardType.DAGGER,
                CardType.SWORD, CardType.SWORD, CardType.SWORD, CardType.HORSE,
                CardType.HORSE, CardType.HORSE, CardType.HORSE
            ),
            getHand(1)
        );

        // Player 2 - Hand
        assertEquals(
            List.of(
                CardType.F5, CardType.F5, CardType.F10, CardType.F15, CardType.F15,
                CardType.F20, CardType.F20, CardType.F25, CardType.F30, CardType.F30,
                CardType.F40
            ),
            getHand(2)
        );

        // Player 3 - Hand
        assertEquals(
            List.of(
                CardType.F5, CardType.F5, CardType.F10, CardType.F15, CardType.F15,
                CardType.F20, CardType.F20, CardType.F25, CardType.F25, CardType.F30,
                CardType.F40, CardType.LANCE
            ),
            getHand(3)
        );

        // Player 4 - Hand
        assertEquals(
            List.of(
                CardType.F5, CardType.F5, CardType.F10, CardType.F15, CardType.F15,
                CardType.F20, CardType.F20, CardType.F25, CardType.F25, CardType.F30,
                CardType.F50, CardType.EXCALIBUR
            ),
            getHand(4)
        );

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
        driver.findElement(By.id("draw")).click();
    }

    private void clickSubmitButton() {
        driver.findElement(By.id("submit")).click();
    }

    private void clickEndTurnButton() {
        driver.findElement(By.id("end-turn")).click();
    }

    private void enterInputAndSubmit(String text) {
        driver.findElement(By.id("input")).sendKeys(text);
        clickSubmitButton();
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
