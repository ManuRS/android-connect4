package es.uam.eps.dadm.connect4.database;

import java.util.ArrayList;

/**
 * Created
 * @author gonzalo on 27/01/16.
 */
public interface Repository {

    void open() throws Exception;
    void close();

    interface LoginRegisterCallback {
        void onLogin(String playerUuid);
        void onError(String error);
    }

    void login(String playername, String password, LoginRegisterCallback callback);
    void register(String playername, String password, LoginRegisterCallback callback);

    interface BooleanCallback {
        void onResponse(boolean ok);
    }

    void addResult(String playeruuid, int time, int points, String otherInfo, BooleanCallback callback);

    class Result {
        public String playerName;
        public int time;
        public int points;
        public String otherInfo;
    }

    interface ResultsCallback {
        void onResponse(ArrayList<Result> results);
        void onError(String error);
    }

    /**
     *   La implementación de esta función es libre, en el sentido que se pueden ignorar
     *   los parámetros o utilizarlos cómo se prefiera.
     *
     *   Una forma de usarlo podría ser:
     *      playeruuid :   Cuando se especifíque se devolveran los resultados ese usuario
     *                     solo
     *      orderByField : Cuando se especifíque se devolveran los resultados ordenados por
     *                     ese campo
     *      group:         Cuando se especifíque se devolveran los resultados agregados
     */
    void getResults(String playeruuid, String orderByField, String group, ResultsCallback callback);
}
