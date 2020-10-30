package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.FlipManager;
import ca.cmpt276.flame.model.SoundPlayer;
import pl.droidsonroids.gif.GifImageView;

/**
 * FlipCoinActivity allows the user to flip a coin and random get a heads or tails, with a
 * coin spin sound played. The result is displayed as a text after coin flip animation is complete.
 * This activity shows which childs turn it is to pick heads or tails. If no children are configured
 * no text is displayed for turn of child. If children are configured, they are allowed to pick
 * whether they think the next result will be a heads or tails. Coin flip history is stored.
 */
public class FlipCoinActivity extends AppCompatActivity {
    FlipManager flipManager = FlipManager.getInstance();

    private boolean isCoinSpinning = false;
    private boolean isFirstSpin = true;
    private TextView childTurnTxt;
    private TextView coinResultTxt;
    private ImageView coinFrame;
    private GifImageView coinGif;
    private Button flipBtn;
    private Button histBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin);
        setupToolbar();

        childTurnTxt = findViewById(R.id.flipCoin_childturn);
        coinResultTxt = findViewById(R.id.flipCoin_resultTxt);
        coinFrame = findViewById(R.id.flipCoin_coinFrame);
        coinGif = findViewById(R.id.flipCoin_coinGif);
        flipBtn = findViewById(R.id.flipCoin_btnFlipCoin);
        histBtn = findViewById(R.id.flipCoin_btnHistory);

        coinResultTxt.setText("");
        updateChildTurnText();
        updateCoinFrame();
        setUpFlipCoinButton();
        setUpHistoryButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.flip_coin);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void updateChildTurnText() {
        Child child = flipManager.getTurnChild();
        if(child == null) {
            childTurnTxt.setText("");
        } else {
            if(isFirstSpin) {
                childTurnTxt.setText(getString(R.string.user_to_flip, child.getName()));
            } else {
                childTurnTxt.setText(getString(R.string.user_to_flip_next, child.getName()));
            }
        }
    }

    private void updateCoinFrame() {
        if ((flipManager.getLastCoinValue() == FlipManager.CoinSide.HEADS)) {
            coinFrame.setImageResource(R.drawable.coin_frame_head);
        } else {
            coinFrame.setImageResource(R.drawable.coin_frame_tail);
        }
    }

    private void setUpFlipCoinButton() {
        flipBtn.setOnClickListener(v -> {
            if(!isCoinSpinning) {
                flipCoin();
            }
        });
    }

    private void setUpHistoryButton() {
        histBtn.setOnClickListener(v -> {
            // TODO: DO SOMETHING ON CLICK ONCE HISTORY MANAGER IS IMPLEMENTED
        });
    }

    private void flipCoin() {
        isCoinSpinning = true;
        isFirstSpin = false;
        coinFrame.setImageDrawable(null);
        coinResultTxt.setText("");
        FlipManager.CoinSide lastVal = flipManager.getLastCoinValue();
        SoundPlayer.playCoinSpinSound(this);

        // TODO: pass in head/tail when radio buttons are implemented
        if(flipManager.doFlip(null) == FlipManager.CoinSide.HEADS) {
            if(lastVal == FlipManager.CoinSide.HEADS) {
                coinGif.setImageResource(R.drawable.h2h_2000ms);
            } else {
                coinGif.setImageResource(R.drawable.t2h_2000ms);
            }
        } else {
            if (lastVal == FlipManager.CoinSide.HEADS) {
                coinGif.setImageResource(R.drawable.h2t_2000ms);
            } else {
                coinGif.setImageResource(R.drawable.t2t_2000ms);
            }
        }

        final int DELAY_IN_MS = 2000;
        new Handler().postDelayed(() -> {
            if (flipManager.getLastCoinValue() == FlipManager.CoinSide.HEADS) {
                coinResultTxt.setText(R.string.heads);
            } else {
                coinResultTxt.setText(R.string.tails);
            }
            updateChildTurnText();
            isCoinSpinning = false;
        }, DELAY_IN_MS);
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, FlipCoinActivity.class);
    }
}