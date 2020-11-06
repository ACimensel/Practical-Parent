package ca.cmpt276.flame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * AboutActivity: TODO add proper comment once activity created
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setupToolbar();
        fillUsageInstruction();
        fillReferences();
    }

    private void fillReferences() {
        TextView references = findViewById(R.id.aboutActivity_reference_body);
        references.setText(R.string.aboutActivity_references_data);
    }

    private void fillUsageInstruction() {
        TextView usageDescription = findViewById(R.id.aboutActivity_coin_flip_instructions_text);
        TextView timeoutDescription = findViewById(R.id.aboutActivity_timeout_instructions_text);
        TextView configureChildDescription = findViewById(R.id.aboutActivity_configure_child_instructions_text);
        usageDescription.setText(R.string.aboutActivity_coinFlipUsage);
        timeoutDescription.setText(R.string.aboutActivity_timeoutUsage);
        configureChildDescription.setText(R.string.aboutActivity_configureChildUsage);

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.about);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    protected static Intent makeIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }
}