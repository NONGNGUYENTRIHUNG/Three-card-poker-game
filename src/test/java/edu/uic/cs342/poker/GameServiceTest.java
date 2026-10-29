package edu.uic.cs342.poker;
import edu.uic.cs342.poker.model.*;
import edu.uic.cs342.poker.logic.ThreeCardLogic;
import edu.uic.cs342.poker.service.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
class GameServiceTest {

    static ArrayList<Card> h(String text) {
        ArrayList<Card> cards = new ArrayList<>();
        for (String token : text.split(" ")) cards.add(new Card(token.charAt(0), Integer.parseInt(token.substring(1))));
        return cards;
    }

    static class ScriptedDealer extends Dealer {
        private final String[] hands;
        private int next;
        ScriptedDealer(String... hands) { this.hands=hands; }
        @Override public void reset() { next=0; }
        @Override public ArrayList<Card> dealHand() { return h(hands[next++]); }
    }
    GameService game(String p1,String p2,String dealer) {
        return new GameService(new ScriptedDealer(p1,p2,dealer));
    }
    GameService example() { return game("S9 H9 D4","C14 H11 S7","S13 H8 D2"); }
    void playBoth(GameService g) { g.decide(0,Player.Decision.PLAY); g.decide(1,Player.Decision.PLAY); }
    @Test void startsBetting() { assertEquals(GameService.Phase.BETTING,example().getPhase()); }
    @Test void cannotPlayBeforeDeal() { assertThrows(IllegalStateException.class,() -> example().decide(0,Player.Decision.PLAY)); }
    @Test void invalidWagerLeavesStateUntouched() {
        GameService g=example(); assertThrows(IllegalArgumentException.class,() -> g.deal(10,5,26,5));
        assertTrue(g.getPlayer(0).getHand().isEmpty()); assertEquals(GameService.Phase.BETTING,g.getPhase());
    }
    @Test void wagerBounds() { assertTrue(GameService.validAnte(5)); assertTrue(GameService.validAnte(25)); assertFalse(GameService.validAnte(4)); assertFalse(GameService.validAnte(26)); assertTrue(GameService.validPairPlus(0)); assertFalse(GameService.validPairPlus(1)); }
    @Test void dealerHiddenUntilBothDecide() { GameService g=example(); g.deal(10,5,10,5); g.decide(0,Player.Decision.PLAY); assertTrue(g.getDealerHand().isEmpty()); g.decide(1,Player.Decision.PLAY); assertEquals(3,g.getDealerHand().size()); }
    @Test void decisionsOnlyOnce() { GameService g=example(); g.deal(10,5,10,5); g.decide(0,Player.Decision.PLAY); assertThrows(IllegalStateException.class,() -> g.decide(0,Player.Decision.FOLD)); }
    @Test void playEqualsAnte() { GameService g=example(); g.deal(25,0,5,0); g.decide(0,Player.Decision.PLAY); assertEquals(25,g.getPlayer(0).getPlay()); }
    @Test void samplePayouts() { GameService g=example(); g.deal(10,5,10,5); playBoth(g); assertEquals(25,g.getResult(0).roundNet()); assertEquals(15,g.getResult(1).roundNet()); assertEquals(25,g.getResult(0).returnedStakes()); }
    @Test void foldForfeitsPairPlusEvenOnPair() { GameService g=example(); g.deal(10,5,10,0); g.decide(0,Player.Decision.FOLD); g.decide(1,Player.Decision.PLAY); assertEquals(-15,g.getResult(0).roundNet()); assertEquals(0,g.getPlayer(0).getPlay()); }
    @Test void bothFold() { GameService g=example(); g.deal(10,5,10,0); g.decide(0,Player.Decision.FOLD); g.decide(1,Player.Decision.FOLD); assertEquals(GameService.Phase.RESULTS,g.getPhase()); assertEquals(-10,g.getResult(1).roundNet()); }
    @Test void losingHandStillWinsPairPlus() { GameService g=game("C2 D2 H5","C4 D7 H9","C13 D13 H14"); g.deal(10,5,10,0); playBoth(g); assertEquals(-15,g.getResult(0).roundNet()); assertEquals(5,g.getResult(0).pairPlusNet()); }
    @Test void tieReturnsBothStakes() { GameService g=game("C14 D9 H7","C4 D7 H9","S14 H9 C7"); g.deal(10,0,10,0); playBoth(g); assertEquals(0,g.getResult(0).roundNet()); assertEquals(20,g.getResult(0).returnedStakes()); }
    @Test void nonQualificationReturnsPlayCarriesAnteAndPaysPairPlus() { GameService g=game("C2 D2 H5","C14 D7 H9","C11 D8 H4"); g.deal(10,5,15,0); playBoth(g); assertEquals(10,g.getResult(0).carriedAnte()); assertEquals(15,g.getResult(0).returnedStakes()); assertEquals(5,g.getResult(0).roundNet()); g.nextRound(); assertEquals(10,g.getPlayer(0).getAnte()); assertEquals(5,g.getPlayer(0).getTotalWinnings()); assertThrows(IllegalArgumentException.class,() -> g.deal(15,0,15,0)); }
    @Test void carriedAnteEventuallyLostOnce() { GameService g=game("C2 D2 H5","C14 D7 H9","C11 D8 H4"); g.deal(10,0,15,0); playBoth(g); g.nextRound(); g.deal(10,0,15,0); g.decide(0,Player.Decision.FOLD); g.decide(1,Player.Decision.FOLD); assertEquals(-10,g.getPlayer(0).getTotalWinnings()); assertEquals(0,g.getPlayer(0).getCarriedAnte()); }
    @Test void nextRoundClearsOnlyRoundState() { GameService g=example(); g.deal(10,5,10,5); playBoth(g); g.nextRound(); assertEquals(25,g.getPlayer(0).getTotalWinnings()); assertTrue(g.getPlayer(0).getHand().isEmpty()); assertEquals(Player.Decision.WAITING,g.getPlayer(0).getDecision()); assertEquals(2,g.getRound()); }
    @Test void cannotStartNextRoundEarly() { assertThrows(IllegalStateException.class,() -> example().nextRound()); }
    @Test void cannotDealTwice() { GameService g=example(); g.deal(10,0,10,0); assertThrows(IllegalStateException.class,() -> g.deal(10,0,10,0)); }
    @Test void cannotReadResultsEarly() { assertThrows(IllegalStateException.class,() -> example().getResult(0)); }
}
