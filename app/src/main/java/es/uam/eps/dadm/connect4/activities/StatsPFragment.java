/*
 * StatsPFragment.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import es.uam.eps.dadm.connect4.R;
import es.uam.eps.dadm.connect4.database.Repository;
import es.uam.eps.dadm.connect4.database.RepositoryFactory;
import es.uam.eps.dadm.connect4.extra.MyArrayAdapter;

/**
 * Fragmento de la pantalla de partidas stats
 */
public class StatsPFragment extends Fragment {

    private Repository repository;
    Repository.ResultsCallback resultCallback;
    View view;

    /**
     * Creador del fragmento
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View la vista
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statsp, container, false);

        repository = RepositoryFactory.createRepository(view.getContext());
        resultCallback = new Repository.ResultsCallback(){

            /**
             * Implementacion de la interfaz Repository
             *
             * @param results array de Result
             * @see es.uam.eps.dadm.connect4.database.Repository
             */
            @Override
            public void onResponse(ArrayList<Repository.Result> results) {
                String[] cadenas = new String[results.size()];
                int i=0;
                for (Repository.Result r: results) {
                    cadenas[i++]=r.otherInfo;
                }
                MyArrayAdapter adapter3 = new MyArrayAdapter(view.getContext(), cadenas);
                ListView lv3 = (ListView)view.findViewById(R.id.list_view1);
                lv3.setAdapter(adapter3);
            }

            /**
             * Implementacion de la interfaz Repository
             *
             * @param error explicacion del error sucedido
             * @see es.uam.eps.dadm.connect4.database.Repository
             */
            @Override
            public void onError(String error) {
                Toast.makeText(StatsPFragment.this.getActivity(), "Stats error", Toast.LENGTH_SHORT).show();
            }

        };

        if(C4PreferencesActivity.getInternet(this.getActivity().getBaseContext())){
            repository.getResults(
                    C4PreferencesActivity.getUserNumber(this.getActivity().getBaseContext()),
                    C4PreferencesActivity.getBusqueda(view.getContext()),
                    "false",
                    resultCallback);
        }else {
            repository.getResults(null, C4PreferencesActivity.getBusqueda(view.getContext()), null, resultCallback);
        }

        ListView lv = (ListView)view.findViewById(R.id.list_view1);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Object listItem = list.getItemAtPosition(position);
                StatsPDialogFragment.num=position;
                new StatsPDialogFragment().show(getFragmentManager(), "ALERT DIALOG");
            }
        });

        return view;
    }

}




