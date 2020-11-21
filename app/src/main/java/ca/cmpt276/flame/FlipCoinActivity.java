package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import ca.cmpt276.flame.model.BGMusicPlayer;
import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.FlipManager;
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

    private boolean isFirstSpin = true;
    protected TextView childTurnTxt;
    private TextView coinResultTxt;
    private ImageView coinFrame;
    private GifImageView coinGif;
    private Button flipBtn;
    private Button historyBtn;
    protected RadioGroup chooseSideRadioGroup;
    private MediaPlayer coinSpinSound;

    protected boolean childDisabled = false;
    private Button testBtn;

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
        chooseSideRadioGroup = findViewById(R.id.flipCoin_radioGroup);
        coinSpinSound = MediaPlayer.create(this, R.raw.coin_spin_sound);

        coinResultTxt.setText("");
        updateChildTurnText();
        updateCoinFrame();
        setUpRadioGroup();
        setUpOnclickListeners();

        // TODO remove this
        testBtn = findViewById(R.id.testButton);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flipManager.getTurnChild() == null) {
                    Toast.makeText(FlipCoinActivity.this, "null", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FlipCoinActivity.this, "" + flipManager.getTurnChild(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.flip_coin);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void updateChildTurnText() {
        Child child = flipManager.getTurnChild();
        if(child == null) {
            childTurnTxt.setVisibility(View.INVISIBLE);
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
            chooseSideRadioGroup.setVisibility(View.INVISIBLE);
            enableFlipCoinBtn();
        } else {
            findViewById(R.id.flipCoin_headsBtn).setOnClickListener(v -> enableFlipCoinBtn());
            findViewById(R.id.flipCoin_tailsBtn).setOnClickListener(v -> enableFlipCoinBtn());
            disableFlipCoinBtn();
        }
    }

    private void setUpOnclickListeners() {
        childTurnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment chooseFlipperFragment = new ChooseFlipperFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.FlipCoinContainer, chooseFlipperFragment, "Choose Flipper");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        flipBtn.setOnClickListener(v -> flipCoin());
        historyBtn.setOnClickListener(v -> startActivity(FlipHistoryActivity.makeIntent(this)));
    }

    private void flipCoin() {
        disableFlipCoinBtn();
        disableHistoryBtn();
        isFirstSpin = false;
        coinFrame.setImageDrawable(null);
        coinResultTxt.setText("");
        setRadioGroupButtons(false);

        int chosenCoinSide = chooseSideRadioGroup.getCheckedRadioButtonId();
        FlipManager.CoinSide coinSideBeforeSpin = flipManager.getLastCoinValue();
        FlipManager.CoinSide flipResult;

        // Pass into flipManager.doFlip() whether heads, tails, or nothing was chosen
        if(chosenCoinSide == R.id.flipCoin_headsBtn) {
            Toast.makeText(this, "Heads chosen", Toast.LENGTH_SHORT).show(); // TODO REMOVE
            flipResult = flipManager.doFlip(FlipManager.CoinSide.HEADS);
        } else if(chosenCoinSide == R.id.flipCoin_tailsBtn) {
            Toast.makeText(this, "Tails chosen", Toast.LENGTH_SHORT).show(); // TODO REMOVE
            flipResult = flipManager.doFlip(FlipManager.CoinSide.TAILS);
        } else {
            Toast.makeText(this, "No one chosen", Toast.LENGTH_SHORT).show(); // TODO REMOVE
            flipResult = flipManager.doFlip(null);
        }

        playCoinSpinSound();
        playCoinAnimation(coinSideBeforeSpin, flipResult);

        // After animation ends:
        // 1) show result as text, 2) update child turn text, 3) clear radio group if checked
        // 4) Enable clicking of the radio buttons, 5) allow user to spin coin again
        final int DELAY_IN_MS = 2000;
        new Handler().postDelayed(() -> {
            if (flipManager.getLastCoinValue() == FlipManager.CoinSide.HEADS) {
                coinResultTxt.setText(R.string.heads_result);
            } else {
                coinResultTxt.setText(R.string.tails_result);
            }

            updateChildTurnText();
            chooseSideRadioGroup.clearCheck();
            setRadioGroupButtons(true);

            enableHistoryBtn();
            if(flipManager.getTurnChild() == null) {
                enableFlipCoinBtn();
            } else {
                chooseSideRadioGroup.setVisibility(View.VISIBLE);
            }
        }, DELAY_IN_MS);
    }

    private void playCoinSpinSound() {
        if(coinSpinSound != null) {
            coinSpinSound.seekTo(0);
            coinSpinSound.start();
        }
    }

    private void playCoinAnimation(FlipManager.CoinSide coinSideBeforeSpin, FlipManager.CoinSide flipResult) {
        // Play 1 of 4 animations, depending on the starting coin value and the end value returned by doFlip()
        if(coinSideBeforeSpin == FlipManager.CoinSide.HEADS) {
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
    }

    void setRadioGroupButtons(boolean enable) {
        for (int i = 0; i < chooseSideRadioGroup.getChildCount(); i++) {
            chooseSideRadioGroup.getChildAt(i).setEnabled(enable);
        }
    }

    private void disableFlipCoinBtn() {
        flipBtn.setEnabled(false);
        flipBtn.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
    }

    protected void enableFlipCoinBtn() {
        flipBtn.setEnabled(true);
        flipBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private void disableHistoryBtn() {
        historyBtn.setEnabled(false);
        historyBtn.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
    }

    private void enableHistoryBtn() {
        historyBtn.setEnabled(true);
        historyBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onResume() {
        super.onResume();
        BGMusicPlayer.resumeBgMusic();
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, FlipCoinActivity.class);
    }
}