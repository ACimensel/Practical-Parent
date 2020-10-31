package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
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
 * whether they think the next result will be a heads or tails.
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
    private Button historyBtn;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin);
        setupToolbar();

        childTurnTxt = findViewById(R.id.flipCoin_txtChildturn);
        coinResultTxt = findViewById(R.id.flipCoin_txtResultTxt);
        coinFrame = findViewById(R.id.flipCoin_imgCoinFrame);
        coinGif = findViewById(R.id.flipCoin_gifCoin);
        flipBtn = findViewById(R.id.flipCoin_btnFlipCoin);
        historyBtn = findViewById(R.id.flipCoin_btnHistory);
        radioGroup = findViewById(R.id.flipCoin_radioGroup);

        coinResultTxt.setText("");
        updateChildTurnText();
        updateCoinFrame();
        setUpRadioGroup();
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
        if (flipManager.getLastCoinValue() == FlipManager.CoinSide.HEADS) {
            coinFrame.setImageResource(R.drawable.coin_frame_head);
        } else {
            coinFrame.setImageResource(R.drawable.coin_frame_tail);
        }
    }

    private void setUpRadioGroup() {
        if(flipManager.getTurnChild() == null) {
            radioGroup.removeAllViews();
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
        historyBtn.setOnClickListener(v -> {
            // TODO: DO SOMETHING ON CLICK ONCE HISTORY MANAGER IS IMPLEMENTED
        });
    }

    private void flipCoin() {
        final int DELAY_IN_MS = 2000;
        isCoinSpinning = true;
        isFirstSpin = false;
        coinFrame.setImageDrawable(null);
        coinResultTxt.setText("");
        SoundPlayer.playCoinSpinSound(this);

        int chosenCoinSide = radioGroup.getCheckedRadioButtonId();
        FlipManager.CoinSide flipResult;

        // Pass into flipManager.doFlip() whether heads, tails, or nothing was chosen
        if(chosenCoinSide == 1) {
            flipResult = flipManager.doFlip(FlipManager.CoinSide.HEADS);
        } else if(chosenCoinSide == 2) {
            flipResult = flipManager.doFlip(FlipManager.CoinSide.TAILS);
        } else {
            flipResult = flipManager.doFlip(null);
        }

        // Play 1 of 4 animations, depending on the starting coin value and the end value returned by doFlip()
        if(flipManager.getLastCoinValue() == FlipManager.CoinSide.HEADS) {
            if(flipResult == FlipManager.CoinSide.HEADS) {
                coinGif.setImageResource(R.drawable.h2h_2000ms);
            } else {
                coinGif.setImageResource(R.drawable.h2t_2000ms);
            }
        } else {
            if (flipResult == FlipManager.CoinSide.HEADS) {
                coinGif.setImageResource(R.drawable.t2h_2000ms);
            } else {
                coinGif.setImageResource(R.drawable.t2t_2000ms);
            }
        }

        // After animation ends:
        // 1) show result as text, 2) update child turn text
        // 3) clear radio group check, 4) allow user to spin again
        new Handler().postDelayed(() -> {
            if (flipManager.getLastCoinValue() == FlipManager.CoinSide.HEADS) {
                coinResultTxt.setText(R.string.heads_result);
            } else {
                coinResultTxt.setText(R.string.tails_result);
            }
            updateChildTurnText();
            radioGroup.clearCheck();
            isCoinSpinning = false;
        }, DELAY_IN_MS);
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, FlipCoinActivity.class);
    }
}