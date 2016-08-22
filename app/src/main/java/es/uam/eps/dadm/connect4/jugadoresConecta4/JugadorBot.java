/*
 * JugadorBot.java
 */
package es.uam.eps.dadm.connect4.jugadoresConecta4;

import java.util.ArrayList;

import es.uam.eps.dadm.connect4.model.TableroConecta4;
import es.uam.eps.multij.*;

/**
 * Clase que implementa un jugador para el TableroConecta4
 * Este jugador juega aleatoriamente a no se que pueda ganar
 * con ese movimiento o impedir con su movimiento ser derrotado
 * en la jugada posterior del rival.
 *
 * @see es.uam.eps.multij.Jugador
 * @see es.uam.eps.dadm.connect4.model.TableroConecta4
 * @author Manuel Reyes
 */
public class JugadorBot implements Jugador{
	
	private String nombre;
	private static int numBots = 0;

	/**
	 * Constructor
	 *
	 * @param nombre que recibira
	 */
	public JugadorBot(String nombre){ this.nombre = nombre; }

	/**
	 *Constructor por defecto
	 */
	public JugadorBot(){ this("Bot "+ (++numBots)); }

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
	            break;
	            
	        case Evento.EVENTO_CONFIRMA:
	        	break;
	        
	        case Evento.EVENTO_TURNO:

	        	/** "Minimax" perezoso **/
	        	TableroConecta4 t4 = (TableroConecta4) t;
	        	ArrayList<Movimiento> movs = t.movimientosValidos();
	        	
	        	/*Recorremos todos los movimientos*/
	        	for(Movimiento m: movs){
	        		TableroConecta4 aux = t4.copiaTablero();
	        		/*Si finaliza, hemos ganado*/
					if(finPartida(aux, m)) try {
						evento.getPartida().realizaAccion(new AccionMover(this, m));
						return;
					} catch (ExcepcionJuego ignored) {
					}
	        	}
	        	
	        	/*Ninguno de los movimientos finaliza la partida*/
	        	ArrayList<Movimiento> movs2 = new ArrayList<Movimiento>();
	        	for(Movimiento m: movs){
	        		try {
	        			TableroConecta4 aux = t4.copiaTablero();
						aux.mueve(m);
						ArrayList<Movimiento> movs_enem = t.movimientosValidos();
						boolean flag = true;
						/*Para cada uno de mis movimientos examino los del rival*/
						for(Movimiento m_enem: movs_enem){
							TableroConecta4 aux2 = aux.copiaTablero();
							if(finPartida(aux2, m_enem))
								flag=false; /*El enemigo gana*/
						}
						/*Si tras recorrer el enemigo no gana ese mov es valido*/
						if(flag)
							movs2.add(m);
					} catch (ExcepcionJuego ignored) {
					}	
	        	}
	        	
	        	/*Elegimos un mov aleatorio*/
	        	try {
	        		if(movs2.isEmpty()){
	    	        	/*Todos son perdedores*/
						evento.getPartida().realizaAccion(new AccionMover(this, movs.get(0)));
	        		}else{
	        			/*Aleatorio entre los no perdedores*/
	        			/*Posibilidad de mejora haciendo una heuristica que evalue esos tableros*/
	        			int r = (int)(Math.random() * movs2.size());
	        			evento.getPartida().realizaAccion(new AccionMover(this, movs2.get(r)));
	        		}
				} catch (ExcepcionJuego ignored) {
				}
	        	break;
		}	
	}

	/**
	 * Metodo auxilar que indica si un movimiento finaliza una partida
	 *
	 * @param t4 tablero
	 * @param m movimiento
	 * @return true if la partida finaliza else false
	 */
	private boolean finPartida(TableroConecta4 t4, Movimiento m){
		try {
			t4.mueve(m);
			/*Si tras mover hemos finalizado, hemos ganado*/
			if(t4.getEstado()==Tablero.FINALIZADA)
				return true;
		} 
		catch (ExcepcionJuego ignored) {
	    }
		return false;
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
	 * @return true if es TableroConecta4 else false
	 */
	@Override
	public boolean puedeJugar(Tablero tablero) {
		return (tablero instanceof TableroConecta4);
	}

}
