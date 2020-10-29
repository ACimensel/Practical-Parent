package ca.cmpt276.flame.model;

import java.util.Date;
import java.util.UUID;

/**
 * FLipHistory manages a coin flip history entry. It contains the UUID of
 * the Child who flipped the coin, what the result of the flip was, whether
 * the child won or not and the date of the flip.
 */
public class FlipHistory {
    private final UUID childUuid;
    private final FlipManager.CoinSide result;
    private final Boolean won;
    private final Date date;

    public FlipHistory(UUID childUuid, FlipManager.CoinSide result, Boolean won) {
        this.childUuid = childUuid;
        this.result = result;
        this.won = won;
        this.date = new Date();
    }

    public UUID getChildUuid() {
        return childUuid;
    }

    public FlipManager.CoinSide getResult() {
        return result;
    }

    public Boolean wasWon() {
        return won;
    }

    public Date getDate() {
        return date;
    }
}
