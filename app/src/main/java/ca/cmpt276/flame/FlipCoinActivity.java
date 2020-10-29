package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * FlipCoinActivity: TODO add proper comment once activity created
 */
public class FlipCoinActivity extends AppCompatActivity {
    // TODO: MOVE THESE TO COIN FLIP MANAGER
    private final int SPINS = 1;
    private boolean coinSpinning = false;
    private ImageView coin;
    private Button btn;

    private final static int[] frameIdArr = new int[] {
            R.drawable.coin_frame_0, R.drawable.coin_frame_1, R.drawable.coin_frame_2,
            R.drawable.coin_frame_3, R.drawable.coin_frame_4, R.drawable.coin_frame_5,
            R.drawable.coin_frame_6, R.drawable.coin_frame_7, R.drawable.coin_frame_8,
            R.drawable.coin_frame_9, R.drawable.coin_frame_10, R.drawable.coin_frame_11,

            R.drawable.coin_frame_12, R.drawable.coin_frame_13, R.drawable.coin_frame_14,
            R.drawable.coin_frame_15, R.drawable.coin_frame_16, R.drawable.coin_frame_17,
            R.drawable.coin_frame_18, R.drawable.coin_frame_19, R.drawable.coin_frame_20,
            R.drawable.coin_frame_21, R.drawable.coin_frame_22, R.drawable.coin_frame_23,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin);
        setupToolbar();

        coin = findViewById(R.id.flipCoin_coin);
        btn = findViewById(R.id.flipCoin_btnFlipCoin);
        setUpFlipCoinButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.flip_coin);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void setUpFlipCoinButton() {

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!coinSpinning) { flipCoin(); }
            }
        });
    }

    private void flipCoin() {
        coinSpinning = true;

        Toast.makeText(FlipCoinActivity.this, "5sec elapsed!", Toast.LENGTH_SHORT).show();
        //coin.setImageDrawable(getResources().getDrawable(R.drawable.coin_frame_12));

        for(int frame = 0; frame < frameIdArr.length; frame++) {

            coin.setImageDrawable(getResources().getDrawable(frameIdArr[frame]));

            /*
            if (i == NUM_COIN_FRAMES - 1) {
                i = 0;
                revs++;
            }
            else { i++; }*/
        }

        coinSpinning = false;
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, FlipCoinActivity.class);
    }
}