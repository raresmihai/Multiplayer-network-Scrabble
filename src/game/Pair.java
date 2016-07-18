package game;

public class Pair {
    public Tile tile;
    public int remaining;


    public Pair(Tile tile, int distribution) {
        this.tile = tile;
        this.remaining = distribution;
    }
}
