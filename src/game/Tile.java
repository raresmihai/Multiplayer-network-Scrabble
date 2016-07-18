package game;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Tile implements Serializable{
    char letter;
    int value;

    public Tile(char letter, int value) {
        this.letter = letter;
        this.value = value;
    }

    public char getLetter() {
        return letter;
    }

    public int getValue() {
        return value;
    }
}
