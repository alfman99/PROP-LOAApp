package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SrJuanV2 implements IPlayer, IAuto {

    private final SearchType executionType;
    private CellType nuestroCell;
    private CellType enemigoCell;
    private final int profundidadMax;
    private boolean timeout;
    private HashMap<QuadType, Integer> quadsMap;

    public SrJuanV2(SearchType minimax, int profundidadMax) {
        this.executionType = minimax;
        this.nuestroCell = CellType.EMPTY;
        this.enemigoCell = CellType.EMPTY;
        this.profundidadMax = profundidadMax;
        this.timeout = false;
        this.quadsMap = new HashMap<>();
    }

    @Override
    public Move move(GameStatus gs) {
        this.timeout = false;
        if (this.nuestroCell == CellType.EMPTY || this.enemigoCell == CellType.EMPTY) {
            this.nuestroCell = gs.getCurrentPlayer();
            this.enemigoCell = CellType.opposite(this.nuestroCell);
        }
        Pair<Move, Double> test;
        switch (this.executionType) {
            case MINIMAX_IDS: {
                try {
                    test = obtenerMovimiento_IDS(gs);
                    return test.getFirst();
                } catch (RuntimeException ex) {
                    System.out.println(ex.getMessage());
                    Point pieza = gs.getPiece(this.enemigoCell, 0);
                    ArrayList<Point> movimiento = gs.getMoves(pieza);
                    return new Move(pieza, movimiento.get(0), 0, 0, SearchType.MINIMAX_IDS);
                }
            }
            default:
            case MINIMAX: {
                test = obtenerMovimiento(gs, this.profundidadMax);
                return test.getFirst();
            }
        }
    }

    private QuadType evalQuad(GameStatus gs, Point ini) {
        boolean[] type = {false, false, false, false};
        int fichas = 0;
        int count = 0;
        Point principio = ini;
        for (int i = principio.x; i < principio.x + 1; i++) {
            for (int j = principio.y; j < principio.y + 1; j++) {
                if (i >= 0 && i < 8 && j >= 0 && j < 8
                        && gs.getPos(i, j) == this.nuestroCell) {
                    type[count] = true;
                    fichas++;
                }
                count++;
            }
        }
        switch (fichas) {
            case 1: {
                return QuadType.Q1;
            }
            case 2: {
                if ((type[0] == true && type[3] == true) || (type[1] == true && type[2] == true)) {
                    return QuadType.Qd;
                }
                else {
                    return QuadType.Q2;
                }
            }
            case 3: {
                return QuadType.Q3;
            }
            case 4: {
                return QuadType.Q4;
            }
            default: {
                return QuadType.Q1;
            }
        }
    }

    private void getQuads(GameStatus gs, Point act) {
        for (int i = -1; i <= 0; i++) {
            for (int j = -1; j <= 0; j++) {
                Point ini = new Point(act.x + i, act.y + j);
                QuadType eval = evalQuad(gs, ini);
                if (this.quadsMap.containsKey(eval)) {
                    this.quadsMap.put(eval, (this.quadsMap.get(eval)) + 1);
                } else {
                    this.quadsMap.put(eval, 1);
                }
            }
        }
    }

    private double distanceToCenter(GameStatus gs, Point act) {
        double xCenter = (gs.getSize() / 2.0F);
        double yCenter = (gs.getSize() / 2.0F);
        double x = act.x;
        double y = act.y;
        double xDist = Math.abs(xCenter - x);
        double yDist = Math.abs(yCenter - y);
        return Math.sqrt(Math.pow(xDist, 2.0D) + Math.pow(yDist, 2.0D));
    }

    private double evalTablero(GameStatus gs) {
        this.quadsMap = new HashMap<>();
        double heurVal = 0.0D;
        int numPiezas = gs.getNumberOfPiecesPerColor(this.nuestroCell);
        double distanceCenterTotal = 0.0D;
        for (int i = 0; i < numPiezas; i++) {
            Point pieza = gs.getPiece(this.nuestroCell, i);
            distanceCenterTotal += distanceToCenter(gs, pieza);
            getQuads(gs, pieza);
            for (Map.Entry<QuadType, Integer> entry : this.quadsMap.entrySet()) {
                switch (entry.getKey()) {
                    case Q1:
                        heurVal += entry.getValue();
                        continue;
                    case Q3:
                        heurVal -= entry.getValue();
                        continue;
                    case Qd:
                        heurVal -= 2 * entry.getValue();
                        continue;
                }
            }
        }
        
        return (heurVal / 4.0D) / (distanceCenterTotal / numPiezas);
    }

    private Pair<Move, Double> obtenerMovimiento_IDS(GameStatus gs) {
        int i = 1;
        Pair<Move, Double> mejorMov = null;
        while (!this.timeout) {
            Pair<Move, Double> aux;
            try {
                aux = obtenerMovimiento(gs, i);
                mejorMov = aux;
            } catch (RuntimeException ex) {
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

    private Pair<Move, Double> obtenerMovimiento(GameStatus gs, int profundidadMaxima) {
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
                if (this.timeout && this.executionType == SearchType.MINIMAX_IDS) {
                    throw new RuntimeException("Timeout obtenerMov");
                }
                try {
                    alfa = minimax(aux, movimientoActual, profundidadMaxima - 1, mejorHeur, Double.POSITIVE_INFINITY, false);
                } catch (RuntimeException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
                if (alfa > mejorHeur || mejorMovimiento == null) {
                    mejorMovimiento = movimientoActual;
                    mejorHeur = alfa;
                }
            }
        }
        mejorMovimiento.setMaxDepthReached(this.profundidadMax);
        return new Pair(mejorMovimiento, mejorHeur);
    }

    private double minimax(GameStatus gs, Move movPrincipalActual, int profundidad, Double alfa, Double beta, boolean isMax) {
        if (this.timeout && this.executionType == SearchType.MINIMAX_IDS) {
            throw new RuntimeException("Timeout minimax");
        }
        if (profundidad <= 0) {
            movPrincipalActual.setNumerOfNodesExplored(movPrincipalActual.getNumerOfNodesExplored() + 1L);
            return evalTablero(gs);
        }
        if (isMax) {
            double nuevaAlfa = Double.NEGATIVE_INFINITY;
            int j = gs.getNumberOfPiecesPerColor(this.nuestroCell);
            for (int k = 0; k < j; k++) {
                Point pieza = gs.getPiece(this.nuestroCell, k);
                ArrayList<Point> movimientos = gs.getMoves(pieza);
                for (Point movimiento : movimientos) {
                    GameStatus aux = new GameStatus(gs);
                    aux.movePiece(pieza, movimiento);
                    if (aux.isGameOver()) {
                        if (aux.GetWinner() == this.nuestroCell) {
                            return Double.POSITIVE_INFINITY;
                        }
                        return Double.NEGATIVE_INFINITY;
                    }
                    nuevaAlfa = Math.max(nuevaAlfa, minimax(aux, movPrincipalActual, profundidad - 1, alfa, beta, false));
                    alfa = Math.max(nuevaAlfa, alfa);
                    if (alfa >= beta) {
                        return alfa;
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
                for (Point movimiento : movimientos) {
                    GameStatus aux = new GameStatus(gs);
                    aux.movePiece(pieza, movimiento);
                    if (aux.isGameOver()) {
                        if (aux.GetWinner() == this.nuestroCell) {
                            return Double.NEGATIVE_INFINITY;
                        }
                        return Double.POSITIVE_INFINITY;
                    }
                    nuevaBeta = Math.min(nuevaBeta, minimax(aux, movPrincipalActual, profundidad - 1, alfa, beta, true));
                    beta = Math.min(nuevaBeta, beta);
                    if (alfa >= beta) {
                        return beta;
                    }
                }
            }
            return nuevaBeta;
        }
    }

    public void timeout() {
        this.timeout = true;
    }

    public String getName() {
        return "Sr Juan";
    }
}
