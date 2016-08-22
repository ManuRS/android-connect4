/*
 * MyService.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import es.uam.eps.dadm.connect4.R;

public class MyService extends Service {

    /*** METODOS DE UN SERVICIO ***/

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new BackgroundTask().execute("first", "second", "third");
        Toast.makeText(this, "Task in background", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    /*******/

    /**
     * Tarea
     * @param str string
     * @return tamaño cadena
     */
    private int task(String str){
        try{
            Thread.sleep(str.length()*500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return str.length();
    }

    /**
     * Ejecuta la tarea en segundo plano e informa de ello
     */
    private class BackgroundTask extends AsyncTask<String, Integer, Long>{

        /**
         * Tarea en 2 plano
         * @param strings
         * @return
         */
        protected Long doInBackground (String... strings){
            int count = strings.length;
            long total=0;
            for (int i=0; i<count; i++) {
                total += task(strings[i]);
                publishProgress((int) (((i+1) / (float) count) * 100));
            }
            return total;
        }

        /**
         * Informa del progrado
         * @param progress
         */
        protected void onProgressUpdate(Integer... progress){
            Toast.makeText(getBaseContext(), String.valueOf(progress[0]) + " % done",
                    Toast.LENGTH_LONG).show();
        }

        /**
         * Metodo ejecutado al finalizar
         * @param result
         */
        protected void onPostExecute(Long result){
            NotificationCompat.Builder noti =
                    new NotificationCompat.Builder(getBaseContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Work done!")
                            .setContentText(result+" characters");
            noti.setAutoCancel(true);

            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int id = 10;
            nm.notify(id, noti.build());

           // Toast.makeText(getBaseContext(), "Done " + result + " characters",Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }
}