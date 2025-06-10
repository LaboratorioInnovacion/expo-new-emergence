package com.desbloqueocelusa.front;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class LocationService extends Service {
    private static final String CHANNEL_ID = "LocationServiceChannel";
    private static final int NOTIF_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
        startForeground(NOTIF_ID, buildNotification());
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(
                CHANNEL_ID,
                "Servicio en primer plano",
                NotificationManager.IMPORTANCE_LOW
            );
            chan.setDescription("Mantiene la app activa para recibir eventos");
            ((NotificationManager) getSystemService(NotificationManager.class))
                .createNotificationChannel(chan);
        }
    }

    private Notification buildNotification() {
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i,
            PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("HeadsetLocationApp")
            .setContentText("Servicio activo en segundo plano")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pi)
            .setOngoing(true)
            .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Si el sistema mata el servicio, lo reinicia.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
