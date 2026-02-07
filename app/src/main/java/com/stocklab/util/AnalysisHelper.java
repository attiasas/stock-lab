package com.stocklab.util;

import com.stocklab.model.Holding;
import com.stocklab.model.Profile;
import com.stocklab.model.Transaction;

import java.util.List;

/**
 * Computes simulation analysis: score, performance, stats.
 */
public final class AnalysisHelper {

    private AnalysisHelper() {
    }

    /**
     * Total value = current cash + sum(holding quantity * current price).
     */
    public static double computeTotalPortfolioValue(long currentCashCents, List<Holding> holdings) {
        double cash = currentCashCents / 100.0;
        double stocks = 0;
        for (Holding h : holdings) {
            stocks += h.quantity * (h.currentPrice > 0 ? h.currentPrice : h.averageCostPerShare);
        }
        return cash + stocks;
    }

    /**
     * Return on starting capital (percentage). startingCash in cents.
     */
    public static double computeReturnPercent(double totalValueNow, long startingCashCents) {
        if (startingCashCents <= 0) return 0;
        double start = startingCashCents / 100.0;
        return ((totalValueNow - start) / start) * 100.0;
    }

    /**
     * Simple score 0–100 based on return and consistency (number of trades).
     * Higher return and some activity = higher score.
     */
    public static int computeScore(double returnPercent, int transactionCount) {
        double returnScore = Math.max(0, Math.min(50, 25 + returnPercent)); // -25% -> 0, +25% -> 50
        double activityScore = Math.min(50, transactionCount * 2.5); // up to 20 trades = 50
        return (int) Math.round(Math.max(0, Math.min(100, returnScore + activityScore)));
    }

    /**
     * Difficulty label for profile.
     */
    public static String difficultyLabel(int difficulty) {
        switch (difficulty) {
            case 0: return "Easy";
            case 1: return "Normal";
            case 2: return "Hard";
            default: return "Normal";
        }
    }
}
