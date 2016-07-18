package game;

import server.RoomThread;

import java.util.*;

public class TilesBag {
    private boolean available;
    private char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private int[] values = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};
    private int[] distribution = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
    private List<Pair> bag = new ArrayList<>();
    private Random randomGenerator = new Random();
    private int remainingLetters = 100;
    RoomThread roomManager;
    
    private void initialize() {
        for (int i = 0; i < distribution.length; ++i) {
            Tile tile = new Tile(letters[i], values[i]);
            bag.add(new Pair(tile, distribution[i]));
        }
        available = true; //the bag containing the tiles were produced
    }

    public TilesBag(RoomThread roomThread) {
    	this.roomManager=roomThread;
        initialize();
    }

    public synchronized List<Tile> getTilesFromBag(int k) {

        while(!available) { //while some other player is getting tiles from the bag - wait
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<Tile> tiles = new ArrayList<>();
        available = false; //start using the bag
        notifyAll();
        if(k>remainingLetters) {
            roomManager.setGameOn(false);
        }
        else{
        	  int count = 0;
        	  try {
        		  while (count < k) { //get k tiles from the bag randomly
                      int index = randomGenerator.nextInt(bag.size());
                      tiles.add(bag.get(index).tile);
                      bag.get(index).remaining--;
                      remainingLetters--;
                      if (bag.get(index).remaining == 0) {
                          bag.remove(index);
                      }
                      count++;
                	  if (remainingLetters==0){
                		  roomManager.setGameOn(false);
                	  }
                  }
        	  } catch (Exception e) {
				roomManager.setGameOn(false);
        	  }
        }
        available = true; //free the bag
        notifyAll();
        return tiles;
    }
}
