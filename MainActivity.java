package com.bhuvan.skylinewallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(48, 64, 48, 48);

        TextView title = new TextView(this);
        title.setText("Skyline Live Wallpaper");
        title.setTextSize(26);
        title.setPadding(0, 0, 0, 24);
        root.addView(title);

        TextView info = new TextView(this);
        info.setText("The wallpaper uses your phone's local time and automatically changes between sunrise, day, sunset and moonlit night. No internet connection is required.");
        info.setTextSize(16);
        info.setPadding(0, 0, 0, 36);
        root.addView(info);

        Button set = new Button(this);
        set.setText("SET LIVE WALLPAPER");
        set.setOnClickListener(v -> {
            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(this, SkylineWallpaperService.class));
            startActivity(intent);
        });
        root.addView(set);

        TextView schedule = new TextView(this);
        schedule.setText("\nDefault cycle\n05:00–07:00  Dawn / sunrise\n07:00–16:30  Day\n16:30–19:00  Sunset / golden hour\n19:00–05:00  Moonlit night\n\nThe schedule is deliberately battery-friendly and follows the device clock.");
        schedule.setTextSize(15);
        root.addView(schedule);

        setContentView(root);
    }
}
