package edu.uic.cs342.poker.service;

/** Net figures exclude returned stakes; held Ante is neither a win nor a loss yet. */
public record RoundResult(String outcome, int anteNet, int playNet, int pairPlusNet,
                          int returnedStakes, int carriedAnte, int roundNet, int totalNet) { }
