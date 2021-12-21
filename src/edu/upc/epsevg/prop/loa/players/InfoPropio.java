/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;

/**
 *
 * @author alehe
 */
public class InfoPropio {
    private int nodesExplored;
    private final int depth;
    private final double heur;

    public int getNumerOfNodesExplored() {
        return nodesExplored;
    }

    public void setNumerOfNodesExplored(int nodesExplored) {
        this.nodesExplored = nodesExplored;
    }

    public int getMaxDepthReached() {
        return depth;
    }

    
    public InfoPropio(int nodesExplored, int depth, double heur) {
        this.nodesExplored = nodesExplored;
        this.depth = depth;
        this.heur = heur;
    }
}

