/*
 * MenuFragment.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import es.uam.eps.dadm.connect4.R;

public class MenuFragment extends Fragment {
    private OnButtonSelectedListener listener;

    /**
     * Creador del fragmento
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View la vista
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        Button local = (Button)view.findViewById(R.id.localButton);
        Button stats = (Button)view.findViewById(R.id.statsButton);
        Button statsP = (Button)view.findViewById(R.id.statsPButton);
        Button help = (Button)view.findViewById(R.id.helpButton);
        Button set = (Button)view.findViewById(R.id.setButton);
        Button map = (Button)view.findViewById(R.id.mapButton);

        local.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonSelected("local");
            }
        });

        if(C4PreferencesActivity.getInternet(this.getActivity().getBaseContext())){
            stats.setVisibility(View.GONE);
            map.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapFragment.lon_ext=null;
                    MapFragment.lat_ext=null;
                    Intent intent = new Intent(getActivity().getBaseContext(), MapActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            map.setVisibility(View.GONE);
            stats.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onButtonSelected("stats");
                }

            });
        }

        statsP.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.onButtonSelected("statsP");
            }

        });

        help.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.onButtonSelected("help");
            }

        });

        set.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Lanzamos las preferencias*/
                Intent intent;
                intent = new Intent(getActivity().getBaseContext(), C4PreferencesActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * Interfaz a implementar para escuchar
     */
    public interface OnButtonSelectedListener {
        public void onButtonSelected(String link);
    }

    /**
     * Une un fragmento y actividad
     *
     * @param activity
     */
    @Override
    public void onAttach (Activity activity){
        super.onAttach(activity);
        if (activity instanceof OnButtonSelectedListener)
            listener = (OnButtonSelectedListener) activity;
        else {throw new ClassCastException(activity.toString() +" does not implement OnButtonSelectedListener");
        }
    }

    /**
     * Desune fragmento y actividad
     */
    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }
}