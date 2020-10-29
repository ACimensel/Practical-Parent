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

import ca.cmpt276.flame.model.CoinFlipManager;
import ca.cmpt276.flame.model.SoundPlayer;
import pl.droidsonroids.gif.GifImageView;

/**
 * FlipCoinActivity: TODO add proper comment once activity created
 */
public class FlipCoinActivity extends AppCompatActivity {
    private boolean coinSpinning = false;
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

        coinResultTxt = findViewById(R.id.flipCoin_resultTxt);
        coinFrame = findViewById(R.id.flipCoin_coinFrame);
        coinGif = findViewById(R.id.flipCoin_coinGif);
        flipBtn = findViewById(R.id.flipCoin_btnFlipCoin);
        histBtn = findViewById(R.id.flipCoin_btnHistory);

        coinResultTxt.setText("");
        updateCoinFrame();
        setUpFlipCoinButton();
        setUpHistoryButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.flip_coin);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void updateCoinFrame() {
        if ((CoinFlipManager.getLastCoinValue() == CoinFlipManager.coinValue.HEADS)) {
            coinFrame.setImageResource(R.drawable.coin_frame_head);
        } else {
            coinFrame.setImageResource(R.drawable.coin_frame_tail);
        }
    }

    private void setUpFlipCoinButton() {
        flipBtn.setOnClickListener(v -> {
            if(!coinSpinning) { flipCoin(); }
        });
    }

    private void setUpHistoryButton() {
        histBtn.setOnClickListener(v -> {
            // TODO: DO SOMETHING ON CLICK ONCE HISTORY MANAGER IS IMPLEMENTED
        });
    }

    private void flipCoin() {
        coinSpinning = true;
        coinFrame.setImageDrawable(null);
        coinResultTxt.setText("");
        SoundPlayer.playCoinSpinSound(this);

        if(CoinFlipManager.getRandomCoinValue() == CoinFlipManager.coinValue.HEADS) {
            if(CoinFlipManager.getLastCoinValue() == CoinFlipManager.coinValue.HEADS) {
                coinGif.setImageResource(R.drawable.h2h_2000ms);
            } else {
                coinGif.setImageResource(R.drawable.t2h_2000ms);
                CoinFlipManager.setLastCoinValue(CoinFlipManager.coinValue.HEADS);
            }
        }
        else {
            if (CoinFlipManager.getLastCoinValue() == CoinFlipManager.coinValue.HEADS) {
                coinGif.setImageResource(R.drawable.h2t_2000ms);
                CoinFlipManager.setLastCoinValue(CoinFlipManager.coinValue.TAILS);
            } else {
                coinGif.setImageResource(R.drawable.t2t_2000ms);
            }
        }

        new Handler().postDelayed(() -> {
            if (CoinFlipManager.getLastCoinValue() == CoinFlipManager.coinValue.HEADS) { coinResultTxt.setText(R.string.heads); }
            else { coinResultTxt.setText(R.string.tails); }
            coinSpinning = false;
        }, 2000);
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, FlipCoinActivity.class);
    }
}