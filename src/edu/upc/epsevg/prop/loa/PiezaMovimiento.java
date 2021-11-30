/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import java.awt.Point;

/**
 *
 * @author naman jain
 * https://stackoverflow.com/questions/52892627/can-not-resolve-import-javafx-util-pair-in-android-studio/52892991
 * 
 */
public class PiezaMovimiento {
    private final Point pieza;
    private final Point movimiento;
    
    public PiezaMovimiento() {
        this.pieza = new Point();
        this.movimiento = new Point();
    }

    public PiezaMovimiento(Point pieza, Point movimiento) {
        this.pieza = pieza;
        this.movimiento = movimiento;
    }

    public Point getPieza() {
        return this.pieza;
    }

    public Point getMovimiento() {
        return this.movimiento;
    }
}