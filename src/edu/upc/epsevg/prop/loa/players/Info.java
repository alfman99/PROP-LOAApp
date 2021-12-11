/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa.players;

/**
 *
 * @author alehe
 */
public class Info {
    private int nodesExplored;
    private int depth;
    private double heur;

    public int getNumerOfNodesExplored() {
        return nodesExplored;
    }

    public void setNumerOfNodesExplored(int nodesExplored) {
        this.nodesExplored = nodesExplored;
    }

    public int getMaxDepthReached() {
        return depth;
    }

    public void setMaxDepthReached(int depth) {
        this.depth = depth;
    }

    public double getHeur() {
        return heur;
    }

    public void setHeur(double heur) {
        this.heur = heur;
    }

    public Info(int nodesExplored, int depth, double heur) {
        this.nodesExplored = nodesExplored;
        this.depth = depth;
        this.heur = heur;
    }
}

