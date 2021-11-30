/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.PiezaMovimiento;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author srimp
 */
public class SrJuan implements IPlayer, IAuto {
        
    private final SearchType executionType;
    private CellType nuestroCell;

    public SrJuan(SearchType minimax) {
        this.executionType = minimax;
        this.nuestroCell = CellType.EMPTY;
    }
    
    @Override
    public Move move(GameStatus gs) {
        
        if (this.nuestroCell == CellType.EMPTY) {
            this.nuestroCell = gs.getCurrentPlayer();
        }
        
        PiezaMovimiento movimiento = obtenerMovimiento(gs, 4);
        
        Move test = new Move(movimiento.getPieza(), movimiento.getMovimiento(), 0, 0, SearchType.RANDOM);
        
        System.out.println(test);
        
        return test;
    }
    
    private double evalTablero(GameStatus gs) {
        return 5.3;
    }
    
    private PiezaMovimiento obtenerMovimiento (GameStatus gs, int profundidadMaxima) {
        double mejorHeur = Double.NEGATIVE_INFINITY;
        PiezaMovimiento mejorMovimiento = new PiezaMovimiento();
        
        int piezasRestantes = gs.getNumberOfPiecesPerColor(this.nuestroCell);
        
        for (int i = 0; i < piezasRestantes; i++) {
            Point pieza = gs.getPiece(this.nuestroCell, i);
            ArrayList<Point> movimientos = gs.getMoves(pieza);
            for (Point movimiento : movimientos) {
                double alfa = Double.NEGATIVE_INFINITY;
                GameStatus aux = new GameStatus(gs);
                aux.movePiece(pieza, movimiento);
                if (aux.isGameOver()) {
                    return new PiezaMovimiento(pieza, movimiento);
                }
                else {
                    alfa = minimax(aux, profundidadMaxima - 1, mejorHeur, Integer.MAX_VALUE , false);
                    if (alfa > mejorHeur || mejorMovimiento.equals(new PiezaMovimiento())) {
                        mejorMovimiento = new PiezaMovimiento(pieza, movimiento);
                        mejorHeur = alfa;
                    }
                }
            }
        }
        return mejorMovimiento;
    }
    
    private double minimax (GameStatus gs, int profundidad, double alfa, double beta, boolean isMax) {
        if (profundidad <= 0) {
            return evalTablero(gs);
        }
        
        CellType playerCell;
        
        if (isMax) {
            playerCell = this.nuestroCell;
        }
        else {
            if (this.nuestroCell == CellType.PLAYER1) {
                playerCell = CellType.PLAYER2;
            }
            else {
                playerCell = CellType.PLAYER1;
            }
        }
        
        if (isMax) {
            double nuevaAlfa = Double.NEGATIVE_INFINITY;
            int piezasRestantes = gs.getNumberOfPiecesPerColor(playerCell);
            for (int i = 0; i < piezasRestantes; i++) {
                Point pieza = gs.getPiece(this.nuestroCell, i);
                ArrayList<Point> movimientos = gs.getMoves(pieza);
                for(Point movimiento : movimientos) {
                    GameStatus aux = new GameStatus(gs);
                    aux.movePiece(pieza, movimiento);
                    if (aux.isGameOver()) {
                        return Double.POSITIVE_INFINITY;
                    }
                    else {
                        nuevaAlfa = Math.max(nuevaAlfa, minimax(aux, profundidad - 1, alfa, beta, false));
                        alfa = Math.max(alfa, nuevaAlfa);
                        if(alfa >= beta) {
                            return alfa;
                        }
                    }
                }
            }
            return nuevaAlfa;
        }
        else {
            double nuevaBeta = Double.POSITIVE_INFINITY;
            int piezasRestantes = gs.getNumberOfPiecesPerColor(playerCell);
            for (int i = 0; i < piezasRestantes; i++) {
                Point pieza = gs.getPiece(this.nuestroCell, i);
                ArrayList<Point> movimientos = gs.getMoves(pieza);
                for(Point movimiento : movimientos) {
                    GameStatus aux = new GameStatus(gs);
                    aux.movePiece(pieza, movimiento);
                    if (aux.isGameOver()) {
                        return Double.NEGATIVE_INFINITY;
                    }
                    else {
                        nuevaBeta = Math.min(nuevaBeta, minimax(aux, profundidad - 1, alfa, beta, true));
                        beta = Math.min(beta, nuevaBeta);
                        if(alfa >= beta) {
                            return beta;
                        }
                    }
                }
            }
            return nuevaBeta;
        }
        
    }

    @Override
    public void timeout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "Sr Juan";
    }
    
}
