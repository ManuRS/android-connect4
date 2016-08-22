/*
 * TableroConecta4.java
 */
package es.uam.eps.dadm.connect4.model;

import android.util.Log;

import java.util.ArrayList;

import es.uam.eps.multij.ExcepcionJuego;
import es.uam.eps.multij.Movimiento;
import es.uam.eps.multij.Tablero;

/**
 * Clase que codifica un Tablero para el juego Conecta4
 */
public class TableroConecta4 extends Tablero{
	
	public static final int FILAS = 6;
	public static final int COLUMNAS = 7;
	private int[][] tablero = new int[FILAS][COLUMNAS]; /*valor 0 por defecto en java*/
	public static final int JUGADOR1 = 1;
	public static final int JUGADOR2 = 2;
	private int turnoJugada = JUGADOR1;
	public int ultimaJugada = -1;
	public int penultimaJugada = -1;
	public int numMovs = 0;

	/**
	 * Constructor por defecto
	 */
	public TableroConecta4(){ estado=EN_CURSO; }

	/**
	 * Devuelve un nuevo objeto de tipo TableroConecta4 que es una copia del objeto que llama
	 *
	 * @return TableroConecta4
	 */
	public TableroConecta4 copiaTablero(){
		TableroConecta4 t = new TableroConecta4();
		t.turnoJugada = this.turnoJugada;
		t.turno = this.turno;
		t.estado = this.estado;
		t.numJugadas = this.numJugadas;
		t.ultimoMovimiento = this.ultimoMovimiento;
		for(int i=0; i<FILAS; i++){
			for(int j=0; j<COLUMNAS; j++){
				t.tablero[i][j]=this.tablero[i][j];
			}
		}
		return t;
	}

	/**
	 * Consulta contenido de una casilla
	 *
	 * @param i fila
	 * @param j columna
	 * @return contenido
	 */
	public int getPosicion(int i, int j){
		return tablero[i][j];
	}

	/**
	 * Realiza un movimiento sobre el tablero
	 *
	 * @param m movimiento a realizar
	 * @throws ExcepcionJuego
	 */
	@Override
	public void mueve(Movimiento m) throws ExcepcionJuego {
		MovimientoConecta4 m4 = (MovimientoConecta4) m;
		if(esValido(m4)){
			for(int i=FILAS-1; i>=0; i--){
				if(tablero[i][m4.getColumna()]==0){
					tablero[i][m4.getColumna()]=turnoJugada;
					if(turnoJugada==JUGADOR1)
						turnoJugada=JUGADOR2;
					else
						turnoJugada=JUGADOR1;
					numMovs++;
					this.ultimoMovimiento=m;
					estado=determinarEstado();
					if(estado==EN_CURSO)cambiaTurno();
					penultimaJugada=ultimaJugada;
					ultimaJugada=i*10+m4.getColumna();
					return;
				}
			}
		}else{
			throw new ExcepcionJuego("Movimiento no valido");
		}
		
	}

	/**
	 * Indica la validez de un movimiento sobre el tablero
	 *
	 * @param m Movimiento
	 * @return true o false
	 */
	@Override
	public boolean esValido(Movimiento m) {
		ArrayList<Movimiento> movs = movimientosValidos();
		for(Movimiento mov: movs){
			if(m.equals(mov))
				return true;
		}
		return false;
	}

	/**
	 * Indica los movimientos validos para el tablero
	 *
	 * @return ArrayList de Movimientos validos
	 */
	@Override
	public ArrayList<Movimiento> movimientosValidos() {
		ArrayList<Movimiento> result = new ArrayList<>();
		if(estado!=EN_CURSO) return result;
		for (int i=0; i<COLUMNAS; i++){
			if(tablero[0][i]==0)
				result.add(new MovimientoConecta4(i));
		}
		return result;
	}

	/**
	 * Indica el estado del tablero
	 *
	 * @return estado del tablero
	 */
	@Override
	public int getEstado(){
		return determinarEstado();
	}

	/**
	 * Determina estado del tablero
	 *
	 * @return estado del tablero
	 */
    private int determinarEstado(){
		int blanco=0;
		for(int i=0; i<FILAS; i++){
			for(int j=0; j<COLUMNAS; j++){
				if(tablero[i][j]!=0){
					if(i<=2)
						if(columnaGanadora(i, j))
							return FINALIZADA;
					if(j<=3)
						if(filaGanadora(i, j))
							return FINALIZADA;
					if(i<=2 && j<=3)
						if(diagonalDerechaGanadora(i, j))
							return FINALIZADA;
					if(i<=2 && j>=3)
						if(diagonalIzquierdaGanadora(i, j))
							return FINALIZADA;
				}else{
					/*Contamos las posiciones en blanco*/
					blanco++;
				}
			}
		}
		if(blanco==0){
			return TABLAS;
		}else{
			return EN_CURSO;
		}
    }

