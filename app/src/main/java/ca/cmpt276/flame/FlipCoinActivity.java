package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
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
    private final int NUM_COIN_FRAMES = 24;
    private final int SPINS = 4;
    private boolean coinSpinning = false;
    private ImageView coin;
    private Button btn;

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
        for(int i = 0, revs = 0; revs < SPINS;) {
            String frameName = "coin_frame_" + i;
            Toast.makeText(FlipCoinActivity.this,frameName, Toast.LENGTH_SHORT).show();

            if (i == NUM_COIN_FRAMES - 1) {
                i = 0;
                revs++;
            }
            else {
                i++;
            }
        }
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, FlipCoinActivity.class);
    }
}