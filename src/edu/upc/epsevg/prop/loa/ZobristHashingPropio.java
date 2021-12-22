/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author alehe
 * Ahora mismo tengo una gran duda y es si deberia traerme en el constructor la
 * informacion del tablero y setear el hashValue en un bucle justo debajo del
 * this.hashvalue = 0 o si como esto ya está, esta bien y gestionamos el seteo
 * del hashvalue desde fuera.
 * 
 * Tampoco estoy seguro si vamos a necesitar hacer el constructor por copia y
 * tampoco se si en algun momento tenemos que reiniciar el hashvalue. 
 */
public class ZobristHashingPropio {

    public class Evaluacion {
        public int profundidad;
        public double heur;

        public Evaluacion(int profundidad, double heur) {
            this.profundidad = profundidad;
            this.heur = heur;
            
        }
        
    }
    
    private final int whiteMove;
    private final HashMap<Integer, Evaluacion> InfoTabla;
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
    
    public void update(GameStatus gs, double heur, int profundidad) {
        int hash = this.getHash(gs);
        this.InfoTabla.put(hash, new Evaluacion(profundidad, heur));
    }
    
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
