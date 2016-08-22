/*
 * RoundTable.java
 */
package es.uam.eps.dadm.connect4.database;

/**
 * Clase que define a la tabla de partidas de la bd
 */
public class RoundTable {
    // Index of player
    public static final String NICK = "nick";

    // Index of the other player
    public static final String NICKRIVAL = "nickrival";

    // Duration of round
    public static final String DURACION = "duracion";

    // Number of pieces left on the board
    public static final String NPIEZAS = "npiezas";

    // Date of the round
    public static final String FECHA = "fecha";

    // Board
    public static final String TABLERO = "tablero";

    // Boadr
    public static final String GANO = "gano";

    // Name of the table
    static final String TABLE_NAME = "rounds";
}