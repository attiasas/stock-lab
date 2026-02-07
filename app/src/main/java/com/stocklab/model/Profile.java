package com.stocklab.model;

/**
 * Domain model for a simulation profile.
 */
public class Profile {

    public final long id;
    public final String name;
    public final long createdAt;
    public final long startingCash;
    public final String currency;
    public final int difficulty; // 0 easy, 1 normal, 2 hard
    public final long currentCash;

    public Profile(long id, String name, long createdAt, long startingCash,
                   String currency, int difficulty, long currentCash) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.startingCash = startingCash;
        this.currency = currency;
        this.difficulty = difficulty;
        this.currentCash = currentCash;
    }
}
