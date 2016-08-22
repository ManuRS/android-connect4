/*
 * MapActivity.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import es.uam.eps.dadm.connect4.R;

/**
 * Actividad cascaron para cargar un fragmento
 */
public class MapActivity extends AppCompatActivity {

    /**
     * Creador de la actividad
     *
     * @param savedInstanceState Bundle
     * @see <a href="http://developer.android.com/intl/es/training/basics/activity-lifecycle/index.html">Ciclo de vida</a>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }
}
