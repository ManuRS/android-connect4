/*
 * Music.java
 */
package es.uam.eps.dadm.connect4.extra;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Clase utilizada para gestionar la musica
 *
 * @author Manuel Reyes
 */
public class Music {

    private static MediaPlayer player;

    /**
     * Comienza a reproducir un sonido
     *
     * @param context actividad asociada
     * @param id identificador del sonido
     */
    public static void play (Context context, int id){
        if(player==null) {
            player = MediaPlayer.create(context, id);
            player.setLooping(true);
            player.start();
        }else{
            player.start();
        }
    }

    /**
     * Pausa la reproduccion de un sonido
     *
     * @param context actividad asociada
     */
    public static void pause (Context context){
        if(player != null){
            player.pause();
        }
    }

    /**
     * Detiene la reproduccion de un sonido
     *
     * @param context actividad asociada
     */
    public static void stop (Context context){
        if(player != null){
            player.stop();
            player.release();
            player = null;
        }
    }

}