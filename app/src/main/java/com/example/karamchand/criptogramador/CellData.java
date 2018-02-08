package com.example.karamchand.criptogramador;

public class CellData {
    public char letter;
    public int number;
    public char punctuation;
    public CellData twin;

    public CellData() {

    }

    public CellData(char c, int i) {
        letter = c;
        number = i;
    }

    public void setPunctuation(Character punctuation) {
        this.punctuation = punctuation;
    }

    public void setTwin(CellData twin) {
        this.twin = twin;
    }

}
