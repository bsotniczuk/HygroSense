package pl.bsotniczuk.hygrosense.controller;

import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import pl.bsotniczuk.hygrosense.MainActivity;
import pl.bsotniczuk.hygrosense.R;

public class ToolbarController {

    public ToolbarController(MainActivity mainActivity, Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                Toast.makeText(mainActivity.getApplicationContext(),
                                "Settings item clicked",
                                Toast.LENGTH_SHORT)
                        .show();
            }
            return false;
        });
    }
}
