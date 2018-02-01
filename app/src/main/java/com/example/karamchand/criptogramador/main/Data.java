package com.example.karamchand.criptogramador.main;

import com.example.karamchand.criptogramador.LettersView;
import com.example.karamchand.criptogramador.PhraseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Data {
    private HashMap<Character, Integer> counter;
    private int sum;
    private int validSum;
    private int vocales;
    private int validWords;

    public Data(ArrayList<String> state) {
        counter = new HashMap<>();
        for (String s : state) {
            processString(s);
            if (s.length() > 1) {
                validWords++;
                validSum += s.length();
            }
        }
    }

    public Data(String s) {
        counter = new HashMap<>();
        processString(s);
    }

    private void processString(String s) {
        s = PhraseActivity.toAlpha(s);
        for (char c : s.toCharArray()) {
            if (counter.keySet().contains(c))
                counter.put(c, counter.get(c) + 1);
            else
                counter.put(c, 1);
            if (LettersView.VOCALES.contains(Character.toString(c)))
                vocales++;
            sum++;
        }
    }

    public int getCount(char c) {
        if (counter.containsKey(c))
            return counter.get(c);
        else
            return 0;
    }

    public int getCount() {
        return sum;
    }

    public int getVowels() {
        return vocales;
    }

    public int getValidWords() {
        return validWords;
    }

    public int getValidSum() {
        return validSum;
    }

    public boolean isFinished(String mPhrase) {
        List<String> phrase = Arrays.asList(mPhrase.split(""));
        for (char c : LettersView.ALPHABET.toCharArray()) {
            if (getCount(c) != Collections.frequency(phrase, Character.toString(c))) return false;
        }
        return true;
    }
}
