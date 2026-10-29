package edu.uic.cs342.poker.model;

import java.util.*;

public class Dealer {
    private final Deck deck;
    public Dealer() { this(new Deck()); }
    public Dealer(Deck deck) { this.deck = Objects.requireNonNull(deck); }
    public void reset() { deck.reset(); }
    public ArrayList<Card> dealHand() {
        if (deck.size() < 3) throw new IllegalStateException("Not enough cards for a hand.");
        ArrayList<Card> hand = new ArrayList<>();
        for (int i = 0; i < 3; i++) hand.add(deck.draw());
        return hand;
    }
    public int remainingCards() { return deck.size(); }
}
