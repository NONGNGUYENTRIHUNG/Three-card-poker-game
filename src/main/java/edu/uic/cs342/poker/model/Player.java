package edu.uic.cs342.poker.model;

import java.util.*;

/** Stores one player's round information and net winnings for the session. */
public class Player {
    public enum Decision { WAITING, PLAY, FOLD }
    private List<Card> hand = List.of();
    private int ante, pairPlus, play, totalWinnings, carriedAnte;
    private Decision decision = Decision.WAITING;
    public List<Card> getHand() { return hand; }
    public int getAnte() { return ante; }
    public int getPairPlus() { return pairPlus; }
    public int getPlay() { return play; }
    public int getTotalWinnings() { return totalWinnings; }
    public int getCarriedAnte() { return carriedAnte; }
    public Decision getDecision() { return decision; }
    public void setWagers(int ante, int pairPlus) { this.ante = ante; this.pairPlus = pairPlus; }
    public void setHand(List<Card> hand) { this.hand = List.copyOf(hand); }
    public void decide(Decision decision) { this.decision = decision; play = decision == Decision.PLAY ? ante : 0; }
    public void addWinnings(int amount) { totalWinnings += amount; }
    public void setCarriedAnte(int amount) { carriedAnte = amount; }
    public void clearRound() { hand = List.of(); ante = carriedAnte; pairPlus = play = 0; decision = Decision.WAITING; }
}
