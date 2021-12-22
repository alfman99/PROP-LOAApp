package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
import edu.upc.epsevg.prop.loa.ZobristHashingPropio;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class SrJuanIDS implements IPlayer, IAuto {

    /**
     * Tipo de Cell que tiene nuestro jugador
     */
    private CellType nuestroCell;
    
    /**
     * Tipo de Cell que tiene el jugador enemigo
     */
    private CellType enemigoCell;
    
    /**
     * Indicador que nos hace salir del calculo del IDS una vez es igual a true
     */
    private boolean timeout;
    
    // private HashMap<QuadType, Integer> quadsMap;
    
    /**
     * Numero total de nodos explorados en el calculo del minimax en un movimiento
     */
    private int totalNodes;
    
    /**
     * Logica de todo el hashing
     */
    private final ZobristHashingPropio hashing;
    
    /**
     * Tipo de busqueda que estamos usando:
     *  Minimax
     *  Minimax_IDS
     */
    private final SearchType tipoBusqueda;
    
    /**
     * Profundidad maxima que queremos alcanzar en el tipo de busqueda Minimax
     */
    private final int profundiadMax;
    
    // Cositas extra
    /**
     * Activar o desactivar el hashing
     */
    private final boolean hashingActivado;
    
    /**
     * Activar o desactivar el output de las estadisticas
     */
    private final boolean estadisticas;
    
    /**
     * Activar o desactivar el output de las estadisticas del tiempo
     */
    private final boolean estadisticasTiempo;
    
    /**
     * Variable que se utilizar para almacenar el tiempo de la ejecución del turno en caso
     * de tener las estadisticasTiempo activadas
     */
    private long timeTurn;
    
    /**
     * Cuenta en que tirada estamos encaso de tener las estadisticas activadas
     */
    private int estadisticaTirada; // Para hacer las estadisticas

        
    public SrJuanIDS(SearchType tipo, int profundidad) {
                
        this.tipoBusqueda = tipo;
        this.profundiadMax = profundidad;
        this.timeTurn = 0;
        
        this.nuestroCell = CellType.EMPTY;
        this.enemigoCell = CellType.EMPTY;
        this.timeout = false;
        // this.quadsMap = new HashMap<>();
        this.totalNodes = 0;
        this.hashing = new ZobristHashingPropio(8); // No podemos acceder a la mida del tablero desde el constructor.
        
        // Cositas extra
        this.hashingActivado = true;
        this.estadisticas = false;
        this.estadisticaTirada = 1;
        this.estadisticasTiempo = true;
        
        if (this.estadisticas) {
            // Ruta donde quieres guardar las estadisticas, cambiar.
            String name = "C:\\Users\\srimp\\Documents\\Universidad\\PROP\\Actividades\\PROP-LOAApp\\estadisticas\\sin hashing 1 segundo\\partida10H.txt";
            try {
                File tmp = new File(name);
                tmp.createNewFile();
                PrintStream out = new PrintStream(new FileOutputStream(name));
                System.setOut(out);
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public Move move(GameStatus gs) {
                        
        this.timeout = false;
        this.totalNodes = 0;

        if (this.nuestroCell == CellType.EMPTY || this.enemigoCell == CellType.EMPTY) {
            this.nuestroCell = gs.getCurrentPlayer();
            this.enemigoCell = CellType.opposite(this.nuestroCell);
        }
                
        if (this.estadisticasTiempo) this.timeTurn = System.nanoTime();
        
        Move test = null;
        switch(this.tipoBusqueda) {
            case MINIMAX: {
                test = obtenerMovimiento(gs, this.profundiadMax).getFirst();
                if (this.estadisticas) this.estadisticaTirada++; // Para hacer las estadisticas
                break;
            }
            default:
            case MINIMAX_IDS: {
                try {
                    test = obtenerMovimiento_IDS(gs).getFirst();
                    if (this.estadisticas) this.estadisticaTirada++; // Para hacer las estadisticas
                } catch (RuntimeException ex) {
                    System.out.println(ex.getMessage());
                    System.exit(-1);
                }
                break;
            }
        }
        
        if (this.estadisticasTiempo) {
            this.timeTurn = System.nanoTime() - this.timeTurn;
            System.out.println("Tiempo en la tirada: " + (this.timeTurn / 1000000) + "ms");
        }
        
        return test;
    }

    /*
    private QuadType evalQuad(GameStatus gs, Point ini, CellType jugador) {
        boolean[] type = {false, false, false, false};
        int fichas = 0;
        int count = 0;
        Point principio = ini;
        for (int i = principio.x; i < principio.x + 1; i++) {
            for (int j = principio.y; j < principio.y + 1; j++) {
                if (i >= 0 && i < 8 && j >= 0 && j < 8
                        && gs.getPos(i, j) == jugador) {
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
                } else {
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
    */

    /*private void getQuads(GameStatus gs, Point act, CellType jugador) {
        for (int i = -1; i <= 0; i++) {
            for (int j = -1; j <= 0; j++) {
                Point ini = new Point(act.x + i, act.y + j);
                QuadType eval = evalQuad(gs, ini, jugador);
                if (this.quadsMap.containsKey(eval)) {
                    this.quadsMap.put(eval, (this.quadsMap.get(eval)) + 1);
                } else {
                    this.quadsMap.put(eval, 1);
                }
            }
        }
    }*/

    /**
     * Distancia de un punto al centro del tablero
     * @param gs Estado del juego
     * @param act punto del que queremos saber la distancia al centro
     * @return Distancia al centro del tablero de la pieza act
     */
    private double distanceToCenter(GameStatus gs, Point act) {
        double xCenter = (gs.getSize() / 2.0F);
        double yCenter = (gs.getSize() / 2.0F);
        double x = act.x;
        double y = act.y;
        double xDist = Math.abs(xCenter - x);
        double yDist = Math.abs(yCenter - y);
        return Math.sqrt(Math.pow(xDist, 2.0D) + Math.pow(yDist, 2.0D));
    }

    /**
     * Funcion que nos da el valor total del tablero visto desde nuestro jugador
     * @param gs Estado del juego
     * @return Valor heuristico del valor del tablero segun los intereses del this.nuestroCell
     */
    private double evalTablero(GameStatus gs) {
        double heurEnemigo = calcEvalTablero(gs, this.enemigoCell);
        double heurNuestro = calcEvalTablero(gs, this.nuestroCell);
                        
        return heurNuestro - heurEnemigo;
    }

    /**
     * Devuelve el numero de componentes en la tabla
     * @param gs Estado del juego
     * @param cell Tipo de casilla que queremos ver cuantas componentes hay
     * @return numero de componentes en el tablero
     */
    private int numberOfComponentsOnBoard(GameStatus gs, CellType cell) {
      CellType[][] board = new CellType[gs.getSize()][gs.getSize()];
      for (int i = 0; i < gs.getSize(); i++) {
        for (int j = 0; j < gs.getSize(); j++) {
          board[i][j] = gs.getPos(i, j);
        }
      }
      int count = 0;
      for (int i = 0; i < gs.getSize(); i++) {
        for (int j = 0; j < gs.getSize(); j++) {
          if (board[i][j] == cell) {
            // remove cells from board using flood fill
            floodFill(board, i, j, cell);
            count++;
          }
        }
      }
      return count;
    }

    /**
     * @param board Estado del tablero
     * @param x posición X que estamos evaluando
     * @param y posición Y que estamos evaluando
     * @param cell Tipo de cell por el que estamos mirando
     */
    private void floodFill(CellType[][] board, int x, int y, CellType cell) {
      if (x < 0 || x >= board.length || y < 0 || y >= board.length) {
        return;
      }
      if (board[x][y] == cell) {
        board[x][y] = CellType.EMPTY;
        floodFill(board, x - 1, y, cell);
        floodFill(board, x + 1, y, cell);
        floodFill(board, x, y - 1, cell);
        floodFill(board, x, y + 1, cell);
        floodFill(board, x - 1, y - 1, cell);
        floodFill(board, x + 1, y + 1, cell);
        floodFill(board, x - 1, y + 1, cell);
        floodFill(board, x + 1, y - 1, cell);
      }
    }

    /**
     * Utilizando el numero de componentes y la distancia al centro del tablero devuelve el valor heuristico del tablero
     * @param gs Estado del juego
     * @param jugador Perspectiva del jugador del que queremos calcular la heuristica
     * @return Valor heuristico del tablero
     */
    private double calcEvalTablero(GameStatus gs, CellType jugador) {
        // this.quadsMap = new HashMap<>();
        // double heurVal = 0.0D;
        int numPiezas = gs.getNumberOfPiecesPerColor(jugador);
        double distanceCenterTotal = 0.0D;
        for (int i = 0; i < numPiezas; i++) {
            Point pieza = gs.getPiece(jugador, i);
            distanceCenterTotal += distanceToCenter(gs, pieza);
            /*getQuads(gs, pieza, jugador);
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
            }*/
        }
        
        int numComponentes = this.numberOfComponentsOnBoard(gs, jugador);
        
        return - distanceCenterTotal - numComponentes;
        // return -heurVal/4.0D;
    }

    /**
     * Mientras que no haya un timeout estará iterando sobre el obtenerMovimiento incrementando la profundidad y
     * quedandose con el movimiento del ultimo nivel que haya completado, una vez haya un timeout dejará el calculo del 
     * nivel en el que está trabajando a medias y utilizará el valor del ultimo nivel completado en su totalidad.
     * @param gs Estado del juego actual 
     * @return Pair contenido con el mejor movimiento y el valor de la heuristica asociado al movimiento
     */
    private Pair<Move, Double> obtenerMovimiento_IDS(GameStatus gs) {
        int i = 1;
        Pair<Move, Double> mejorMov = null;
        while (!this.timeout) {
            Pair<Move, Double> aux;
            try {
                aux = obtenerMovimiento(gs, i);
                mejorMov = aux;
            } catch (RuntimeException ex) {
                // System.out.println(ex.getMessage());
            }
            mejorMov.getFirst().setNumerOfNodesExplored(this.totalNodes);
            mejorMov.getFirst().setMaxDepthReached(i);
            i++;
        }
        if (mejorMov == null) {
            throw new RuntimeException("No se ha completado ni el primer nivel IDS");
        }
        if (this.estadisticas) System.out.println(this.estadisticaTirada + ":" + this.totalNodes + ":" + mejorMov.getFirst().getMaxDepthReached()); // Para hacer las estadisticas
        // System.out.println("heur: " + mejorMov.getSecond()); // Debug stuff
        return mejorMov;
    }

    /**
     * Devuelve el mejor movimiento que podríamos hacer dada una profundidad maxima según un minimax y una heuristica
     * @param gs Estado del juego actual
     * @param profundidadMaxima Profundidad a la que queremos llegar como maximo
     * @return Pair contenido con el mejor movimiento y el valor de la heuristica asociado al movimiento
     */
    private Pair<Move, Double> obtenerMovimiento(GameStatus gs, int profundidadMaxima) {
        double mejorHeur = Double.NEGATIVE_INFINITY;
        Move mejorMovimiento = null;
        int piezasRestantes = gs.getNumberOfPiecesPerColor(this.nuestroCell);
        for (int i = 0; i < piezasRestantes; i++) {
            Point pieza = gs.getPiece(this.nuestroCell, i);
            ArrayList<Point> movimientos = gs.getMoves(pieza);
            for (Point movimiento : movimientos) {
                Move movimientoActual = new Move(pieza, movimiento, 0, 0, SearchType.MINIMAX_IDS);
                double alfa = Double.NEGATIVE_INFINITY;
                GameStatus aux = new GameStatus(gs);
                aux.movePiece(pieza, movimiento);
                if (aux.isGameOver()) {
                    if (aux.GetWinner() == this.nuestroCell) {
                        return new Pair<>(movimientoActual, Double.POSITIVE_INFINITY);
                    }
                    else {
                        continue;
                    }
                }
                if (this.timeout && this.tipoBusqueda.equals(SearchType.MINIMAX_IDS)) {
                    throw new RuntimeException("Timeout obtenerMov");
                }
                try {
                    alfa = minimax(aux, profundidadMaxima - 1, mejorHeur, Double.POSITIVE_INFINITY, false);
                } catch (RuntimeException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
                if (alfa > mejorHeur || mejorMovimiento == null) {
                    mejorMovimiento = movimientoActual;
                    mejorHeur = alfa;
                }
            }
        }
        
        return new Pair(mejorMovimiento, mejorHeur);
    }

    /**
     * Interfaz para poder asignarle una función y hacer el minimax todo en uno sin necesidad de usar el negamax
     */
    interface MinOMax {
        double minOMax(double a, double b);
    }
    
    /**
     * Metodo que realiza el desarrollo del árbol heurístico MiniMax de forma 
     * recursiva. Comprueva el valor máximo si isMax es verdadero y en caso
     * contrario mira el valor mínimo. Este lo realiza con la poda, para así 
     * poder evitar cercar ramas innecesarias del árbol. 
     * @param gs Estado del juego actual en la simulación
     * @param profundidad Profundidad actual de la simulación, utilizado para hacer cutoff
     * @param alfa Valor alfa para hacer la poda alfa beta
     * @param beta Valor beta para hacer la poda alfa beta
     * @param isMax Para saber si está en una altura donde hay que minimizar o maximizar
     * @return Valor de la heuristica en el nodo hoja de esa simulación minimax
     */
    private double minimax(GameStatus gs, int profundidad, Double alfa, Double beta, boolean isMax) {
                
        MinOMax func;
        
        if (this.timeout && this.tipoBusqueda.equals(SearchType.MINIMAX_IDS)) {
            throw new RuntimeException("Timeout minimax");
        }
        if (profundidad <= 0) {
            this.totalNodes++;
            double tmp = evalTablero(gs);
            if (hashingActivado) this.hashing.update(gs, tmp, profundidad);
            return tmp;
        }
        
        double nuevoValor;
        int j;
        
        if (isMax) {
            func = Math::max;
            j = gs.getNumberOfPiecesPerColor(this.nuestroCell);
            nuevoValor = Double.NEGATIVE_INFINITY;
        }
        else {
            func = Math::min;
            j = gs.getNumberOfPiecesPerColor(this.enemigoCell);
            nuevoValor = Double.POSITIVE_INFINITY;
        }
        
        
        for (int k = 0; k < j; k++) {
            Point pieza;
            if (isMax) {
                pieza = gs.getPiece(this.nuestroCell, k);
            }
            else {
                pieza = gs.getPiece(this.enemigoCell, k);
            }
            ArrayList<Point> movimientos = gs.getMoves(pieza);
            for (Point movimiento : movimientos) {
                GameStatus aux = new GameStatus(gs);
                aux.movePiece(pieza, movimiento);
                if (hashingActivado) {
                    var eval = this.hashing.evaluate(gs, profundidad);
                    if (eval != null) {
                        // System.out.println("Zobrist triggered");
                        this.totalNodes++;
                        return eval.heur;
                    }
                }
                if (aux.isGameOver()) {
                    if (aux.GetWinner() == this.nuestroCell) {
                        return Double.POSITIVE_INFINITY;
                    }
                    return Double.NEGATIVE_INFINITY;
                }
                nuevoValor = func.minOMax(nuevoValor, minimax(aux, profundidad - 1, alfa, beta, !isMax));
                
                if (isMax) {
                    alfa = func.minOMax(nuevoValor, alfa);
                    if (nuevoValor >= beta) {
                        if (this.hashingActivado) this.hashing.update(gs, alfa, profundidad);
                        return alfa;
                    }
                }
                else {
                    beta = func.minOMax(nuevoValor, beta);
                    if (alfa >= nuevoValor) {
                        if (this.hashingActivado) this.hashing.update(gs, beta, profundidad);
                        return beta;
                    }
                }
            }
        }
        if (this.hashingActivado) this.hashing.update(gs, nuevoValor, profundidad);
        return nuevoValor;
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