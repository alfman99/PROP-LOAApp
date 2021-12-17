/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import java.awt.Point;

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
public class ZobristHashing {
    private int hashValue;
    private final int[][][] tablero;
    
    public ZobristHashing(GameStatus gs){
        this.hashValue = 0;
        
        int mida = gs.getSize();
        this.tablero = new int[mida][mida][3];
        
        for(int i = 0; i < mida; i++) {
            for(int j = 0; j < mida; j++) {
                for(int k = 0; k < 2; k++) {
                    this.tablero[i][j][k] = (int)(Math.random() * 1069);
                }
            }
        }
        
        int numPiezas = gs.getNumberOfPiecesPerColor(CellType.PLAYER1);
        for (int i = 0; i < numPiezas; i++) {
            Point pieza = gs.getPiece(CellType.PLAYER1, i);
            this.hashValue ^= this.tablero[CellType.toColor01(CellType.PLAYER1)][pieza.x][pieza.y];
        }
        
        numPiezas = gs.getNumberOfPiecesPerColor(CellType.PLAYER2);
        for (int i = 0; i < numPiezas; i++) {
            Point pieza = gs.getPiece(CellType.PLAYER2, i);
            this.hashValue ^= this.tablero[CellType.toColor01(CellType.PLAYER2)][pieza.x][pieza.y];
        }
        
    }
    
    public int update(int color, int x, int y){
        return this.hashValue ^= this.tablero[x][y][color];
    }
    
    public int add(CellType c, Point p) {
        return this.update(CellType.toColor01(c), p.x, p.y);
    }
    
    public int remove(CellType c, Point p) {
        return this.update(CellType.toColor01(c), p.x, p.y);
    }   
}
