package edu.uic.cs342.poker.logic;

import edu.uic.cs342.poker.model.Card;
import java.util.*;

/** Pure game rules; no JavaFX dependencies. Lower nonzero category is stronger. */
public final class ThreeCardLogic {
    private ThreeCardLogic() { }
    private static int[] values(ArrayList<Card> hand) {
        if (hand == null || hand.size() != 3 || hand.contains(null) || new HashSet<>(hand).size() != 3)
            throw new IllegalArgumentException("A hand needs three distinct cards.");
        return hand.stream().mapToInt(Card::value).sorted().toArray();
    }
    private static int straightHigh(int[] v) {
        if (v[0] == 2 && v[1] == 3 && v[2] == 14) return 3; // Ace-low straight
        return v[0] + 1 == v[1] && v[1] + 1 == v[2] ? v[2] : 0;
    }
    /** 0 high card, 1 straight flush, 2 trips, 3 straight, 4 flush, 5 pair. */
    public static int evalHand(ArrayList<Card> hand) {
        int[] v = values(hand);
        boolean flush = hand.stream().map(Card::suit).distinct().count() == 1;
        boolean straight = straightHigh(v) > 0;
        if (flush && straight) return 1;
        if (v[0] == v[2]) return 2;
        if (straight) return 3;
        if (flush) return 4;
        if (v[0] == v[1] || v[1] == v[2]) return 5;
        return 0;
    }
    /** Profit only, excluding the returned original stake; zero for no winning category. */
    public static int evalPPWinnings(ArrayList<Card> hand, int bet) {
        if (bet < 0) throw new IllegalArgumentException("Negative wager.");
        int multiplier = switch (evalHand(hand)) { case 1 -> 40; case 2 -> 30; case 3 -> 6; case 4 -> 3; case 5 -> 1; default -> 0; };
        return bet * multiplier;
    }
    private static int[] key(ArrayList<Card> hand) {
        int[] v = values(hand);
        int category = evalHand(hand);
        int strength = category == 0 ? 0 : 6 - category;
        if (category == 1 || category == 3) return new int[]{strength, straightHigh(v), 0, 0};
        if (category == 5) {
            int pair = v[0] == v[1] ? v[0] : v[1];
            int kicker = v[0] == v[1] ? v[2] : v[0];
            return new int[]{strength, pair, kicker, 0};
        }
        return new int[]{strength, v[2], v[1], v[0]};
    }
    /** 0 tie, 1 dealer wins, 2 player wins. Suits never break ties. */
    public static int compareHands(ArrayList<Card> dealer, ArrayList<Card> player) {
        int comparison = Arrays.compare(key(dealer), key(player));
        return comparison == 0 ? 0 : comparison > 0 ? 1 : 2;
    }
    public static boolean dealerQualifies(List<Card> hand) {
        ArrayList<Card> copy = new ArrayList<>(hand);
        return evalHand(copy) != 0 || Arrays.stream(values(copy)).max().orElse(0) >= 12;
    }
    public static String handName(List<Card> hand) {
        return switch (evalHand(new ArrayList<>(hand))) {
            case 1 -> "Straight Flush"; case 2 -> "Three of a Kind"; case 3 -> "Straight";
            case 4 -> "Flush"; case 5 -> "Pair"; default -> "High Card";
        };
    }
}
