package com.neology.parking_neo.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by Cesar Segura on 08/03/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    String extra;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("BROADCAST", "ESTAMOS DENTRO DEL RECEIVER");

        extra = intent.getExtras().getString("extra");

        Log.i("BROADCAST", "VALOR DE LA LLAVE "+extra);

        Intent service_intent = new Intent(context, RingtonePlayingService.class);
        service_intent.putExtra("extra", extra);
        context.startService(service_intent);
    }

}
