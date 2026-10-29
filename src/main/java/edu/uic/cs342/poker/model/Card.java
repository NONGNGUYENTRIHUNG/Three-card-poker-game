package edu.uic.cs342.poker.model;

/** Immutable card. Values are 2 through 14, with Ace = 14. */
public record Card(char suit, int value) {
    public Card {
        suit = Character.toUpperCase(suit);
        if ("CDHS".indexOf(suit) < 0 || value < 2 || value > 14)
            throw new IllegalArgumentException("Invalid card suit or value.");
    }
    public String rank() {
        return switch (value) { case 11 -> "J"; case 12 -> "Q"; case 13 -> "K"; case 14 -> "A"; default -> "" + value; };
    }
    public String symbol() {
        return switch (suit) { case 'C' -> "♣"; case 'D' -> "♦"; case 'H' -> "♥"; default -> "♠"; };
    }
    @Override public String toString() { return rank() + symbol(); }
}
