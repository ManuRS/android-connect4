/*
 * MovimientoConecta4.java
 */
package es.uam.eps.dadm.connect4.model;
import es.uam.eps.multij.Movimiento;

/**
 * Clase que representa un Movimiento para el TableroConecta4
 *
 * @see es.uam.eps.multij.Movimiento
 * @see es.uam.eps.dadm.connect4.model.TableroConecta4
 */
public class MovimientoConecta4 extends Movimiento {
	
	private int columna;

	/**
	 * Constructor
	 *
	 * @param columna en la cual se mueve
	 */
	public MovimientoConecta4(int columna){
		this.columna = columna;
	}

	/**
	 * Devuelve como mensaje el movimiento
	 *
	 * @return texto con la columna del movimiento
	 */
	@Override
	public String toString() {
		return  "Ficha introducida en columna: "+columna;
	}


	/**
	 * Indica si dos movimientos son iguales
	 *
	 * @param o objeto a comparar
	 * @return true if ambos son el mismo movimiento else false
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof MovimientoConecta4){
			MovimientoConecta4 aux = (MovimientoConecta4)o;
			return columna == aux.getColumna();
		}
		return false;
	}

	/**
	 * Devuelve la columna de un movimiento
	 *
	 * @return columna
	 */
	public int getColumna(){
		return columna;
	}

}
