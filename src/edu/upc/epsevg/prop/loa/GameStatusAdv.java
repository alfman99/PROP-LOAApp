/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import java.awt.Point;

/**
 *
 * @author srimp
 */
public class GameStatusAdv extends GameStatus {
    
    private int hashValue;
    private final int[][][] tablero;
    
    public GameStatusAdv(GameStatus gs) {
        super(gs);
        int mida = gs.getSize();
        
        this.hashValue = 0;
        this.tablero = new int[mida][mida][3];
    }
    
    @Override
    public void movePiece(Point point, Point point1) {
        System.out.println("ASLÑDJFJKLÑASDSJKDLÑFAAFSDJKLSFDAJKLÑ");
        super.movePiece(point, point1);
    }
    
    
}
