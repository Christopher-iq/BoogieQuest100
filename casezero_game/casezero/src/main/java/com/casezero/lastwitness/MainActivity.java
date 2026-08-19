package com.casezero.lastwitness;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.EditText;

public class MainActivity extends Activity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Attach the content/decor view before touching WindowInsetsController.
        // On Android 11+ requesting the controller before the decor exists can
        // throw a framework NullPointerException and kill the app at launch.
        gameView = new GameView(this);
        setContentView(gameView);
        scheduleImmersiveMode();
    }

    private void scheduleImmersiveMode() {
        try {
            final View decor = getWindow().getDecorView();
            if (decor != null) decor.post(this::hideSystemUiSafely);
        } catch (Throwable ignored) {
            // System bars are cosmetic; they must never prevent the game opening.
        }
    }

    private void hideSystemUiSafely() {
        try {
            final Window window = getWindow();
            final View decor = window.getDecorView();
            if (decor == null) return;

            if (Build.VERSION.SDK_INT >= 30) {
                window.setDecorFitsSystemWindows(false);
                final WindowInsetsController controller = decor.getWindowInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.systemBars());
                    controller.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    );
                } else {
                    // The decor can be present a frame before its controller.
                    // Retry later instead of crashing.
                    decor.postDelayed(this::hideSystemUiSafely, 120L);
                }
            } else {
                applyLegacyImmersive(decor);
            }
        } catch (Throwable ignored) {
            // Fallback for vendor-specific window implementations.
            try {
                View decor = getWindow().getDecorView();
                if (decor != null) applyLegacyImmersive(decor);
            } catch (Throwable ignoredAgain) {
                // Leaving system bars visible is preferable to terminating the app.
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void applyLegacyImmersive(View decor) {
        decor.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) scheduleImmersiveMode();
    }

    public void promptDetectiveName(String current) {
        final EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setText(current == null ? "" : current);
        input.setSelectAllOnFocus(true);
        new AlertDialog.Builder(this)
            .setTitle("Detective name")
            .setMessage("This name appears in case files and the season ending.")
            .setView(input)
            .setPositiveButton("SAVE", (d, w) -> {
                String value = input.getText().toString().trim();
                if (value.length() < 2) value = "Detective";
                gameView.setDetectiveName(value);
            })
            .setNegativeButton("CANCEL", null)
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) gameView.resumeAudio();
        scheduleImmersiveMode();
    }

    @Override
    protected void onPause() {
        if (gameView != null) gameView.pauseAudio();
        super.onPause();
    }
}
