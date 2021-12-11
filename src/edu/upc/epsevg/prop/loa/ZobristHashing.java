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
 * Ahora mismo tengo una gran duda y es si deberia traerme en el constructor la informacion del tablero y setear el hashValue en un bucle justo debajo del this.hashvalue =0 o si como esto ya está, esta bien y gestionamos el seteo del hashvalue desde fuera.
 * Tampoco estoy seguro si vamos a necesitar hacer el constructor por copia y tampoco se si en algun momento tenemos que reiniciar el hashvalue. 
 */
public class ZobristHashing {
    private int hashValue;
    private final int [][][] tablero;
    
    @SuppressWarnings("MathRandomCastToInt")
    public ZobristHashing(Integer mida){
        this.tablero = new int[mida][mida][2];
        for (int i = 0; i<this.tablero.length; i++){
            for (int j = 0; j < mida; i++) {
                for (int k = 0; k < mida; i++) {
                    this.tablero[i][j][k] = (int) Math.random();
                }
            }
        }
        this.hashValue = 0;
    }
    public int update(int color, int x, int y){//Sino patino con esto hacemos la xor
        return this.hashValue ^= this.tablero[color][x][y];
    }
    
    public int add(CellType c, Point p) {
        return this.update(CellType.toColor01(c), p.x,p.y);
    }
    public int remove(CellType c, Point p) {
        return this.update(CellType.toColor01(c), p.x,p.y);
    }
    
    public int hashCode() {
        return this.hashValue;
    }
    public void sethashCode(int hash) {
        this.hashValue = hash;
    }
}
