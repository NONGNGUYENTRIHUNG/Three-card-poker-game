package edu.uic.cs342.poker.model;

import java.util.*;

public class Deck {
    private final ArrayList<Card> cards = new ArrayList<>();
    private final Random random;
    public Deck() { this(new Random()); }
    public Deck(Random random) { this.random = Objects.requireNonNull(random); reset(); }
    /** Restores all 52 cards and shuffles, including after a partially dealt round. */
    public void reset() {
        cards.clear();
        for (char suit : new char[]{'C', 'D', 'H', 'S'})
            for (int value = 2; value <= 14; value++) cards.add(new Card(suit, value));
        shuffle();
    }
    public void shuffle() { Collections.shuffle(cards, random); }
    public Card draw() {
        if (cards.isEmpty()) throw new IllegalStateException("The deck is empty.");
        return cards.removeLast();
    }
    public int size() { return cards.size(); }
    public List<Card> getCards() { return List.copyOf(cards); }
}
