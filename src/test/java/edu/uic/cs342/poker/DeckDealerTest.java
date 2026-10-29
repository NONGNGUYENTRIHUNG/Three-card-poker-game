package edu.uic.cs342.poker;
import edu.uic.cs342.poker.model.*;
import edu.uic.cs342.poker.logic.ThreeCardLogic;
import edu.uic.cs342.poker.service.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
class DeckDealerTest {
    @Test void deckSize() { assertEquals(52,new Deck().size()); }
    @Test void uniqueCards() { assertEquals(52,new HashSet<>(new Deck().getCards()).size()); }
    @Test void suits() { for(char s : new char[]{'C','D','H','S'}) assertEquals(13,new Deck().getCards().stream().filter(c -> c.suit()==s).count()); }
    @Test void values() { for(int v=2;v<=14;v++){ int rank=v; assertEquals(4,new Deck().getCards().stream().filter(c -> c.value()==rank).count()); } }
    @Test void drawRemovesCard() { Deck d=new Deck(); Card c=d.draw(); assertEquals(51,d.size()); assertFalse(d.getCards().contains(c)); }
    @Test void resetRestores() { Deck d=new Deck(); d.draw(); d.draw(); d.reset(); assertEquals(52,d.size()); assertEquals(52,new HashSet<>(d.getCards()).size()); }
    @Test void shufflePreservesContents() { Deck d=new Deck(); var before=new HashSet<>(d.getCards()); d.shuffle(); assertEquals(before,new HashSet<>(d.getCards())); }
    @Test void shuffleChangesOrder() { Deck d=new Deck(new Random(42)); var before=d.getCards(); d.shuffle(); assertNotEquals(before,d.getCards()); }
    @Test void exhaustion() { Deck d=new Deck(); for(int i=0;i<52;i++) d.draw(); assertThrows(IllegalStateException.class,d::draw); }
    @Test void snapshotCannotMutateDeck() { Deck d=new Deck(); assertThrows(UnsupportedOperationException.class,() -> d.getCards().clear()); }
    @Test void threeCardsDealt() { Dealer d=new Dealer(); assertEquals(3,d.dealHand().size()); assertEquals(49,d.remainingCards()); }
    @Test void threeHandsDisjoint() { Dealer d=new Dealer(); Set<Card> all=new HashSet<>(); for(int i=0;i<3;i++) all.addAll(d.dealHand()); assertEquals(9,all.size()); assertEquals(43,d.remainingCards()); }
    @Test void dealerReset() { Dealer d=new Dealer(); d.dealHand(); d.reset(); assertEquals(52,d.remainingCards()); }
    @Test void insufficientCardsAtomic() { Dealer d=new Dealer(); for(int i=0;i<17;i++) d.dealHand(); assertThrows(IllegalStateException.class,d::dealHand); assertEquals(1,d.remainingCards()); }
    @Test void invalidCard() { assertThrows(IllegalArgumentException.class,() -> new Card('S',15)); assertThrows(IllegalArgumentException.class,() -> new Card('X',2)); }
}
