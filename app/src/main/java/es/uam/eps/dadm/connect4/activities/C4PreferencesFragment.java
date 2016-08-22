/*
 * C4PreferencesFragment.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import es.uam.eps.dadm.connect4.R;

/**
 * Fragmento de las preferencias
 */
public class
C4PreferencesFragment extends PreferenceFragment{

    /**
     * Se crea cargando el fichero de diseño
     *
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
