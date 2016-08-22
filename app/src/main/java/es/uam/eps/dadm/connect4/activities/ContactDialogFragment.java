/*
 * ContactDialogFragment.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import es.uam.eps.dadm.connect4.R;

/**
 * Dialogo del desarrollador
 */
public class ContactDialogFragment extends DialogFragment{

    /**
     * Crea el dialogo
     *
     * @param savedInstanceState Bundle
     * @return creacion
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MenuActivity main = (MenuActivity) getActivity();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.app_name);
        alertDialogBuilder.setMessage(R.string.emailmsg);
        alertDialogBuilder.setPositiveButton(R.string.emailsug,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "manuel.reyes@estudiante.uam.es"));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                        startActivity(Intent.createChooser(emailIntent, "Click app"));
                    }
                });
        return alertDialogBuilder.create();
    }
}