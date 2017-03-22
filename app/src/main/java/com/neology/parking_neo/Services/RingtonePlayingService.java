package com.neology.parking_neo.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.neology.parking_neo.MainActivity;
import com.neology.parking_neo.R;

/**
 * Created by Cesar Segura on 08/03/2017.
 */

public class RingtonePlayingService extends Service {

    MediaPlayer mediaPlayer;
    String state;
    private int startId;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SERVICE", "EN EL SERVICIO");
        state = intent.getExtras().getString("extra");
        Log.i("SERVICE", "RINGTONE EXTRA ES " + state);
        assert state != null;
        switch (state) {
            case "alarm on":
                this.startId = 1;
                break;
            case "alarm off":
                this.startId = 0;
                break;
            default:
                this.startId = 0;
                break;
        }

        sendNotification();

        /*
        if(!this.isRunning && startId == 1) {
            Log.i("MUSIC", "NO HAY MUSICA, ASI QUE EMPIEZA");
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
            mediaPlayer.start();
            this.isRunning = true;
            this.startId = 0;
            sendNotification();
        } else if(this.isRunning && startId == 0) {
            Log.i("MUSIC", "SI HAY MUSICA, ASI QUE TERMINA");
            mediaPlayer.stop();
            mediaPlayer.reset();
            this.isRunning = false;
            this.startId = 0;
        } else if(!this.isRunning && startId == 0) {
            Log.i("MUSIC", "NO HAY MUSICA, ASI QUE TERMINA");
            this.isRunning = false;
            this.startId = 0;
        } else if(this.isRunning && startId == 1) {
            Log.i("MUSIC", "SI HAY MUSICA, ASI QUE EMPIEZA");
            this.isRunning = true;
            this.startId = 1;
        } else {

        }
        */

        return START_NOT_STICKY;
    }

    private void sendNotification() {
        NotificationManager notifyManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        Intent intent_pagro_fragment = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent_pagro_fragment, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("ALARMA!")
                .setContentText("Click me!")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setDefaults(
                        Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE
                                | Notification.DEFAULT_LIGHTS
                )
                .build();

        notifyManager.notify(0, notification);

        //Vibrator vibrator =(Vibrator) getSystemService(this.getApplicationContext().VIBRATOR_SERVICE);
        //vibrator.vibrate(1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
    }
}
