/*
 * JugadorRemoto.java
 */
package es.uam.eps.dadm.connect4.jugadoresConecta4;

import es.uam.eps.multij.Evento;
import es.uam.eps.multij.Jugador;
import es.uam.eps.multij.Tablero;

/**
 * Jugador que no hace nada. He optado por un modelo orientado a tener unas
 * cuantas partidas simultaneas y no centrada en jugar una partida en directo
 * con otra persona. Por eso no hace falta
 *
 * @see es.uam.eps.multij.Jugador
 * @see es.uam.eps.dadm.connect4.model.TableroConecta4
 * @author Manuel Reyes
 */
public class JugadorRemoto implements Jugador {

    private String nombre;

    /**
     * Constructor
     *
     * @param nombre que recibira
     */
    public JugadorRemoto(String nombre){ this.nombre = nombre; }

    @Override
    public String getNombre() {
        return "Remoto";
    }

    @Override
    public boolean puedeJugar(Tablero tablero) {
        return false;
    }

    @Override
    public void onCambioEnPartida(Evento evento) {


    }
}
