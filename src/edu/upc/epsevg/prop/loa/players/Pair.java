package edu.upc.epsevg.prop.loa.players;

class Pair<U, V> {

    private final U first;
    private final V second;

    // Constructs a new pair with specified values
    Pair(U first, V second) {
        this.first = first;
        this.second = second;
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
