/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import java.awt.Point;
import java.util.Random;

/**
 *
 * @author srimp
 */
public class GameStatusPropio extends GameStatus {
    
    private int hashValue;
    
    private int profundidad;
    private int nodosExplorados;
    private int valorHeuristica;
    private Move mejorTirada;
    
    public GameStatusPropio(GameStatusPropio gs) {
        super(gs);
        
        int mida = gs.getSize();
        this.hashValue = gs.hashValue;
        // this.tablero = new int[mida][mida][3];
        this.profundidad = gs.profundidad;
        this.nodosExplorados = gs.nodosExplorados;
        this.valorHeuristica = gs.valorHeuristica;
        this.mejorTirada = gs.mejorTirada;
        
    }
    
    public GameStatusPropio(GameStatus gs) {
        super(gs);
        
        this.hashValue = 0;
    }
        
    @Override
    public void movePiece(Point point, Point point1) {
        super.movePiece(point, point1);
        
        int i = point.x, j = point.y, i1 = point1.x, j1 = point1.y;
        int type = this.CellTypeToIndex(this.getPos(point));
        int type1 = this.CellTypeToIndex(this.getPos(point1));
        
        // Quitar ficha de espacio actual
        /*this.hashValue ^= this.tablero[i][j][type];
        if (type1 == 0) {
            // Mover ficha a espacio vacio
            this.hashValue ^= this.tablero[i1][j1][type];
        } else {
            // Eliminar fichar y mover la tuya
            this.hashValue ^= this.tablero[i1][j1][type1];
            this.hashValue ^= this.tablero[i1][j1][type];
        }*/
        
        this.nodosExplorados++;
        this.profundidad++;
    }
    
    private int CellTypeToIndex(CellType cell) {
        switch (cell) {
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

    @Override
    public int hashCode() {
        return this.hashValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameStatusPropio other = (GameStatusPropio) obj;
        return true;
    }
    
    
        
}
