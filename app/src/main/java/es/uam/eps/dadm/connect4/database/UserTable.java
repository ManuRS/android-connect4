/*
 * UserTable.java
 */
package es.uam.eps.dadm.connect4.database;

/**
 * Clase que define a la tabla de usuarios de la bd
 */
public class UserTable {
    // Unique id of each player
    public static final String ID = "id";

    // Name of the player
    public static final String NICK = "nick";

    // Password of the player
    public static final String PASSWORD = "password";

    // Rounds play by the player
    public static final String NUM_JUGADAS = "numjugadas";

    // Rounds win by the player
    public static final String NUM_GANADAS = "numganadas";

    // Name of the table
    static final String TABLE_NAME = "users";
}