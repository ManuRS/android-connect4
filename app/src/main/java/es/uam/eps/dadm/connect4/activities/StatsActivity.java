/*
 * StatsActivity.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import es.uam.eps.dadm.connect4.R;

/**
 * Actividad cascaron para cargar un fragmento
 */
public class StatsActivity extends AppCompatActivity {

    /**
     * Creador de la actividad
     *
     * @param savedInstanceState Bundle
     * @see <a href="http://developer.android.com/intl/es/training/basics/activity-lifecycle/index.html">Ciclo de vida</a>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*Esta es una actividad cascaron de un fragmento, asi que vamos a cargarlo*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        FragmentManager fm = getFragmentManager();

        if (fm.findFragmentById(R.id.fragmentStats) == null) {
            Fragment fr = new StatsFragment();
            fm.beginTransaction().add(R.id.fragmentStats, fr).commit();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
