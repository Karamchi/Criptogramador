package com.example.karamchand.criptogramador;

public class CellData {
    public char letter;
    public int number;
    public char punctuation;

    public CellData() {}

    public CellData(char c, int i) {
        letter = c;
        number = i;
    }

    public CellData(char c, int i, Character p) {
        letter = c;
        number = i;
        if (p != null)
            punctuation = p;
    }

}
