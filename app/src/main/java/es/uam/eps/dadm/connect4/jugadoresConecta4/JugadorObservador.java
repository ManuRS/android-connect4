/*
 * JugadorObservador.java
 */
package es.uam.eps.dadm.connect4.jugadoresConecta4;

import es.uam.eps.multij.*;

/**
 * Jugador que imprime las jugadas por consola, puede usarse para debug
 * No juega, no utilizar como jugador
 *
 * @see es.uam.eps.multij.Jugador
 * @see es.uam.eps.dadm.connect4.model.TableroConecta4
 * @author Manuel Reyes
 */
public class JugadorObservador implements Jugador{
	
	private String nombre;
	private static int numObservadores = 0;

	/**
	 * Constructor
	 *
	 * @param nombre que recibira
	 */
	JugadorObservador(String nombre){
		this.nombre = nombre;
	}

	/**
	 *Constructor por defecto
	 */
	JugadorObservador(){
		this("Humano "+ (++numObservadores));
	}


	/**
	 * Recive eventos de la partida y actua en consecuencia
	 *
	 * @param evento
	 */
	@Override
	public void onCambioEnPartida(Evento evento) {
		Tablero t = evento.getPartida().getTablero();
		switch (evento.getTipo()) {
	        case Evento.EVENTO_CAMBIO:
	        	/*Impresion del tablero*/
	        	System.out.println(t.toString());
	            break;
	            
	        case Evento.EVENTO_CONFIRMA:
	        	break;
	        
	        case Evento.EVENTO_TURNO:
	        	break;
		}	
	}

	/**
	 * Indentificador del jugador
	 */
	@Override
	public String getNombre() {
		return nombre;
	}


	/**
	 * Indica si un tablero es jugable por el jugador
	 *
	 * @param tablero
	 * @return false, este jugador no juega, solo observa
	 */
	@Override
	public boolean puedeJugar(Tablero tablero) {
		return false;
	}

}
