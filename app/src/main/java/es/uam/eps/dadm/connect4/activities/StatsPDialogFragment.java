/*
 * StatsPDialogFragment.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import es.uam.eps.dadm.connect4.R;
import es.uam.eps.dadm.connect4.database.Repository;
import es.uam.eps.dadm.connect4.database.RepositoryFactory;
import es.uam.eps.dadm.connect4.model.TableroConecta4;
import es.uam.eps.dadm.connect4.views.TableroConecta4View;

/**
 * Dialogo visor de una partida
 */
public class StatsPDialogFragment extends DialogFragment{

    static int num=0;
    String msg = "";
    AlertDialog.Builder alertDialogBuilder;
    TextView text;
    TableroConecta4View tablero;

    /**
     * Crea el dialogo
     *
     * @param savedInstanceState Bundle
     * @return dialogo
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /*Inflamos la interfaz interna y conseguimos sus elementos*/
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogfragment_statsp, null);
        text = (TextView) v.findViewById(R.id.textView12);
        tablero = (TableroConecta4View) v.findViewById(R.id.tableroConecta4View);

        /*Contruimos alert dialog*/
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        if(C4PreferencesActivity.getInternet(this.getActivity().getBaseContext())){
            alertDialogBuilder.setTitle(R.string.onlinepart);
        }
        alertDialogBuilder.setView(v);

        /*Tablero*/
        GameActivity.figurecode = Integer.parseInt(C4PreferencesActivity.getFigureCode(this.getActivity()));
        GameActivity.fondocode = C4PreferencesActivity.getFondoCode(this.getActivity());

        /*Construimos la informacion*/
        Repository repository = RepositoryFactory.createRepository(this.getActivity());
        Repository.ResultsCallback resultCallback = new Repository.ResultsCallback(){

            @Override
            public void onResponse(ArrayList<Repository.Result> results) {
                if (C4PreferencesActivity.getInternet(getActivity())){
                    msg = ""
                            + getString(R.string.rivales) + ": " + results.get(0).otherInfo + "\n"
                            + getString(R.string.fecha) + ": " + results.get(1).otherInfo + "\n"
                            + getString(R.string.hora) + ": " + results.get(2).otherInfo + "\n"
                            + getString(R.string.ganador) + ": " + results.get(5).otherInfo + "\n";

                }else{
                    alertDialogBuilder.setTitle(results.get(0).otherInfo);
                    msg = ""
                            + getString(R.string.fecha) + ": " + results.get(1).otherInfo + "\n"
                            + getString(R.string.duracion) + ": " + results.get(2).otherInfo + "(s)\n"
                            + getString(R.string.piezas) + ": " + results.get(3).otherInfo + "\n"
                            + getString(R.string.ganador) + ": " + results.get(5).otherInfo + "\n";
                }
                text.setText(msg);
                TableroConecta4 aux = new TableroConecta4();
                aux.stringToTablero(results.get(4).otherInfo);
                tablero.setTablero(aux);
            }

            @Override
            public void onError(String error) {
                Log.d("connect4", error);
            }
        };

        repository.getResults("//getPartida//", C4PreferencesActivity.getBusqueda(this.getActivity()), num+"", resultCallback);

        /*Boton*/
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


        return alertDialogBuilder.create();
    }
}
