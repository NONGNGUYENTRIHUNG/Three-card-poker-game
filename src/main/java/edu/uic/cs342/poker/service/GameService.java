package edu.uic.cs342.poker.service;

import edu.uic.cs342.poker.model.*;
import edu.uic.cs342.poker.logic.ThreeCardLogic;
import java.util.*;

public class GameService {
    public enum Phase { BETTING, DECISIONS, RESULTS }
    private final Dealer dealer;
    private final Player[] players = {new Player(), new Player()};
    private final RoundResult[] results = new RoundResult[2];
    private List<Card> dealerHand = List.of();
    private Phase phase = Phase.BETTING;
    private int round = 1;
    public GameService() { this(new Dealer()); }
    public GameService(Dealer dealer) { this.dealer = Objects.requireNonNull(dealer); }
    public Player getPlayer(int i) { return players[i]; }
    public Phase getPhase() { return phase; }
    public int getRound() { return round; }
    public List<Card> getDealerHand() { return phase == Phase.RESULTS ? dealerHand : List.of(); }
    public boolean dealerQualifies() { require(Phase.RESULTS); return ThreeCardLogic.dealerQualifies(dealerHand); }
    public RoundResult getResult(int i) { require(Phase.RESULTS); return results[i]; }
    public static boolean validAnte(int amount) { return amount >= 5 && amount <= 25; }
    public static boolean validPairPlus(int amount) { return amount == 0 || validAnte(amount); }
    private void require(Phase expected) {
        if (phase != expected) throw new IllegalStateException("That action is unavailable during " + phase + ".");
    }
    public void deal(int ante1, int pp1, int ante2, int pp2) {
        require(Phase.BETTING);
        int[] antes = {ante1, ante2}, pps = {pp1, pp2};
        for (int i = 0; i < 2; i++) {
            if (!validAnte(antes[i]) || !validPairPlus(pps[i]))
                throw new IllegalArgumentException("Player " + (i+1) + ": Ante must be $5-$25; Pair Plus must be Off or $5-$25.");
            if (players[i].getCarriedAnte() > 0 && antes[i] != players[i].getCarriedAnte())
                throw new IllegalArgumentException("A carried Ante stays locked for the next round.");
        }
        dealer.reset();
        for (int i = 0; i < 2; i++) {
            players[i].setWagers(antes[i], pps[i]);
            players[i].setHand(dealer.dealHand());
        }
        dealerHand = List.copyOf(dealer.dealHand());
        phase = Phase.DECISIONS;
    }
    public void decide(int index, Player.Decision decision) {
        require(Phase.DECISIONS);
        Player player = players[index];
        if (decision == null || decision == Player.Decision.WAITING || player.getDecision() != Player.Decision.WAITING)
            throw new IllegalStateException("Choose Play or Fold once for each player.");
        player.decide(decision);
        if (Arrays.stream(players).noneMatch(p -> p.getDecision() == Player.Decision.WAITING)) settle();
    }
    private void settle() {
        boolean qualified = ThreeCardLogic.dealerQualifies(dealerHand);
        for (int i = 0; i < 2; i++) {
            Player p = players[i];
            int anteNet = 0, playNet = 0, ppNet = 0, returned = 0, carry = 0;
            String outcome;
            if (p.getDecision() == Player.Decision.FOLD) {
                anteNet = -p.getAnte(); ppNet = -p.getPairPlus(); outcome = "FOLDED";
            } else {
                if (p.getPairPlus() > 0) {
                    int profit = ThreeCardLogic.evalPPWinnings(new ArrayList<>(p.getHand()), p.getPairPlus());
                    ppNet = profit == 0 ? -p.getPairPlus() : profit;
                    if (profit > 0) returned += p.getPairPlus();
                }
                if (!qualified) {
                    returned += p.getPlay(); carry = p.getAnte(); outcome = "DEALER DOES NOT QUALIFY";
                } else {
                    int comparison = ThreeCardLogic.compareHands(new ArrayList<>(dealerHand), new ArrayList<>(p.getHand()));
                    if (comparison == 2) {
                        anteNet = p.getAnte(); playNet = p.getPlay(); returned += p.getAnte() + p.getPlay(); outcome = "WIN";
                    } else if (comparison == 1) {
                        anteNet = -p.getAnte(); playNet = -p.getPlay(); outcome = "LOSS";
                    } else { returned += p.getAnte() + p.getPlay(); outcome = "PUSH / TIE"; }
                }
            }
            p.setCarriedAnte(carry);
            int net = anteNet + playNet + ppNet;
            p.addWinnings(net);
            results[i] = new RoundResult(outcome, anteNet, playNet, ppNet, returned, carry, net, p.getTotalWinnings());
        }
        phase = Phase.RESULTS;
    }
    public void nextRound() {
        require(Phase.RESULTS);
        for (Player p : players) p.clearRound();
        Arrays.fill(results, null); dealerHand = List.of(); round++; phase = Phase.BETTING;
    }
}
