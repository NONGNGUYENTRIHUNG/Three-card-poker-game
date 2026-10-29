package edu.uic.cs342.poker;
import edu.uic.cs342.poker.model.*;
import edu.uic.cs342.poker.logic.ThreeCardLogic;
import edu.uic.cs342.poker.service.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
class ThreeCardLogicTest {

    static ArrayList<Card> h(String text) {
        ArrayList<Card> cards = new ArrayList<>();
        for (String token : text.split(" ")) cards.add(new Card(token.charAt(0), Integer.parseInt(token.substring(1))));
        return cards;
    }
    @Test void straightFlush() { assertEquals(1, ThreeCardLogic.evalHand(h("S7 S8 S9"))); }
    @Test void trips() { assertEquals(2, ThreeCardLogic.evalHand(h("C7 D7 H7"))); }
    @Test void straight() { assertEquals(3, ThreeCardLogic.evalHand(h("C7 D8 H9"))); }
    @Test void flush() { assertEquals(4, ThreeCardLogic.evalHand(h("S2 S6 S10"))); }
    @Test void pair() { assertEquals(5, ThreeCardLogic.evalHand(h("C7 D7 H9"))); }
    @Test void highCard() { assertEquals(0, ThreeCardLogic.evalHand(h("C2 D6 H10"))); }
    @Test void aceLowStraight() { assertEquals(3, ThreeCardLogic.evalHand(h("C14 D2 H3"))); }
    @Test void aceHighStraight() { assertEquals(3, ThreeCardLogic.evalHand(h("C12 D13 H14"))); }
    @Test void wrapIsNotStraight() { assertEquals(0, ThreeCardLogic.evalHand(h("C13 D14 H2"))); }
    @Test void categoryBeatsHighCard() { assertEquals(2, ThreeCardLogic.compareHands(h("C14 D9 H7"),h("C2 D2 H4"))); }
    @Test void straightBeatsFlush() { assertEquals(2, ThreeCardLogic.compareHands(h("C4 C8 C12"),h("C4 D5 H6"))); }
    @Test void dealerWins() { assertEquals(1, ThreeCardLogic.compareHands(h("C14 D9 H7"),h("C13 D9 H7"))); }
    @Test void highCardTie() { assertEquals(0, ThreeCardLogic.compareHands(h("C14 D9 H7"),h("S14 H9 C7"))); }
    @Test void secondKicker() { assertEquals(2, ThreeCardLogic.compareHands(h("C14 D8 H7"),h("S14 H9 C7"))); }
    @Test void thirdKicker() { assertEquals(2, ThreeCardLogic.compareHands(h("C14 D9 H6"),h("S14 H9 C7"))); }
    @Test void pairKicker() { assertEquals(2, ThreeCardLogic.compareHands(h("C7 D7 H9"),h("S7 H7 C10"))); }
    @Test void pairRankBeforeKicker() { assertEquals(1, ThreeCardLogic.compareHands(h("C8 D8 H2"),h("S7 H7 C14"))); }
    @Test void lowestStraight() { assertEquals(2, ThreeCardLogic.compareHands(h("C14 D2 H3"),h("C2 D3 H4"))); }
    @Test void straightFlushTie() { assertEquals(0, ThreeCardLogic.compareHands(h("C5 C6 C7"),h("S5 S6 S7"))); }
    @Test void tripsRank() { assertEquals(1, ThreeCardLogic.compareHands(h("C9 D9 H9"),h("C8 D8 H8"))); }
    @Test void sfPayout() { assertEquals(200, ThreeCardLogic.evalPPWinnings(h("S7 S8 S9"),5)); }
    @Test void tripsPayout() { assertEquals(150, ThreeCardLogic.evalPPWinnings(h("C7 D7 H7"),5)); }
    @Test void straightPayout() { assertEquals(30, ThreeCardLogic.evalPPWinnings(h("C7 D8 H9"),5)); }
    @Test void flushPayout() { assertEquals(15, ThreeCardLogic.evalPPWinnings(h("S2 S6 S10"),5)); }
    @Test void pairPayout() { assertEquals(5, ThreeCardLogic.evalPPWinnings(h("C7 D7 H9"),5)); }
    @Test void noPayout() { assertEquals(0, ThreeCardLogic.evalPPWinnings(h("C2 D6 H10"),5)); }
    @Test void zeroBet() { assertEquals(0, ThreeCardLogic.evalPPWinnings(h("S7 S8 S9"),0)); }
    @Test void queenQualifies() { assertTrue(ThreeCardLogic.dealerQualifies(h("C12 D9 H7"))); }
    @Test void jackDoesNotQualify() { assertFalse(ThreeCardLogic.dealerQualifies(h("C11 D9 H7"))); }
    @Test void lowPairQualifies() { assertTrue(ThreeCardLogic.dealerQualifies(h("C2 D2 H7"))); }
    @Test void rejectDuplicate() { assertThrows(IllegalArgumentException.class, () -> ThreeCardLogic.evalHand(h("C2 C2 H7"))); }
    @Test void rejectShortHand() { assertThrows(IllegalArgumentException.class, () -> ThreeCardLogic.evalHand(h("C2 H7"))); }
    @Test void doesNotReorderInput() { var hand=h("H14 C2 D3"); var before=List.copyOf(hand); ThreeCardLogic.evalHand(hand); assertEquals(before,hand); }
}
