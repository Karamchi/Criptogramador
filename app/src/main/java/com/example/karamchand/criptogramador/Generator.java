package com.example.karamchand.criptogramador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.example.karamchand.criptogramador.builder.LettersView.ALPHABET;

public class Generator {

    public ArrayList<ArrayList<Integer>> mLettersState = new ArrayList<>();
    public ArrayList<Character> mCellLetters = new ArrayList<>();
    public ArrayList<Integer> mCellNumbers = new ArrayList<>();
    public HashMap<Integer, Character> mPunctuation = new HashMap<>();
    public String mFileId;
    public int mSolution;

    private HashMap<Character, ArrayList<Integer>> indexes = new HashMap<>();
    private HashMap<Integer, Character> lettersForPhrasePos = new HashMap<>();

    private String mPhrase;
    private String mPhraseAlpha;
    private final ArrayList<String> mWords;

    public Generator(String phrase, ArrayList<String> words) {
        mPhrase = phrase.toLowerCase();
        this.mWords = words;
        for (int i = 0; i < mWords.size(); i++)
            mLettersState.add(new ArrayList<Integer>());
    }

    public void generate() {
        buildPunctuation(mPhrase);
        mPhrase = PhraseActivity.toAlpha(mPhrase, true);
        mPhraseAlpha = mPhrase.replace(" ", "");
        buildIndexes();
        buildWords();
        buildPhrase();
        mFileId =  mPhrase.subSequence(0, Math.min(8, mPhrase.length())).toString().replace(" ", "_");
        mSolution = mPhraseAlpha.hashCode();
    }

    private void buildPunctuation(String phrase) {
        int letterCount = 0;
        for (char c : phrase.toCharArray()) {
            if ((ALPHABET + "áéíóúü").contains(Character.toString(c)))
                letterCount++;
            else if (c != ' ')
                mPunctuation.put(letterCount, c);
        }
    }

    private void buildIndexes() {
        for (int i = 0; i < mPhraseAlpha.length(); i++) {
            char c = mPhraseAlpha.charAt(i);
            if (indexes.containsKey(c)) {
                ArrayList<Integer> oldArray = indexes.get(c);
                oldArray.add(i);
                Collections.shuffle(oldArray);
                indexes.put(c, oldArray);
            } else {
                ArrayList<Integer> array = new ArrayList<>();
                array.add(i);
                indexes.put(c, array);
            }
        }
    }

    private void buildWords() {
        for (int i = 0; i < mWords.size(); i++) {
            String word = mWords.get(i);
            for (char c : word.toCharArray()) {
                int index = indexes.get(c).remove(0);
                mLettersState.get(i).add(index + 1);
                lettersForPhrasePos.put(index, (ALPHABET.toUpperCase() + ALPHABET).charAt(mWords.indexOf(word)));
            }
        }
    }

    private void buildPhrase() {
        int letterPosition = 0;
        for (int i = 0; i < mPhrase.length(); i++) {
            if (mPhrase.charAt(i) == ' ') {
                mCellLetters.add(' ');
                mCellNumbers.add(0);
            } else {
                mCellLetters.add(lettersForPhrasePos.get(letterPosition));
                letterPosition++;
                mCellNumbers.add(letterPosition);
            }
        }
    }



}
