package ca.cmpt276.flame.model;

import java.util.Random;

public class CoinFlipManager {
    public enum coinValue {
        TAILS,
        HEADS
    }

    private static coinValue lastCoinValue = coinValue.TAILS;

    private CoinFlipManager() {
        // static class: prevent other classes from creating new ones
    }

    public static coinValue getRandomCoinValue() {
        int randVal = (new Random()).nextInt(2);
        return (randVal == 1) ? coinValue.HEADS : coinValue.TAILS;
    }

    public static void setLastCoinValue(coinValue lastCoinValue) {
        CoinFlipManager.lastCoinValue = lastCoinValue;
    }

    public static coinValue getLastCoinValue() {
        return lastCoinValue;
    }
}
