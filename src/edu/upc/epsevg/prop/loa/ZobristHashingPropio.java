/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import java.util.HashMap;
import java.util.Random;

public class ZobristHashingPropio {

    /**
     * Clase que se utiliza para almacenar la profundidad y el valor heuristico de un tablero
     */
    public class Evaluacion {
        public int profundidad;
        public double heur;

        public Evaluacion(int profundidad, double heur) {
            this.profundidad = profundidad;
            this.heur = heur;   
        }
    }
    
    /**
     * Valor aleatorio que se utiliza para hacer una XOR a los tableros donde tira el jugador blanco
     * para poder distinguir del mismo tablero donde tira el jugador negro
     */
    private final int whiteMove;
    
    /**
     * Hashmap donde se guardan todos los pares hash-evaluacion para posteriormente ser utilizados
     */
    private final HashMap<Integer, Evaluacion> InfoTabla;
    
    /**
     * tablero con valores aleatorios para hacer el hashing
     */
    private final int[][][] tablero;
    
    public ZobristHashingPropio(int mida){
        
        this.InfoTabla = new HashMap<>();
        this.tablero = new int[mida][mida][3];
        
        Random rand = new Random();
        
        for(int i = 0; i < mida; i++) {
            for(int j = 0; j < mida; j++) {
                for(int k = 0; k < 2; k++) {
                    this.tablero[i][j][k] = rand.nextInt();
                }
            }
        }
        
        this.whiteMove = rand.nextInt();
        
    }
    
    /**
     * Sirve para meter en la tabla de hash la informacion de un tablero
     * @param gs Estado del juego
     * @param heur valor heuristico del tablero
     * @param profundidad profundidad a la que se ha encontrado el tablero
     */
    public void update(GameStatus gs, double heur, int profundidad) {
        int hash = this.getHash(gs);
        this.InfoTabla.put(hash, new Evaluacion(profundidad, heur));
    }
    
    /**
     * 
     * @param gs Estado del juego
     * @param profundidad profundidad en la que se encuentra ese gamestatus
     * @return Null si no se ha encontrado el tablero en el HashMap o si el tablero que se ha encontrado está a menos profundidad
     *      y devuelve la evaluación del gamestatus si este está a una profundidad mayor que la profundidad que se encuentra el gamestatus
     *      en ese momento
     */
    public Evaluacion evaluate (GameStatus gs, int profundidad) {
        int hash = this.getHash(gs);
        if (this.InfoTabla.containsKey(hash)) {
            Evaluacion info = this.InfoTabla.get(hash);
            if (info.profundidad >= profundidad) {
                return info;
            }
        }
        return null;
    }
        
    /**
     * Devuelve el valor de hash del tablero del gamestatus
     * @param gs Estado del juego
     * @return Valor heuristico del tablero del gamestatus
     */
    public int getHash (GameStatus gs) {
        int mida = gs.getSize();
        int hash = 0;
                
        for(int i = 0; i < mida; i++) {
            for (int j = 0; j < mida; j++) {
                hash ^= this.tablero[i][j][this.celltypeToInt(gs.get(i, j))];
            }
        }
        
        return hash ^ (gs.getCurrentPlayer() == CellType.PLAYER1 ? this.whiteMove : 0);
    }
    
    /**
     * Pasa de un celltype a un entero util para el acceso del array this.tablero
     * @param cell El cell que se quiere pasar
     * @return si es EMPTY: 0, si es PLAYER1: 1 y si es PLAYER2: 2
     */
    private int celltypeToInt(CellType cell) {
        switch(cell) {
            case EMPTY:
                return 0;
            case PLAYER1:
                return 1;
            case PLAYER2:
                return 2;
            default:
                return 0;
        }
    }
}
