package com.example.karamchand.criptogramador;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.example.karamchand.criptogramador.LettersView.ALPHABET;

public class PrintActivity extends AppCompatActivity {

    private String mPhrase;
    private ArrayList<String> mWords;
    private HashMap<Character, ArrayList<Integer>> mIndexes;
    private HashMap<Integer, Character> mLettersForPhrasePos;

    private String lettersBuffer = "";
    private String phrasebuffer = "";
    private String mPhraseAlpha;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        mPhrase = PhraseActivity.toAlpha(getIntent().getStringExtra("phrase").toLowerCase(), true);
        mPhraseAlpha = mPhrase.replace(" ", "");
        mWords = (ArrayList<String>) getIntent().getExtras().get("words");

        generate();
    }

    public void generate() {

        mIndexes = new HashMap<>();
        mLettersForPhrasePos = new HashMap<>();

        for (int i = 0; i < mPhraseAlpha.length(); i++) {
            char c = mPhraseAlpha.charAt(i);
            if (mIndexes.containsKey(c)) {
                ArrayList<Integer> oldArray = mIndexes.get(c);
                oldArray.add(i);
                Collections.shuffle(oldArray);
                mIndexes.put(c, oldArray);
            } else {
                ArrayList<Integer> array = new ArrayList<>();
                array.add(i);
                mIndexes.put(c, array);
            }
        }

        for (String word : mWords) {
            for (char c : word.toCharArray()) {
                int index = mIndexes.get(c).remove(0);
                lettersBuffer += " \t" + Integer.toString(index + 1) + "\t";
                mLettersForPhrasePos.put(index, ALPHABET.toUpperCase().charAt(mWords.indexOf(word)));
            }
            lettersBuffer += "\n\n";
        }

        int letterPosition = 0;
        for (int i = 0; i < mPhrase.length(); i++) {
            if (mPhrase.charAt(i) == ' ') {
                phrasebuffer += " \t \t";
            } else {
                phrasebuffer += mLettersForPhrasePos.get(letterPosition) + "\t";
                letterPosition++;
                phrasebuffer += letterPosition + "\t";
            }
            if ((i + 1) % 20 == 0) phrasebuffer += "\n\n";
        }

        ((TextView) findViewById(R.id.print_words)).setText(lettersBuffer);
        ((TextView) findViewById(R.id.print_phrase)).setText(phrasebuffer);
    }

}
