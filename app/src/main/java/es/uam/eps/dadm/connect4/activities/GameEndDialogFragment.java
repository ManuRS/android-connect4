/*
 * GameDialogFragment.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import es.uam.eps.dadm.connect4.R;
import es.uam.eps.multij.ExcepcionJuego;

/**
 * Dialogo de fin de partida
 */
public class GameEndDialogFragment extends DialogFragment{

    /**
     * Crea el dialogo
     *
     * @param savedInstanceState Bundle
     * @return dialogo
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        try {
            final GameActivity main = (GameActivity) getActivity();
            alertDialogBuilder.setMessage(main.winner);
        }catch (Exception e){
            final SelectActivity main = (SelectActivity) getActivity();
            alertDialogBuilder.setMessage(main.winner);
        }

        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return alertDialogBuilder.create();
    }
}