/*
 * GCMReceiver.java
 */
package es.uam.eps.dadm.connect4.servidor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import es.uam.eps.dadm.connect4.R;
import es.uam.eps.dadm.connect4.activities.C4PreferencesActivity;
import es.uam.eps.dadm.connect4.activities.GameActivity;
import es.uam.eps.dadm.connect4.activities.SelectActivity;

/**
 * Clase encargada de tratar con GCM y lanzar notificaciones
 */
public class GCMReceiver extends BroadcastReceiver {
    /**
     * Cuando llega una notificacion de GCM este es el metodo invocado
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //Cogemos la info
        Bundle extras = intent.getExtras();
        Log.d("connect4GCM", ""+extras.toString());
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            if(extras.get("msgtype").equals("1")){
                //Avisamos si hay jugada nueva y no esta en las pantallas de juego
                if(!GameActivity.primerplano && !SelectActivity.primerplano){
                    //Creamos la notificacion
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setPriority(Notification.PRIORITY_HIGH)
                                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                                    .setLights(Color.WHITE, 4000, 4000)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle( context.getText(R.string.app_name) )
                                    .setContentText( context.getText(R.string.aviso) )
                                    .setAutoCancel(true);
                    if (Build.VERSION.SDK_INT >= 21) mBuilder.setVibrate(new long[0]);
                    Intent resultIntent;
                    TaskStackBuilder stackBuilder;
                    //Segun sea tablet o movil al pulsar ira a una u otra actividad
                    if(C4PreferencesActivity.getTabletCode(context)) {
                        resultIntent = new Intent(context, GameActivity.class);
                        stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(GameActivity.class);
                    }else {
                        resultIntent = new Intent(context, SelectActivity.class);
                        resultIntent.putExtra("fin", false);
                        stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(SelectActivity.class);
                    }
                    //Añadimos el intent
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    //notificamos
                    NotificationManager mNotificationManager =
                            (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    int mId=0;
                    mNotificationManager.notify(mId, mBuilder.build());
                }else{
                    GameActivity.refresh=true;
                    SelectActivity.refresh=true;
                }
            }
        }
    }
}