	/**
	 * Indica si una fila es ganadora
	 *
	 * @param fila a comprobar
	 * @param columna a comprobar
	 * @return true o false
	 */
	private Boolean filaGanadora(int fila, int columna){
		return (
			 this.tablero[fila][columna]==this.tablero[fila][columna+1] &&
			 this.tablero[fila][columna]==this.tablero[fila][columna+2] &&
			 this.tablero[fila][columna]==this.tablero[fila][columna+3]
		);
	}

	/**
	 * Indica si una columna es ganadora
	 *
	 * @param fila a comprobar
	 * @param columna a comprobar
	 * @return true o false
	 */
	private Boolean columnaGanadora(int fila, int columna){
		return (
			 this.tablero[fila][columna]==this.tablero[fila+1][columna] &&
			 this.tablero[fila][columna]==this.tablero[fila+2][columna] &&
			 this.tablero[fila][columna]==this.tablero[fila+3][columna]
		);
	}


	/**
	 * Indica si una diagonal a la derecha es ganadora
	 *
	 * @param fila a comprobar
	 * @param columna a comprobar
	 * @return true o false
	 */
	private Boolean diagonalDerechaGanadora(int fila, int columna){
		return (
 			 this.tablero[fila][columna]==this.tablero[fila+1][columna+1] &&
			 this.tablero[fila][columna]==this.tablero[fila+2][columna+2] &&
			 this.tablero[fila][columna]==this.tablero[fila+3][columna+3]
		);
	}

	/**
	 * Indica si una diagonal a la izquierda es ganadora
	 *
	 * @param fila a comprobar
	 * @param columna a comprobar
	 * @return true o false
	 */
	private Boolean diagonalIzquierdaGanadora(int fila, int columna){
		return (
			 this.tablero[fila][columna]==this.tablero[fila+1][columna-1] &&
			 this.tablero[fila][columna]==this.tablero[fila+2][columna-2] &&
			 this.tablero[fila][columna]==this.tablero[fila+3][columna-3]
		);
	}

	/**
	 * Transforma el tablero en una cadena
	 *
	 * @return cadena que codifica el tablero
	 */
	@Override
	public String tableroToString() {
		String ret = "";
		for(int i=0; i<FILAS; i++){
			for(int j=0; j<COLUMNAS; j++){
				ret = ret+tablero[i][j];
			}
		}
		ret = ret+turnoJugada;
		return ret;
	}


	/**
	 * Carga una codificacion del tablero
	 *
	 * @param cadena que codifica el tablero
	 */
	@Override
	public void stringToTablero(String cadena) {
		int count=0;
		numMovs=0;
		for(int i=0; i<FILAS; i++){
			for(int j=0; j<COLUMNAS; j++){
				tablero[i][j] = Integer.parseInt(cadena.substring(count, count + 1));
				if(tablero[i][j]!=0)
					numMovs++;
				count++;
			}
		}
		turnoJugada=Integer.parseInt(cadena.substring(count, count+1));
		estado=determinarEstado();
	}

	/**
	 * Devuelve una representacion visual mediante texto del tablero
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		String ret = "";
		if(this.getUltimoMovimiento()!=null)
			ret = ret + this.getUltimoMovimiento().toString() +"\n";
		ret = ret + "  0 1 2 3 4 5 6\n  ' ' ' ' ' ' '\n";
		for(int i=0; i<FILAS; i++){
			ret = ret+" |";
			for(int j=0; j<COLUMNAS; j++){
				if(tablero[i][j]==1)
					ret = ret+"o|";
				else if(tablero[i][j]==2)
					ret = ret+"x|";
				else
					ret = ret+" |";
			}
			ret=ret+"\n";
		}
		ret = ret+" ---------------\n";
		if(this.estado==Tablero.EN_CURSO){
			if(this.getTurno()==0)
				ret = ret+"Turno de: o\n";
			else
				ret = ret+"Turno de: x\n";
		}else if(this.estado==Tablero.TABLAS){
			ret = ret + "\nEMPATE\n";
		}else if(this.getTurno()==0){
			ret = ret + "\nGANAN: " + "o\n";
		}else{
			ret = ret + "\nGANAN: " + "x\n";
		}
		return ret;
	}

	@Override
	public int getTurno() {
		return turnoJugada-1;
	}
}
