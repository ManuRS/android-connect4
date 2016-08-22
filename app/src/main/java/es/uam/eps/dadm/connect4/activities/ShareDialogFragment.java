/*
 * ContactDialogFragment.java
 */
package es.uam.eps.dadm.connect4.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.uam.eps.dadm.connect4.R;
import es.uam.eps.dadm.connect4.extra.MyArrayAdapter;

/**
 * Dialogo del share
 */
public class ShareDialogFragment extends DialogFragment{

    private ArrayList<Map<String, String>> contacts;
    private ListView contactsListView;

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
        View v = inflater.inflate(R.layout.dialogfragment_walist, null);
        contactsListView = (ListView) v.findViewById(R.id.listWhatsAppContacts);

        /*Dialogo*/
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.shareWA);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        /*Boton*/
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        if(!appInstalledOrNot(v.getContext().getPackageManager(), "com.whatsapp")){
            alertDialogBuilder.setMessage("No WhatsApp");
            Toast.makeText(v.getContext(), "No WhatsApp", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try{
                startActivity(goToMarket);
            }catch(Exception e) {

            }
            return alertDialogBuilder.create();
        }

        /*Permisos*/
        // Si la version es mayor que M debemos pedir permiso al usuario. Tambien se mira si ya se tiene porque ya se pidio
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 124);
            // El sistema respondera en la funcion onRequestPermissionsResult(int, String[], int[])
            return alertDialogBuilder.create();
        }
        // Version anterior que no necesita pedir permiso

        /*Creamos lista*/
        String[] from = { "name" , "number" };
        int[] to = { R.id.txtName, R.id.txtNumber };
        contacts = getWA(v.getContext().getContentResolver());
        SimpleAdapter adapter = new SimpleAdapter(v.getContext(), contacts, R.layout.wa_list_item, from, to);
        contactsListView.setAdapter(adapter);

        /*Abrir conversacion que toque*/
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                try {
                    Uri uri = Uri.parse("smsto:" + contacts.get(arg2).get("number"));
                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                    i.setPackage("com.whatsapp");
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Log.d("connect4", e.getMessage());
                }
            }
        });

        /*Seteamos la vista del dialog con la view*/
        alertDialogBuilder.setView(v);
        return alertDialogBuilder.create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Para M y su sistema de permisos en tiempo real, nos avisa con el resultado
        if (requestCode == 124) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Rellamamos
                onCreateDialog(null);
            } else {
                Toast.makeText(this.getActivity(), "No permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<Map<String, String>> getWA(ContentResolver cr){
        ArrayList<Map<String, String>> list = new ArrayList<Map<String,String>>();

        final String[] projection={
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.MIMETYPE,
                "account_type",
                ContactsContract.Data.DATA3,
        };
        final String selection= ContactsContract.Data.MIMETYPE+" =? and account_type=?";
        final String[] selectionArgs = {
                "vnd.android.cursor.item/vnd.com.whatsapp.profile",
                "com.whatsapp"
        };
        Cursor c = cr.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        while(c.moveToNext()){
            String id=c.getString(c.getColumnIndex(ContactsContract.Data.CONTACT_ID));
            String number=c.getString(c.getColumnIndex(ContactsContract.Data.DATA3));
            String name="";
            Cursor mCursor=cr.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                    ContactsContract.Contacts._ID + " =?",
                    new String[]{id},
                    null);
            while(mCursor.moveToNext()){
                name=mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }
            mCursor.close();
            list.add(putData(name, number));
        }
        c.close();
        return list;
    }

    private HashMap<String, String> putData(String name, String number) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("name", name);
        item.put("number", number);
        return item;
    }

    private boolean appInstalledOrNot(PackageManager pm, String uri) {
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}