package com.desbloqueocelusa.HeadsetLocationApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.util.Log;

public class MediaButtonReceiver extends BroadcastReceiver {
    private static long[] tapTimestamps = new long[3];
    private static int tapCount = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK ||
                    event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {

                    long now = System.currentTimeMillis();
                    tapTimestamps[tapCount % 3] = now;
                    tapCount++;

                    if (tapCount >= 3) {
                        long first = tapTimestamps[(tapCount - 3) % 3];
                        if (now - first < 1500) {
                            Log.d("MediaButton", "Triple pulsación detectada");
                            Intent serviceIntent = new Intent(context, LocationService.class);
                            context.startForegroundService(serviceIntent);
                            tapCount = 0;
                        }
                    }
                }
            }
        }
    }
}
