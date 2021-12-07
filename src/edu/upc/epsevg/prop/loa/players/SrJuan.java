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
 * @author https://www.techiedelight.com/implement-pair-class-java/
 * @param <U>
 * @param <V> 
 */
class Pair<U, V> {
    private final U first;       // the first field of a pair
    private final V second;      // the second field of a pair
 
    // Constructs a new pair with specified values
    public Pair(U first, V second)
    {
        this.first = first;
        this.second = second;
    }
 
    @Override
    // Checks specified object is "equal to" the current object or not
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
 
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
 
        Pair<?, ?> pair = (Pair<?, ?>) o;
 
        // call `equals()` method of the underlying objects
        if (!first.equals(pair.first)) {
            return false;
        }
        return second.equals(pair.second);
    }
  
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    public U getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
 
}

/**
 *
 * @author srimp
 */
public class SrJuan implements IPlayer, IAuto {
        
    private final SearchType executionType;
    private CellType nuestroCell;
    private CellType enemigoCell;
    private final int profundidadMax;
    private boolean timeout;

    public SrJuan(SearchType minimax, int profundidadMax) {
        this.executionType = minimax;
        this.nuestroCell = CellType.EMPTY;
        this.enemigoCell = CellType.EMPTY;
        this.profundidadMax = profundidadMax;
        this.timeout = false;
    }
    
    @Override
    public Move move(GameStatus gs) {
        
        this.timeout = false;
                
        if (this.nuestroCell == CellType.EMPTY || this.enemigoCell == CellType.EMPTY) {
            this.nuestroCell = gs.getCurrentPlayer();
            this.enemigoCell = CellType.opposite(this.nuestroCell);
        }       
        
        Pair<Move, Double> test = null;
        
        switch (this.executionType) {
            case MINIMAX_IDS: {
                try {
                    test = obtenerMovimiento_IDS(gs);
                }
                catch (RuntimeException ex) {
                    System.out.println(ex.getMessage());
                    Point pieza = gs.getPiece(this.enemigoCell, 0);
                    ArrayList movimiento = gs.getMoves(pieza);
                    return new Move(pieza, (Point) movimiento.get(0), 0, 0, SearchType.MINIMAX_IDS);
                }
                break;
            }
            case MINIMAX:
            default: {
                test = obtenerMovimiento(gs, this.profundidadMax);
                break;
            }
        }
        
        System.out.println(test.getFirst().getMaxDepthReached());
        System.out.println(test.getFirst().getNumerOfNodesExplored());
                
        return test.getFirst();
    }
    //EMPEZAR DE NUEVO ESTE METODO
    private double mediaAlCentro (double sumaTotal) {
        return 0.0;
    }
    private double distanciaCentro (Point pieza, GameStatus gs){
        Point center = new Point(gs.getSize()/2, gs.getSize()/2);
        double diffX = center.getX() - pieza.getX();
        double diffY = center.getY() - pieza.getY();

        return Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
    }
    
    private double evalTablero(GameStatus gs) {
        
        return 0.0;
    }
    
    private Pair<Move, Double> obtenerMovimiento_IDS (GameStatus gs) {
        int i = 1;
        Pair<Move, Double> mejorMov = null;
        while (!this.timeout) {
            Pair<Move, Double> aux = null;
            try {
                aux = obtenerMovimiento(gs, i);
                mejorMov = aux;
            }
            catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
            mejorMov.getFirst().setMaxDepthReached(i);
            i++;
        }
        if (mejorMov == null) {
            throw new RuntimeException("No se ha completado ni el primer nivel IDS");
        }
        return mejorMov;
    }
        
    private Pair<Move, Double> obtenerMovimiento (GameStatus gs, int profundidadMaxima) {
        double mejorHeur = Double.NEGATIVE_INFINITY;
        
        Move mejorMovimiento = null;
        int piezasRestantes = gs.getNumberOfPiecesPerColor(this.nuestroCell);
        
        for (int i = 0; i < piezasRestantes; i++) {
            Point pieza = gs.getPiece(this.nuestroCell, i);
            ArrayList<Point> movimientos = gs.getMoves(pieza);
            for (Point movimiento : movimientos) {
                Move movimientoActual = new Move(pieza, movimiento, 0, 0, this.executionType);
                double alfa = Double.NEGATIVE_INFINITY;
                GameStatus aux = new GameStatus(gs);
                aux.movePiece(pieza, movimiento);
                if (aux.isGameOver()) {
                    return new Pair<>(movimientoActual, Double.POSITIVE_INFINITY);
                }
                else {
                    if (this.timeout && this.executionType == SearchType.MINIMAX_IDS) {
                        throw new RuntimeException("Timeout obtenerMov");
                    }
                    try {
                        alfa = minimax(aux, movimientoActual, profundidadMaxima - 1, mejorHeur, Double.POSITIVE_INFINITY, false);
                    }
                    catch (RuntimeException ex) {
                        throw new RuntimeException(ex.getMessage());
                    }
                    if (alfa > mejorHeur || mejorMovimiento == null) {
                        mejorMovimiento = movimientoActual;
                        mejorHeur = alfa;
                    }
                }
            }
        }
        mejorMovimiento.setMaxDepthReached(this.profundidadMax);
        return new Pair<>(mejorMovimiento, mejorHeur);
    }
    
    private double minimax (GameStatus gs, Move movPrincipalActual, int profundidad, Double alfa, Double beta, boolean isMax) {
        
        if (this.timeout && this.executionType == SearchType.MINIMAX_IDS) {
            throw new RuntimeException("Timeout minimax");
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
                        if (aux.GetWinner() == this.nuestroCell) {
                            return Double.POSITIVE_INFINITY;
                        }
                        else {
                            return Double.NEGATIVE_INFINITY;
                        }
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
            int piezasRestantes = gs.getNumberOfPiecesPerColor(this.enemigoCell);
            for (int i = 0; i < piezasRestantes; i++) {
                Point pieza = gs.getPiece(this.enemigoCell, i);
                ArrayList<Point> movimientos = gs.getMoves(pieza);
                for(Point movimiento : movimientos) {
                    GameStatus aux = new GameStatus(gs);
                    aux.movePiece(pieza, movimiento);
                    if (aux.isGameOver()) {
                        if (aux.GetWinner() == this.nuestroCell) {
                            return Double.NEGATIVE_INFINITY;
                        }
                        else {
                            return Double.POSITIVE_INFINITY;
                        }
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
