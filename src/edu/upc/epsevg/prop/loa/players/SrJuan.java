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
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author srimp
 */
public class SrJuan implements IPlayer, IAuto {
        
    private final SearchType executionType;
    private CellType nuestroCell;
    private int profundidadMax;
    private boolean timeout;

    public SrJuan(SearchType minimax, int profundidadMax) {
        this.executionType = minimax;
        this.nuestroCell = CellType.EMPTY;
        this.profundidadMax = profundidadMax;
        this.timeout = false;
    }
    
    @Override
    public Move move(GameStatus gs) {
        
        this.timeout = false;
        
        if (this.nuestroCell == CellType.EMPTY) {
            this.nuestroCell = gs.getCurrentPlayer();
        }
        
        Move test = obtenerMovimiento(gs, this.profundidadMax);
        
        System.out.println(test.getMaxDepthReached());
        System.out.println(test.getNumerOfNodesExplored());
                
        return test;
    }
    
    private double evalTablero(GameStatus gs) {
        return 5.3;
    }
    
    private Move obtenerMovimiento (GameStatus gs, int profundidadMaxima) {
        double mejorHeur = Double.NEGATIVE_INFINITY;
        
        Move mejorMovimiento = null;
        int piezasRestantes = gs.getNumberOfPiecesPerColor(this.nuestroCell);
        
        for (int i = 0; i < piezasRestantes; i++) {
            Point pieza = gs.getPiece(this.nuestroCell, i);
            ArrayList<Point> movimientos = gs.getMoves(pieza);
            for (Point movimiento : movimientos) {
                if (this.timeout && this.executionType == SearchType.MINIMAX_IDS) {
                    return mejorMovimiento;
                }
                Move movimientoActual = new Move(pieza, movimiento, 0, 0, this.executionType);
                double alfa = Double.NEGATIVE_INFINITY;
                GameStatus aux = new GameStatus(gs);
                aux.movePiece(pieza, movimiento);
                if (aux.isGameOver()) {
                    return movimientoActual;
                }
                else {
                    alfa = minimax(aux, movimientoActual, profundidadMaxima - 1, mejorHeur, Double.POSITIVE_INFINITY, false);
                    if (alfa > mejorHeur || mejorMovimiento == null) {
                        mejorMovimiento = movimientoActual;
                        mejorHeur = alfa;
                    }
                }
            }
        }
        return mejorMovimiento;
    }
    
    private double minimax (GameStatus gs, Move movPrincipalActual, int profundidad, Double alfa, Double beta, boolean isMax) {
        
        movPrincipalActual.setMaxDepthReached(profundidad);
        
        if (this.timeout && this.executionType == SearchType.MINIMAX_IDS) {
            return isMax ? alfa : beta;
        }
        
        if (profundidad <= 0) {
            movPrincipalActual.setNumerOfNodesExplored(movPrincipalActual.getNumerOfNodesExplored() + 1);
            return evalTablero(gs);
        }     
        
        if (isMax) {
            double nuevaAlfa = Double.NEGATIVE_INFINITY;
            int piezasRestantes = gs.getNumberOfPiecesPerColor(this.nuestroCell);
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
                        nuevaAlfa = Math.max(nuevaAlfa, minimax(aux, movPrincipalActual, profundidad - 1, alfa, beta, false));
                        alfa = Math.max(nuevaAlfa, alfa);
                        if (alfa >= beta) {
                            return alfa;
                        }
                    }
                }
            }
            return nuevaAlfa;
        }
        else {
            double nuevaBeta = Double.POSITIVE_INFINITY;
            CellType enemigo;
            if (this.nuestroCell == CellType.PLAYER1) {
                enemigo = CellType.PLAYER2;
            }
            else {
                enemigo = CellType.PLAYER1;
            }
            int piezasRestantes = gs.getNumberOfPiecesPerColor(enemigo);
            for (int i = 0; i < piezasRestantes; i++) {
                Point pieza = gs.getPiece(enemigo, i);
                ArrayList<Point> movimientos = gs.getMoves(pieza);
                for(Point movimiento : movimientos) {
                    GameStatus aux = new GameStatus(gs);
                    aux.movePiece(pieza, movimiento);
                    if (aux.isGameOver()) {
                        return Double.NEGATIVE_INFINITY;
                    }
                    else {
                        nuevaBeta = Math.min(nuevaBeta, minimax(aux, movPrincipalActual, profundidad - 1, alfa, beta, true));
                        beta = Math.min(nuevaBeta, beta);
                        if (alfa >= beta) {
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
        this.timeout = true;
    }

    @Override
    public String getName() {
        return "Sr Juan";
    }
    
}
