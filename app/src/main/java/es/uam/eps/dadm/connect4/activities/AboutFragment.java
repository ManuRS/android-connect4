/*
 * AboutFragment.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import es.uam.eps.dadm.connect4.R;

/**
 * Fragmento de la pantalla de ayuda
 */
public class AboutFragment extends Fragment {

    /**
     * Creador del fragmento
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View la vista
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        Button w = (Button)view.findViewById(R.id.wiki);

        w.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://en.wikipedia.org/wiki/Connect_Four"));
                startActivity(i);
            }
        });

        TextView tx = (TextView)view.findViewById(R.id.textView10);
        tx.append("\nNick: "+C4PreferencesActivity.getUserCode(view.getContext()));

        return view;
    }
}



