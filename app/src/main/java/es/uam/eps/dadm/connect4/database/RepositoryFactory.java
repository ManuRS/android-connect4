package es.uam.eps.dadm.connect4.database;

import android.content.Context;

import es.uam.eps.dadm.connect4.activities.C4PreferencesActivity;
import es.uam.eps.dadm.connect4.servidor.ServidorAdapter;

/**
 * @author gonzalo on 29/02/16.
 */
public class RepositoryFactory {

    private static Repository repository;

    public static Repository createRepository(Context context) {

        if (repository==null) {
            if(C4PreferencesActivity.getInternet(context))
                repository = new ServidorAdapter(context);
            else
                repository = new DataBaseAdapter(context);
        }

        try {
            repository.open();
        }
        catch (Exception e) {
            return null;
        }

        return repository;
    }

    public static void closeRepository(){

        if (repository!=null) {
            repository.close();
            repository=null;
        }

        return;

    }
}
