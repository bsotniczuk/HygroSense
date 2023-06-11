package pl.bsotniczuk.hygrosense;

import androidx.appcompat.app.AppCompatActivity;
import pl.bsotniczuk.hygrosense.controller.ToolbarMainActivityController;
import pl.bsotniczuk.hygrosense.controller.ToolbarSettingsController;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        new ToolbarSettingsController(this, this.findViewById(R.id.toolbar));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        startActivity(MainActivity.class);
        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        this.finishAfterTransition();
//        this.startActivity(intent);
//        this.finish();
    }
}